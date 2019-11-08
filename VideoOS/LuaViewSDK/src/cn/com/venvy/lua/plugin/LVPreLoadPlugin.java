package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.Platform;
import cn.com.venvy.App;
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
        venvyLVLibBinder.set("preloadLuaList", new PreLoadLuaCacheData(platform));
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

    private static class PreLoadLuaCacheData extends VarArgFunction {
        private Platform mPlatform;

        PreLoadLuaCacheData(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                try {
                    HashMap<String, String> paramsMap = LuaUtil.toMap(table);
                    if (paramsMap == null || paramsMap.size() <= 0) {
                        return LuaValue.NIL;
                    }
                    JSONArray proLoadArray = new JSONArray();
                    for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                        proLoadArray.put(new JSONObject(entry.getValue().toString()));
                    }
                    mPlatform.preloadLuaList(mPlatform, proLoadArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

//                if (callback != null && callback.isfunction()) {
//                    LuaUtil.callFunction(callback, LuaValue.valueOf(true));
//                }
                try {
                    HashMap<String, String> paramsMap = LuaUtil.toMap(table);
                    if (paramsMap == null || paramsMap.size() <= 0) {
                        return LuaValue.NIL;
                    }
                    JSONArray proLoadArray = new JSONArray();
                    for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                        proLoadArray.put(new JSONObject(entry.getValue().toString()));
                    }
                    mPlatform.preloadLuaList(mPlatform, proLoadArray);
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
}
