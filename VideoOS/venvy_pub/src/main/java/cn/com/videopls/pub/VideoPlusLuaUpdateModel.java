package cn.com.videopls.pub;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.SingleDownloadListener;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyMD5Util;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
/*
 * Created by yanjiangbo on 2018/1/29.
 */

public class VideoPlusLuaUpdateModel extends VideoPlusBaseModel {

    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final String LUA_MANIFEST_JSON = "manifest.json";
    private static final String UNZIP_LUA_ASYNC_TAG = "unzip_lua";
    private static final String LOCAL_ASSETS_PATH = "lua";
    private static final String LUA_LOAD = "load_luas";
    private static final String PARSE_LOCAL_LUA_NAME = "parse_name_luas";
    private static final String PARSE_LOCAL_LUA_MD5 = "parse_md5_luas";
    private static final String LUA_FILE_VERSION = "lua_version";
    private static final String LUA_FILE_SDK_VERSION = "sdk_version";
    private static final String UPDATE_VERSION = "/api/detailedFileVersion";
    private DownloadTaskRunner mDownloadTaskRunner;
    private LuaUpdateCallback mUpdateCallback;


    public VideoPlusLuaUpdateModel(Platform platform, LuaUpdateCallback callback) {
        super(platform);
        mUpdateCallback = callback;
    }


    private LuaUpdateCallback getLuaUpdateCallback() {
        return mUpdateCallback;
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
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateError(new Exception("update data error"));
                            return;
                        }
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    if (TextUtils.isEmpty(encryptData)) {
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateError(new NullPointerException());
                        }
                        return;
                    }
//                    JSONObject needValue = new JSONObject(VenvyRSAUtil.decryptByRSA1(encryptData, VenvyRSAUtil.KEY_PUBLIC));
                    JSONObject needValue = new JSONObject(VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));
                    if (needValue == null) {
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateError(new NullPointerException());
                        }
                        return;
                    }
                    String luaPackageUrl = needValue.optString("downloadUrl");
                    String luaVersion = needValue.optString("version");
                    // 判定当有下载url，同时版本比当前版本高，同时本地不存在lua文件的时候下载
                    if (!TextUtils.isEmpty(luaPackageUrl)) {
                        if (!isOldVersionLua(Config.SDK_VERSION, LUA_FILE_SDK_VERSION)) {
                            VenvyFileUtil.copyFilesFromAssets(App.getContext(), LOCAL_ASSETS_PATH, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
                            writeToFileVersion(Config.SDK_VERSION, LUA_FILE_SDK_VERSION);
                        }
                        if (isOldVersionLua(luaVersion, LUA_FILE_VERSION)) {
                            //读取manifestJson文件内容
                            final String manifestJson = readManifestJson();
                            if (TextUtils.isEmpty(manifestJson)) {
                                startDownloadManifestJsonFile(luaVersion, luaPackageUrl);
                            } else {
                                checkDownLuaNameUrls(new LuaUpdateInfo.Builder().setVersion(luaVersion).setDownloadUrl(luaPackageUrl).setManifestJson(manifestJson).build());
                            }
                            return;
                        }
                        startDownloadManifestJsonFile(luaVersion, luaPackageUrl);
                    }
                } catch (Exception e) {
                    VenvyLog.e(VideoPlusLuaUpdateModel.class.getName(), e);
                    LuaUpdateCallback callback = getLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoPlusLuaUpdateModel.class.getName(), "检查更新接口访问失败 " + (e != null ? e.getMessage() : ""));
                LuaUpdateCallback callback = getLuaUpdateCallback();
                if (callback != null) {
                    callback.updateError(e);
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
        return HttpRequest.post(Config.HOST_VIDEO_OS + UPDATE_VERSION, createBody());
    }

    private Map<String, String> createBody() {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return paramBody;
    }

    private void startDownloadManifestJsonFile(final String version, final String url) {
        if (TextUtils.isEmpty(url)) {
            VenvyLog.e(VideoPlusLuaUpdateModel.class.getName(), "download url can't be null");
            LuaUpdateCallback callback = getLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("download url can't be null"));
            }
            return;
        }
        mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        mDownloadTaskRunner.startTask(new DownloadTask(App.getContext(), url, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + LUA_MANIFEST_JSON, true), new SingleDownloadListener<DownloadTask, Boolean>() {
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
                LuaUpdateCallback callback = getLuaUpdateCallback();
                if (callback != null) {
                    callback.updateError(new Exception("update error,because downloadTask error"));
                }
            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {
                final String fileCachePath = downloadTask.getDownloadCacheUrl();
                if (TextUtils.isEmpty(fileCachePath)) {
                    LuaUpdateCallback callback = getLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                File hasDownFile = new File(fileCachePath);
                if (!hasDownFile.exists() || !TextUtils.equals("json", VenvyFileUtil.getExtension(fileCachePath))) {
                    LuaUpdateCallback callback = getLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error, because downloadFile not find"));
                    }
                    return;
                }
                checkDownLuaMD5Urls(new LuaUpdateInfo.Builder().setVersion(version).setDownloadUrl(url).setManifestJson(readManifestJson()).build());
            }
        });
    }

    private void startDownloadLuaFile(final String version, String[] luaUrls) {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        }
        VenvyAsyncTaskUtil.doAsyncTask(LUA_LOAD, new VenvyAsyncTaskUtil.IDoAsyncTask<String, Void>() {
            @Override
            public Void doAsyncTask(String... urls) throws Exception {
                if (urls == null || urls.length <= 0) {
                    return null;
                }
                ArrayList<DownloadTask> arrayList = new ArrayList<>();
                for (String url : urls) {
                    DownloadTask task = new DownloadTask(App.getContext(), url, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(url).getLastPathSegment());
                    arrayList.add(task);
                }
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
                        if (failedTasks == null || failedTasks.size() <= 0) {
                            writeToFileVersion(version, LUA_FILE_VERSION);
                        }
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateComplete(true);
                        }
                    }
                });
                return null;
            }
        }, null, luaUrls);
    }

    private void writeToFileVersion(String version, String fileName) {
        Map<String, String> params = new HashMap<>();
        params.put("version", version);
        String path = VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH;
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        VenvyFileUtil.writeToFile(App.getContext(), path + File.separator + fileName, new JSONObject(params).toString());
    }

    private boolean isOldVersionLua(String version, String fileName) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName);
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        boolean isOldVersion = false;
        String localVersion = VenvyFileUtil.readFormFile(App.getContext(), file.getAbsolutePath());
        try {
            JSONObject obj = new JSONObject(localVersion);
            isOldVersion = TextUtils.equals(version, obj.optString("version"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOldVersion;
    }

    private String readManifestJson() {
        return VenvyFileUtil.readFormFile(App.getContext(), VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + LUA_MANIFEST_JSON);
    }

    private void checkDownLuaMD5Urls(final LuaUpdateInfo info) {
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_LOCAL_LUA_MD5, new VenvyAsyncTaskUtil.IDoAsyncTask<LuaUpdateInfo,
                List<String>>() {
            @Override
            public List<String> doAsyncTask(LuaUpdateInfo... infos) throws Exception {
                if (infos == null || infos.length == 0) {
                    return null;
                }
                List<String> luaUrls = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(infos[0].getManifestJson());
                    File luaFile = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
                    String[] tempList = luaFile.list();
                    int len = tempList.length;
                    int manifestLen = jsonArray.length();
                    for (int i = 0; i < manifestLen; i++) {
                        JSONObject jsonItemObj = jsonArray.optJSONObject(i);
                        String md5 = jsonItemObj.optString("md5");
                        String name = jsonItemObj.optString("name");
                        boolean needDown = true;
                        for (int j = 0; j < len; j++) {
                            File temp;
                            if (luaFile.getAbsolutePath().endsWith(File.separator)) {
                                temp = new File(luaFile.getAbsolutePath() + tempList[j]);
                            } else {
                                temp = new File(luaFile.getAbsolutePath() + File.separator + tempList[j]);
                            }
                            if (!temp.exists() || !temp.isFile()) {
                                continue;
                            }
                            if (TextUtils.equals(md5, VenvyMD5Util.EncoderByMd5(temp)) && TextUtils.equals(name, tempList[j])) {
                                needDown = false;
                                break;
                            }
                            needDown = true;
                        }
                        if (needDown) {
                            luaUrls.add(jsonItemObj.optString("url"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.i(TAG, "VideoPlusLuaUpdateModel ——> checkDownLuaUrls error：" + e.getMessage());
                }
                return luaUrls;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<List<String>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(List<String> urls) {
                if (urls == null) {
                    return;
                }
                if (urls.size() > 0) {
                    startDownloadLuaFile(info.getVersion(), urls.toArray(new String[urls.size()]));
                    return;
                }
                LuaUpdateCallback callback = getLuaUpdateCallback();
                if (callback != null) {
                    callback.updateComplete(false);
                }
            }

            @Override
            public void onCancelled() {
                VenvyLog.e("cancel");
            }

            @Override
            public void onException(Exception ie) {

            }
        }, info);


    }

    private void checkDownLuaNameUrls(final LuaUpdateInfo info) {
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_LOCAL_LUA_NAME, new VenvyAsyncTaskUtil.IDoAsyncTask<LuaUpdateInfo,
                List<String>>() {
            @Override
            public List<String> doAsyncTask(LuaUpdateInfo... infos) throws Exception {
                if (infos == null || infos.length == 0) {
                    return null;
                }
                List<String> luaUrls = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(infos[0].getManifestJson());
                    File luaFile = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
                    String[] tempList = luaFile.list();
                    int len = tempList.length;
                    int manifestLen = jsonArray.length();
                    for (int i = 0; i < manifestLen; i++) {
                        JSONObject jsonItemObj = jsonArray.optJSONObject(i);
                        String name = jsonItemObj.optString("name");
                        boolean needDown = true;
                        for (int j = 0; j < len; j++) {
                            File temp;
                            if (luaFile.getAbsolutePath().endsWith(File.separator)) {
                                temp = new File(luaFile.getAbsolutePath() + tempList[j]);
                            } else {
                                temp = new File(luaFile.getAbsolutePath() + File.separator + tempList[j]);
                            }
                            if (!temp.exists() || !temp.isFile()) {
                                continue;
                            }
                            if (TextUtils.equals(name, tempList[j])) {
                                needDown = false;
                                break;
                            }
                            needDown = true;
                        }
                        if (needDown) {
                            luaUrls.add(jsonItemObj.optString("url"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.i(TAG, "VideoPlusLuaUpdateModel ——> checkDownLuaUrls error：" + e.getMessage());
                }
                return luaUrls;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<List<String>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(List<String> urls) {
                if (urls == null) {
                    return;
                }
                if (urls.size() > 0) {
                    startDownloadLuaFile(info.getVersion(), urls.toArray(new String[urls.size()]));
                    return;
                }
                LuaUpdateCallback callback = getLuaUpdateCallback();
                if (callback != null) {
                    callback.updateComplete(false);
                }
            }

            @Override
            public void onCancelled() {
                VenvyLog.e("cancel");
            }

            @Override
            public void onException(Exception ie) {
            }
        }, info);


    }

    public interface LuaUpdateCallback {
        void updateComplete(boolean isUpdateByNetWork);

        void updateError(Throwable t);
    }


    public void destroy() {
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        VenvyAsyncTaskUtil.cancel(PARSE_LOCAL_LUA_NAME);
        VenvyAsyncTaskUtil.cancel(PARSE_LOCAL_LUA_MD5);
        VenvyAsyncTaskUtil.cancel(UNZIP_LUA_ASYNC_TAG);
        mUpdateCallback = null;
    }
}
