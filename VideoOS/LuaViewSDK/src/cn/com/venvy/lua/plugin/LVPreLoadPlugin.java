package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.Platform;
import cn.com.venvy.App;
import cn.com.venvy.PreloadLuaUpdate;
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
                final LuaValue callback = LuaUtil.getValue(args,fixIndex+2);
                try {
                    HashMap<String, String> paramsMap = LuaUtil.toMap(table);
                    if (paramsMap == null || paramsMap.size() <= 0) {
                        return LuaValue.NIL;
                    }
                    JSONArray proLoadArray=new JSONArray();
                    for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                        proLoadArray.put(new JSONObject(entry.getValue().toString()));
                    }
                    mPlatform.preloadLuaList(mPlatform, proLoadArray,new PreloadLuaUpdate.CacheLuaUpdateCallback() {

                        @Override
                        public void updateComplete(boolean isUpdateByNetWork) {
                            if (isUpdateByNetWork) {
                                try {
                                    //TODO 反射 强制更新Lua目录
                                    Class<?> mClass = Class.forName("cn.com.videopls.pub.view.VideoOSLuaView");
                                    Method method = mClass.getMethod("destroyLuaScript");
                                    method.setAccessible(true);
                                    method.invoke(mClass, new Object[]{});
                                    LuaUtil.callFunction(callback,LuaValue.valueOf(true));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void updateError(Throwable t) {
                            LuaUtil.callFunction(callback,LuaValue.valueOf(false));
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
}
