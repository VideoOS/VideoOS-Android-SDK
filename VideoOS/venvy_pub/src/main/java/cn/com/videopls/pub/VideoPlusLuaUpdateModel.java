package cn.com.videopls.pub;

import android.support.annotation.Nullable;
import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.App;
import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
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
import cn.com.venvy.common.utils.VenvyPreferenceHelper;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/*
 * Created by yanjiangbo on 2018/1/29.
 */

public class VideoPlusLuaUpdateModel extends VideoPlusBaseModel {

    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final String LUA_ZIP = "/lua/os/lua.zip";
    private static final String LUA_CACHE_FILE_NAME = "venvy_lua_cache";
    private static final String LUA_CACHE_VERSION = "venvy_lua_version";
    private static final String UNZIP_LUA_ASYNC_TAG = "unzip_lua";
    private static final String LOCAL_ASSETS_PATH = "lua";
    private static final String UPDATE_VERSION = "/api/fileVersion";
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
                    JSONObject needValue = new JSONObject( VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));
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
                        if (!isOldVersionLua(luaVersion)) {
                            if (isValidLuaFile()) {
                                LuaUpdateCallback callback = getLuaUpdateCallback();
                                if (callback != null) {
                                    callback.updateComplete(false);
                                }
                                return;
                            }
                        }
                        startDownloadLuaFile(luaVersion, luaPackageUrl);
                    }
                } catch (JSONException e) {
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
        return HttpRequest.post(Config.HOST_VIDEO_OS+UPDATE_VERSION, createBody());
    }

    private Map<String, String> createBody() {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
//        paramBody.put("data", VenvyRSAUtil.encryptByRSA(new JSONObject(paramBody).toString(), VenvyRSAUtil.KEY_PUBLIC));
        return paramBody;
    }

    private void startDownloadLuaFile(final String version, String url) {
        if (TextUtils.isEmpty(url)) {
            VenvyLog.e(VideoPlusLuaUpdateModel.class.getName(), "download url can't be null");
            LuaUpdateCallback callback = getLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("download url can't be null"));
            }
            return;
        }
        VenvyFileUtil.copyFilesFromAssets(App.getContext(), LOCAL_ASSETS_PATH, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);//TODO
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
                LuaUpdateCallback callback = getLuaUpdateCallback();
                if (callback != null) {
                    callback.updateError(throwable);
                }
            }

            @Override
            public void onTaskSuccess(final DownloadTask downloadTask, Boolean aBoolean) {
                final String fileCachePath = downloadTask.getDownloadCacheUrl();
                if (TextUtils.isEmpty(fileCachePath)) {
                    LuaUpdateCallback callback = getLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                File hasDownFile = new File(fileCachePath);
                if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(fileCachePath))) {
                    LuaUpdateCallback callback = getLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error, because downloadFile not find"));
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
                            LuaUpdateCallback callback = getLuaUpdateCallback();
                            if (callback != null) {
                                callback.updateError(new Exception("unzip error"));
                            }
                            return;
                        }
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, version);
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateComplete(true);
                        }
                    }

                    @Override
                    public void onCancelled() {
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateError(new Exception("unzip error"));
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


    private boolean isOldVersionLua(String version) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        String oldVersion = VenvyPreferenceHelper.getString(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, "");
        return !TextUtils.equals(oldVersion, version);
    }

    private boolean isValidLuaFile() {
        File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
        return file.exists() && file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0;
    }

    public interface LuaUpdateCallback {
        void updateComplete(boolean isUpdateByNetWork);

        void updateError(Throwable t);
    }


    public void destroy() {
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }

        VenvyAsyncTaskUtil.cancel(UNZIP_LUA_ASYNC_TAG);
        mUpdateCallback = null;
    }
}
