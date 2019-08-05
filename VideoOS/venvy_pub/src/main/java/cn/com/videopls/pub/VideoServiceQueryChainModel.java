package cn.com.videopls.pub;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.venvy.App;
import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyGzipUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视联网模式投放信息查询
 */

public class VideoServiceQueryChainModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_CHAIN_URL_MOCK = Config.HOST_VIDEO_OS
            + "/vision/getLabelConf";
    private static final String MOCK = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/getLabelConf#!method=POST&queryParameters=%5B%5D&body=&headers=%5B%5D";
    private static final String LUA_ZIP = "/lua/os/chain.zip";
    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private ServiceQueryChainCallback mQueryChainCallback;
    private VideoPlusLuaUpdate mDownLuaUpdate;
    private VideoPlusZipUpdate mDownZipUpdate;
    private Map<String, String> mQueryAdsParams;

    public VideoServiceQueryChainModel(Platform platform, Map<String, String> params,
                                       VideoServiceQueryChainModel.ServiceQueryChainCallback callback) {
        super(platform);
        this.mQueryChainCallback = callback;
        this.mQueryAdsParams = params;
    }

    private VideoServiceQueryChainModel.ServiceQueryChainCallback getQueryChainCallback() {
        return mQueryChainCallback;
    }

    private Map<String, String> getQueryChainParams() {
        return mQueryAdsParams;
    }

    @Override
    public boolean needCheckResponseValid() {
        return false;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (!response.isSuccess()) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data error"));
                        }
                        return;
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    String decrypt = VenvyAesUtil.decrypt(encryptData,
                            AppSecret.getAppSecret(getPlatform()),
                            AppSecret.getAppSecret(getPlatform()));
                    JSONObject obj = new JSONObject(decrypt);
                    final String template = obj.optString("template");
                    final JSONArray dataJsonArray = obj.optJSONArray("jsonList");
                    final JSONArray luaJsonArray = obj.optJSONArray("luaList");

                    if (TextUtils.isEmpty(template)) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query chain data with template is " +
                                    "null"));
                        }
                        return;
                    }
                    if (dataJsonArray == null || dataJsonArray.length() <= 0) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query chain data with jsonList is " +
                                    "null"));
                        }
                        return;
                    }
                    if (luaJsonArray == null || luaJsonArray.length() <= 0) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query chain data with luaList is " +
                                    "null"));
                        }
                        return;
                    }
                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new VideoPlusLuaUpdate(getPlatform(), new
                                VideoPlusLuaUpdate.CacheLuaUpdateCallback() {
                                    @Override
                                    public void updateComplete(boolean isUpdateByNetWork) {
                                        VenvyUIUtil.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mDownZipUpdate.startDownloadZipFile(dataJsonArray);
                                            }
                                        });
                                    }

                                    @Override
                                    public void updateError(Throwable t) {
                                        ServiceQueryChainCallback callback = getQueryChainCallback();
                                        if (callback != null) {
                                            callback.queryError(new Exception("chain ads down lua" +
                                                    " failed"));
                                        }
                                    }
                                });
                    }
                    if (mDownZipUpdate == null) {
                        mDownZipUpdate = new VideoPlusZipUpdate(getPlatform(), new VideoPlusZipUpdate.CacheZipUpdateCallback() {
                            @Override
                            public void updateComplete(JSONArray zipJsonDataArray) {
                                Log.i("video++","=start=updateComplete====");
                                Map<String, String> params = getQueryChainParams();
                                String adsType = params != null ? params.get
                                        (VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
                                ServiceQueryAdsInfo queryAdsInfo =
                                        new ServiceQueryAdsInfo
                                                .Builder()
                                                .setQueryAdsTemplate(template)
                                                .setQueryAdsId(null)
                                                .setQueryAdsType(!TextUtils.isEmpty(adsType) ?
                                                        Integer.valueOf(adsType) : 0).build();
                                ServiceQueryChainCallback callback = getQueryChainCallback();
                                if (callback != null) {
                                    try {
                                        JSONObject jsonObject=new JSONObject();
                                        jsonObject.put("data",zipJsonDataArray);
                                        callback.queryComplete(jsonObject,
                                                queryAdsInfo);
                                        Log.i("video++","=start=完成22====");
                                    } catch (Exception e) {
                                        Log.i("video++","=start=error===="+e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void updateError(Throwable t) {
                                ServiceQueryChainCallback callback = getQueryChainCallback();
                                if (callback != null) {
                                    callback.queryError(new Exception("chain ads down lua" +
                                            " failed"));
                                }
                            }
                        });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(luaJsonArray);
                } catch (Exception e) {
                    VenvyLog.e(VideoServiceQueryChainModel.class.getName(), e);
                    ServiceQueryChainCallback callback = getQueryChainCallback();
                    if (callback != null) {
                        callback.queryError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoServiceQueryChainModel.class.getName(), e);
                ServiceQueryChainCallback callback = getQueryChainCallback();
                if (callback != null) {
                    callback.queryError(e);
                }
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(SERVICE_QUERYALL_CHAIN_URL_MOCK, createBody(mQueryAdsParams));
    }

    private Map<String, String> createBody(Map<String, String> params) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        Platform platform = getPlatform();
        if (platform != null) {
            PlatformInfo info = platform.getPlatformInfo();
            if (info != null) {
                String videoId = info.getVideoId();
                if (!TextUtils.isEmpty(videoId)) {
                    bodyParams.put("videoId", videoId);
                }
            }
        }
        if (params != null) {
            bodyParams.putAll(params);
        }
        HashMap<String, String> dataParams = new HashMap<>();
        dataParams.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()),
                AppSecret.getAppSecret(getPlatform()), new JSONObject(bodyParams).toString()));
        return dataParams;
    }

    private void startDownloadZipFile(JSONArray zipUrls, final String template) {
        if (zipUrls == null || zipUrls.length() <= 0) {
            ServiceQueryChainCallback callback = getQueryChainCallback();
            if (callback != null) {
                callback.queryError(new Exception("download url can't be null"));
            }
            return;
        }
        int len = zipUrls.length();
        ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            JSONObject obj = zipUrls.optJSONObject(i);
            String url = obj.optString("url");
            DownloadTask task = new DownloadTask(App.getContext(), obj.optString("url"), VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP + File.separator + Uri.parse(url).getLastPathSegment());
            arrayList.add(task);
        }
        mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        mDownloadTaskRunner.startTasks(arrayList, new TaskListener<DownloadTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                return false;
            }

            @Override
            public void onTaskStart(DownloadTask downloadTask) {

            }

            @Override
            public void onTaskProgress(DownloadTask downloadTask, int progress) {

            }

            @Override
            public void onTaskFailed(DownloadTask downloadTask, @Nullable Throwable throwable) {
                if (downloadTask != null) {
                    downloadTask.failed();
                }
            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {

            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                if (failedTasks != null && failedTasks.size() > 0) {
                    ServiceQueryChainCallback callback = getQueryChainCallback();
                    if (callback != null) {
                        callback.queryError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                final JSONArray queryArray = new JSONArray();
                for (DownloadTask downloadTask : successfulTasks) {
                    final String cacheUrlPath = downloadTask.getDownloadCacheUrl();
                    File hasDownFile = new File(downloadTask.getDownloadCacheUrl());
                    if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(cacheUrlPath))) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("update error, because downloadFile not find"));
                        }
                        return;
                    }
                    VenvyAsyncTaskUtil.doAsyncTask("unzip_lua", new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Boolean>() {

                        @Override
                        public Boolean doAsyncTask(Void... voids) throws Exception {
                            long value = VenvyGzipUtil.unzipFile(cacheUrlPath, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, false);
                            File file = new File(cacheUrlPath);
                            file.delete();
                            return value > 0;

                        }
                    }, new VenvyAsyncTaskUtil.CommonAsyncCallback<Boolean>() {
                        @Override
                        public void onPostExecute(Boolean aBoolean) {
                            if (!aBoolean) {
                                ServiceQueryChainCallback callback = getQueryChainCallback();
                                if (callback != null) {
                                    callback.queryError(new Exception("unzip error"));
                                }
                                return;
                            }
                            ServiceQueryChainCallback callback = getQueryChainCallback();
                            if (callback != null) {
                                String fileName = Uri.parse(cacheUrlPath).getLastPathSegment();
                                if (TextUtils.isEmpty(fileName)) {
                                    callback.queryError(new Exception(""));
                                    return;
                                }
                                File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName.replace(".zip", ".json"));
                                if (!file.exists() || !file.isFile()) {
                                    callback.queryError(new Exception(""));
                                    return;
                                }
                                String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), file.getAbsolutePath());
                                if (TextUtils.isEmpty(queryChainData)) {
                                    callback.queryError(new Exception(""));
                                    return;
                                }
                                Map<String, String> params = new HashMap<>();
                                params.put("data", queryChainData);
                                queryArray.put(new JSONObject(params));
                            }
                            Map<String, String> params = getQueryChainParams();
                            String adsType = params != null ? params.get
                                    (VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
                            ServiceQueryAdsInfo queryAdsInfo =
                                    new ServiceQueryAdsInfo
                                            .Builder()
                                            .setQueryAdsTemplate(template)
                                            .setQueryAdsId(null)
                                            .setQueryAdsType(!TextUtils.isEmpty(adsType) ?
                                                    Integer.valueOf(adsType) : 0).build();
                            callback.queryComplete(queryArray,
                                    queryAdsInfo);
                        }

                        @Override
                        public void onCancelled() {
                            ServiceQueryChainCallback callback = getQueryChainCallback();
                            if (callback != null) {
                                callback.queryError(new Exception("unzip error"));
                            }
                        }

                        @Override
                        public void onException(Exception ie) {

                        }
                    });
                }

            }
        });

    }

    public interface ServiceQueryChainCallback {
        void queryComplete(Object queryAdsData, ServiceQueryAdsInfo queryAdsInfo);

        void queryError(Throwable t);
    }

    public void destroy() {
        mQueryChainCallback = null;
        if (mQueryAdsParams != null) {
            mQueryAdsParams.clear();
        }
        VenvyAsyncTaskUtil.cancel(LUA_ZIP);
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        if (mDownZipUpdate != null) {
            mDownZipUpdate.destroy();
        }
        if (mDownLuaUpdate != null) {
            mDownLuaUpdate.destroy();
        }
    }
}
