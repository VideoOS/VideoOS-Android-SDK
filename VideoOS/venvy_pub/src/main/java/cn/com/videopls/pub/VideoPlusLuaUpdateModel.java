package cn.com.videopls.pub;

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
import cn.com.venvy.common.utils.VenvyPreferenceHelper;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/*
 * Created by yanjiangbo on 2018/1/29.
 */

public class VideoPlusLuaUpdateModel extends VideoPlusBaseModel {

    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final String LUA_MANIFEST_JSON = "/lua/os/manifest.json";
    private static final String LUA_CACHE_FILE_NAME = "venvy_lua_cache";
    private static final String LUA_CACHE_VERSION = "venvy_lua_version";
    private static final String LUA_CACHE_FILEMD5 = "venvy_lua_fileMd5";
    private static final String UNZIP_LUA_ASYNC_TAG = "unzip_lua";
    private static final String LOCAL_ASSETS_PATH = "lua";
    private static final String LUA_LOAD = "load_luas";
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
                    String luaFileMd5 = needValue.optString("fileMd5");
                    // 判定当有下载url，同时版本比当前版本高，同时本地不存在lua文件的时候下载
                    if (!TextUtils.isEmpty(luaPackageUrl)) {
                        if (!isOldVersionLua(luaVersion)) {
                            if (isValidLuaFile()) {
                                List<JSONObject> jsonObjectList = readErrorLua(new File(getManifestJsonPath(luaVersion)), new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH));
                                int len = jsonObjectList.size();
                                if (len > 0) {
                                    startDownloadLuaFile(jsonObjectList.toArray(new JSONObject[len]), luaFileMd5, luaVersion);
                                } else {
                                    LuaUpdateCallback callback = getLuaUpdateCallback();
                                    if (callback != null) {
                                        callback.updateComplete(false);
                                    }
                                }
                                return;
                            }
                        }
                        startDownloadManifestJsonFile(luaVersion, luaFileMd5, luaPackageUrl);
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

    private void startDownloadManifestJsonFile(final String version, final String fileMd5, String url) {
        if (TextUtils.isEmpty(url)) {
            VenvyLog.e(VideoPlusLuaUpdateModel.class.getName(), "download url can't be null");
            LuaUpdateCallback callback = getLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("download url can't be null"));
            }
            return;
        }
        VenvyFileUtil.copyFilesFromAssets(App.getContext(), LOCAL_ASSETS_PATH, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
        mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        mDownloadTaskRunner.startTask(new DownloadTask(App.getContext(), url, getManifestJsonPath(version), true), new SingleDownloadListener<DownloadTask, Boolean>() {
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
                File oldManifestFile = new File(getManifestJsonPath(getOldVersion()));
                if (isOldManifestJson(oldManifestFile)) {
                    if (isSameMd5WithManifestJson(hasDownFile)) {
                        deleteOldManifestJson(version);
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, version);
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_FILEMD5, fileMd5);
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateComplete(true);
                        }
                        return;
                    } else {
                        List<JSONObject> jsonObjectList = readLuaWithFile(hasDownFile, oldManifestFile);
                        int len = jsonObjectList.size();
                        if (len <= 0) {
                            deleteOldManifestJson(version);
                            VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, version);
                            VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_FILEMD5, fileMd5);
                            LuaUpdateCallback callback = getLuaUpdateCallback();
                            if (callback != null) {
                                callback.updateComplete(false);
                            }
                            return;
                        } else {
                            startDownloadLuaFile(jsonObjectList.toArray(new JSONObject[len]), fileMd5, version);
                        }
                    }
                } else {
                    List<JSONObject> jsonObjectList = readLuaWithFile(hasDownFile);
                    int len = jsonObjectList.size();
                    if (len <= 0) {
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, version);
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_FILEMD5, fileMd5);
                        LuaUpdateCallback callback = getLuaUpdateCallback();
                        if (callback != null) {
                            callback.updateComplete(false);
                        }
                        return;
                    } else {
                        startDownloadLuaFile(jsonObjectList.toArray(new JSONObject[len]), fileMd5, version);
                    }
                }
            }
        });
    }

    private void startDownloadLuaFile(JSONObject[] luaUrls, final String fileMd5, final String version) {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(getRequestConnect());
        }
        VenvyAsyncTaskUtil.doAsyncTask(LUA_LOAD, new VenvyAsyncTaskUtil.IDoAsyncTask<JSONObject, Void>() {
            @Override
            public Void doAsyncTask(JSONObject... objs) throws Exception {
                if (objs == null || objs.length <= 0) {
                    return null;
                }
                ArrayList<DownloadTask> arrayList = new ArrayList<>();
                for (JSONObject obj : objs) {
                    String url = obj.optString("url");
                    String name = obj.optString("name");
                    DownloadTask task = new DownloadTask(App.getContext(), url, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + name);
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
                    }

                    @Override
                    public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {
                    }

                    @Override
                    public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                        deleteOldManifestJson(version);
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, version);
                        VenvyPreferenceHelper.put(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_FILEMD5, fileMd5);
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

    private List<JSONObject> readLuaWithFile(File manifestFile) {
        List<JSONObject> jsonObjList = new ArrayList<>();
        String manifestJson = VenvyFileUtil.readFormFile(App.getContext(), manifestFile.getAbsolutePath());
        try {
            JSONArray jsonArray = new JSONArray(manifestJson);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                jsonObjList.add(jsonArray.optJSONObject(i));
            }
        } catch (Exception e) {
            VenvyLog.i(TAG, "readLuaWithFile ——> error：" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObjList;
    }

    private List<JSONObject> readErrorLua(File manifestFile, File luaFile) {
        List<JSONObject> jsonObjList = new ArrayList<>();
        try {
            String manifestJson = VenvyFileUtil.readFormFile(App.getContext(), manifestFile.getAbsolutePath());

            JSONArray jsonArray = new JSONArray(manifestJson);
            String[] tempList = luaFile.list();
            int len = tempList.length;
            int manifestLen = jsonArray.length();
            for (int i = 0; i < manifestLen; i++) {
                JSONObject jsonItemObj = jsonArray.optJSONObject(i);
                String luaName = jsonItemObj.optString("name");
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
                    if (TextUtils.equals(luaName, temp.getName())) {
                        needDown = false;
                        break;
                    }
                    needDown = true;
                }
                if (needDown) {
                    jsonObjList.add(jsonItemObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjList;
    }

    private List<JSONObject> readLuaWithFile(File manifestFile, File oldManifestFile) {
        List<JSONObject> jsonObjList = new ArrayList<>();
        try {
            String manifestJson = VenvyFileUtil.readFormFile(App.getContext(), manifestFile.getAbsolutePath());
            String oldManifestJson = VenvyFileUtil.readFormFile(App.getContext(), oldManifestFile.getAbsolutePath());

            JSONArray jsonArray = new JSONArray(manifestJson);
            JSONArray oldJsonArray = new JSONArray(oldManifestJson);
            int len = jsonArray.length();
            int oldLen = oldJsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonItemObj = jsonArray.optJSONObject(i);
                String luaName = jsonItemObj.optString("name");
                String luaMd5 = jsonItemObj.optString("md5");
                boolean needDown = true;
                for (int j = 0; j < oldLen; j++) {
                    JSONObject oldJsonItemObj = oldJsonArray.optJSONObject(j);
                    String oldLuaName = oldJsonItemObj.optString("name");
                    String oldLuaMd5 = oldJsonItemObj.optString("md5");
                    if (TextUtils.equals(luaName, oldLuaName) && TextUtils.equals(luaMd5, oldLuaMd5)) {
                        needDown = false;
                        break;
                    }
                    needDown = true;
                }
                if (needDown) {
                    jsonObjList.add(jsonItemObj);
                }
            }
        } catch (Exception e) {
            VenvyLog.i(TAG, "readLuaWithFile ——> error：" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObjList;
    }

    private boolean isSameMd5WithManifestJson(File manifestFile) {
        String manifestJson = VenvyFileUtil.readFormFile(App.getContext(), manifestFile.getAbsolutePath());
        String oldMd5 = getOldFileMd5();
        if (!TextUtils.isEmpty(manifestJson)) {
            try {
                JSONObject jsonObj = new JSONObject(manifestJson);
                return TextUtils.equals(jsonObj.optString("fileMd5"), oldMd5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isOldManifestJson(File oldManifestFile) {
        if (!oldManifestFile.exists() || !TextUtils.equals("json", VenvyFileUtil.getExtension(oldManifestFile.getAbsolutePath()))) {
            return false;
        }
        return true;
    }

    private boolean isOldVersionLua(String version) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        String oldVersion = VenvyPreferenceHelper.getString(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, "");
        return !TextUtils.equals(oldVersion, version);
    }

    private String getOldVersion() {
        return VenvyPreferenceHelper.getString(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_VERSION, "");
    }

    private String getOldFileMd5() {
        return VenvyPreferenceHelper.getString(App.getContext(), LUA_CACHE_FILE_NAME, LUA_CACHE_FILEMD5, "");
    }

    private boolean isValidLuaFile() {
        File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
        return file.exists() && file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0;
    }

    private String getManifestJsonPath(String version) {
        return VenvyFileUtil.getCachePath(App.getContext()) + File.separator + version + LUA_MANIFEST_JSON;
    }

    private void deleteOldManifestJson(String version) {
        String oldVersion = getOldVersion();
        if (!TextUtils.isEmpty(oldVersion)&& TextUtils.equals(version,oldVersion)) {
            VenvyFileUtil.delFolder(VenvyFileUtil.getCachePath(App.getContext()) + File.separator + getOldVersion());
        }
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
