package cn.com.venvy.lua.plugin;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Config;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * 获取Video_os http host
 * Created by Arthur on 2019/1/1.
 */

public class LVHostPlugin {
    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("videoOShost", new GetHost());
    }

    /**
     * 获取本地Host
     */
    private static class GetHost extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(Config.HOST_VIDEO_OS);
        }
    }
}
