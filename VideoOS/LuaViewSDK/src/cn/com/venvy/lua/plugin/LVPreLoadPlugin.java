package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Map;

import cn.com.venvy.Platform;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua预加载插件
 * Created by mac on 17/12/7.
 */

public class LVPreLoadPlugin {
    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("preloadImage", new PreLoadImageData(platform));
        venvyLVLibBinder.set("preloadVideo", new PreLoadVideoCacheData(platform));
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
}
