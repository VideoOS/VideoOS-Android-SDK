package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua: report("1","yourTag","yourMessage")
 * Created by mac on 18/2/6.
 */

public class LVReportPlugin {

    private static LuaReport sLuaReport;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("report", sLuaReport == null ? sLuaReport = new LuaReport() : sLuaReport);
    }

    private static class LuaReport extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {

                switch (LuaUtil.getString(args, fixIndex + 1)) {
                    case "0":
                        break;
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        break;
                }
            }
            return LuaValue.NIL;
        }
    }
}
