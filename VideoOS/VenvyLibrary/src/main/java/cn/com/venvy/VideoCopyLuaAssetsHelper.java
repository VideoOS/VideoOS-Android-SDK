package cn.com.venvy;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.common.utils.VenvyFileUtil;

/**
 * Created by videojj_pls on 2019/8/23.
 */

public class VideoCopyLuaAssetsHelper {
    private static final String LUA_FILE_SDK_VERSION = "sdk_version";
    private static final String LOCAL_ASSETS_PATH = "lua";

    private static VideoCopyLuaAssetsHelper sVideoPlusCopyLuaAssetsHelper;

    public static synchronized VideoCopyLuaAssetsHelper getInstance() {
        if (sVideoPlusCopyLuaAssetsHelper == null) {
            sVideoPlusCopyLuaAssetsHelper = new VideoCopyLuaAssetsHelper();
        }
        return sVideoPlusCopyLuaAssetsHelper;
    }

    private VideoCopyLuaAssetsHelper() {
    }

    public void start(LuaCopyCallback copyCallback) {
        if (!isOldVersionLua(Config.SDK_VERSION, LUA_FILE_SDK_VERSION)) {
            boolean isSuccess = VenvyFileUtil.copyFilesFromAssets(App.getContext(), LOCAL_ASSETS_PATH, VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH);
            if (isSuccess) {
                writeToFileVersion(Config.SDK_VERSION, LUA_FILE_SDK_VERSION);
                if (copyCallback != null) {
                    copyCallback.copyComplete();
                }
            } else {
                if (copyCallback != null) {
                    copyCallback.copyError(new Exception("copy lua with assets error"));
                }
            }
            return;
        }
        if (copyCallback != null) {
            copyCallback.copyComplete();
        }
    }

    public interface LuaCopyCallback {
        void copyComplete();

        void copyError(Throwable t);
    }

    private boolean isOldVersionLua(String version, String fileName) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH, fileName);
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

    private void writeToFileVersion(String version, String fileName) {
        Map<String, String> params = new HashMap<>();
        params.put("version", version);
        String path = VenvyFileUtil.getCachePath(App.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH;
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
}
