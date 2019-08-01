package cn.com.videopls.pub;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.App;
import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.SingleDownloadListener;
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
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视联网模式投放信息查询
 */

public class VideoServiceQueryChainModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_CHAIN_URL_MOCK = Config.HOST_VIDEO_OS
            + "/api/queryAllAds";
    private static final String MOCK = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/getLabelConf#!method=POST&queryParameters=%5B%5D&body=&headers=%5B%5D";
    private static final String LUA_ZIP = "/lua/os/chain.zip";
    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private ServiceQueryChainCallback mQueryChainCallback;
    private VideoPlusLuaUpdate mDownLuaUpdate;
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
                    final JSONObject dataJsonObj = dataJsonArray.optJSONObject(0);
                    if (dataJsonObj == null) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query chain data with jsonList is " +
                                    "null"));
                        }
                        return;
                    }
                    final String dataJsonUrl = dataJsonObj.optString("url");
                    if (TextUtils.isEmpty(dataJsonUrl)) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query chain data with jsonList is " +
                                    "null"));
                        }
                        return;
                    }

                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new VideoPlusLuaUpdate(getPlatform(), new
                                VideoPlusLuaUpdate.CacheLuaUpdateCallback() {
                                    @Override
                                    public void updateComplete(boolean isUpdateByNetWork) {
                                        startDownloadZipFile(dataJsonUrl, template);
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
        return HttpRequest.post(MOCK, createBody(mQueryAdsParams));
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

    private void startDownloadZipFile(final String url, final String template) {
        if (TextUtils.isEmpty(url)) {
            ServiceQueryChainCallback callback = getQueryChainCallback();
            if (callback != null) {
                callback.queryError(new Exception("download url can't be null"));
            }
            return;
        }
        mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        mDownloadTaskRunner.startTask(new DownloadTask(App.getContext(), url, VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP, true), new SingleDownloadListener<DownloadTask, Boolean>() {
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
                ServiceQueryChainCallback callback = getQueryChainCallback();
                if (callback != null) {
                    callback.queryError(throwable);
                }
            }

            @Override
            public void onTaskSuccess(final DownloadTask downloadTask, Boolean aBoolean) {
                final String fileCachePath = downloadTask.getDownloadCacheUrl();
                if (TextUtils.isEmpty(fileCachePath)) {
                    ServiceQueryChainCallback callback = getQueryChainCallback();
                    if (callback != null) {
                        callback.queryError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                File hasDownFile = new File(fileCachePath);
                if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(fileCachePath))) {
                    ServiceQueryChainCallback callback = getQueryChainCallback();
                    if (callback != null) {
                        callback.queryError(new Exception("update error, because downloadFile not find"));
                    }
                    return;
                }
                VenvyAsyncTaskUtil.doAsyncTask("unzip_lua", new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Boolean>() {

                    @Override
                    public Boolean doAsyncTask(Void... voids) throws Exception {
                        long value = VenvyGzipUtil.unzipFile(fileCachePath, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, true);
                        File file = new File(fileCachePath);
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
                            String fileName = Uri.parse(url).getLastPathSegment().replace(".zip", "");
                            if (TextUtils.isEmpty(fileName)) {
                                callback.queryError(new Exception(""));
                                return;
                            }
                            File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName);
                            if (!file.exists() || !file.isFile()) {
                                callback.queryError(new Exception(""));
                                return;
                            }
                            String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), file.getAbsolutePath());
                            if (TextUtils.isEmpty(queryChainData)) {
                                callback.queryError(new Exception(""));
                                return;
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
                            callback.queryComplete(queryChainData,
                                    queryAdsInfo);
                        }
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
                        onTaskFailed(downloadTask, ie);
                    }
                });
            }
        });

    }

    public interface ServiceQueryChainCallback {
        void queryComplete(String queryAdsData, ServiceQueryAdsInfo queryAdsInfo);

        void queryError(Throwable t);
    }

    public void destroy() {
        mQueryChainCallback = null;
        if (mQueryAdsParams != null) {
            mQueryAdsParams.clear();
        }
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        if (mDownLuaUpdate != null) {
            mDownLuaUpdate.destroy();
        }
    }
}
