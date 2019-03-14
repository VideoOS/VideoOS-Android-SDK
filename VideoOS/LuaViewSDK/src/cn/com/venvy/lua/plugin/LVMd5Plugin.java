package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.utils.VenvyStringUtil;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * md5 加密插件
 * Created by Arthur on 2017/8/21.
 */

public class LVMd5Plugin {
    private static Md5 sMd5;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("md5", sMd5 == null ? sMd5 = new Md5() : sMd5);
    }

    private static class Md5 extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 1);
            String md5 = VenvyLVLibBinder.luaValueToString(target);
            return !TextUtils.isEmpty(md5) ? LuaValue.valueOf(VenvyStringUtil.convertMD5(md5)) : LuaValue.NIL;
        }
    }
}
