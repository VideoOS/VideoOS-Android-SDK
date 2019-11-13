package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.venvy.App;
import cn.com.venvy.Platform;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.download.DownloadDbHelper;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua预加载插件
 * Created by mac on 17/12/7.
 */

public class LVPreLoadPlugin {
    private static ISVideoCachedData sISVideoCachedData;

    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("preloadImage", new PreLoadImageData(platform));
        venvyLVLibBinder.set("preloadVideo", new PreLoadVideoCacheData(platform));
        venvyLVLibBinder.set("preloadMiniAppLua", new PreLoadMiniAppLuaCacheData(platform));
        venvyLVLibBinder.set("isCacheVideo", sISVideoCachedData == null ? sISVideoCachedData = new ISVideoCachedData() : sISVideoCachedData);
    }

    private static class PreLoadImageData extends VarArgFunction {
        private Platform mPlatform;

        PreLoadImageData(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                Map<String, String> map = LuaUtil.toMap(table);
                if (map == null || map.size() <= 0) {
                    return LuaValue.NIL;
                }
                String[] preLoadUrls = new String[map.size()];
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    preLoadUrls[Integer.valueOf(entry.getKey()) - 1] = entry.getValue();
                }
                mPlatform.preloadImage(preLoadUrls, null);
            }
            return LuaValue.NIL;
        }
    }

    private static class PreLoadVideoCacheData extends VarArgFunction {
        private Platform mPlatform;

        PreLoadVideoCacheData(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                Map<String, String> map = LuaUtil.toMap(table);
                if (map == null || map.size() <= 0) {
                    return LuaValue.NIL;
                }
                String[] preLoadUrls = new String[map.size()];
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    preLoadUrls[Integer.valueOf(entry.getKey()) - 1] = entry.getValue();
                }
                mPlatform.preloadMedia(preLoadUrls, null);
            }
            return LuaValue.NIL;
        }
    }

    private static class PreLoadMiniAppLuaCacheData extends VarArgFunction {
        private Platform mPlatform;

        PreLoadMiniAppLuaCacheData(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                final LuaFunction callback = LuaUtil.getFunction(args, fixIndex + 2);
                try {
                    HashMap<String, String> paramsMap = LuaUtil.toMap(table);
                    if (paramsMap == null || paramsMap.size() <= 0) {
                        return LuaValue.NIL;
                    }

                    JSONObject miniAppInfoObj = new JSONObject(paramsMap);
                    if(miniAppInfoObj == null){
                        return LuaValue.NIL;
                    }
                    String miniAppId = miniAppInfoObj.optString("miniAppId");
                    JSONArray luaListArray = new JSONArray(miniAppInfoObj.optString("luaList"));
                    if(TextUtils.isEmpty(miniAppId) || luaListArray == null || luaListArray.length() <= 0){
                        return LuaValue.NIL;
                    }
                    List<LuaFileInfo> luaFileInfoList = new ArrayList<>();
                    LuaFileInfo luaFileInfo = new LuaFileInfo();
                    luaFileInfo.setMiniAppId(miniAppId);
                    List<LuaFileInfo.LuaListBean> luaList = luaArray2LuaList(luaListArray);

                    if (luaList == null || luaList.size() <= 0) {
                        return LuaValue.NIL;
                    }

                    luaFileInfo.setLuaList(luaList);
                    luaFileInfoList.add(luaFileInfo);

                    mPlatform.preloadMiniAppLua(mPlatform, luaFileInfoList, new PreloadLuaUpdate.CacheLuaUpdateCallback() {
                        @Override
                        public void updateComplete(boolean isUpdateByNetWork) {
                            if (callback != null && callback.isfunction()) {
                                LuaUtil.callFunction(callback, LuaValue.valueOf(true));
                            }
                        }

                        @Override
                        public void updateError(Throwable t) {
                            if (callback != null && callback.isfunction()) {
                                LuaUtil.callFunction(callback, LuaValue.valueOf(false));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class ISVideoCachedData extends VarArgFunction {
        DownloadDbHelper mHelper;

        ISVideoCachedData() {
            super();
            if (mHelper == null) {
                mHelper = new DownloadDbHelper(App.getContext());
            }
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 1);
            String url = VenvyLVLibBinder.luaValueToString(target);
            DownloadDbHelper.DownloadInfo info = mHelper.queryDownloadInfo(url);
            if (info == null) {
                return LuaValue.valueOf(false);
            }
            return LuaValue.valueOf(info.status == DownloadDbHelper.DownloadStatus.DOWNLOAD_SUCCESS);
        }
    }

    public static List<LuaFileInfo.LuaListBean> luaArray2LuaList(JSONArray luaArray) {
        if(luaArray == null || luaArray.length() <= 0){
            return null;
        }
        List<LuaFileInfo.LuaListBean> videoModeLuaList = new ArrayList<>();
        for (int j = 0; j < luaArray.length(); j++) {
            JSONObject luaFileObj = luaArray.optJSONObject(j);
            if(luaFileObj == null){
                break;
            }
            String luaName = luaFileObj.optString("name");
            String luaMD5 = luaFileObj.optString("md5");
            String luaUrl = luaFileObj.optString("url");
            String luaPath = luaFileObj.optString("path");
            if(TextUtils.isEmpty(luaMD5) || TextUtils.isEmpty(luaUrl)){
                break;
            }
            LuaFileInfo.LuaListBean luaListBean = new LuaFileInfo.LuaListBean();
            luaListBean.setLuaFileMd5(luaMD5);
            luaListBean.setLuaFileName(luaName);
            luaListBean.setLuaFilePath(luaPath);
            luaListBean.setLuaFileUrl(luaUrl);

            videoModeLuaList.add(luaListBean);
        }
        return videoModeLuaList;
    }
}
