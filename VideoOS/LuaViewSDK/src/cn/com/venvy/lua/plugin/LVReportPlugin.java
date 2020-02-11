package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.report.Report;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua: report("1","yourTag","yourMessage")
 * Created by mac on 18/2/6.
 */

public class LVReportPlugin {

    private static LuaReport sLuaReport;
    private static final String TAG = "LVReportPlugin";

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("report", sLuaReport == null ? sLuaReport = new LuaReport() : sLuaReport);
    }

    private static class LuaReport extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaValue keyValue = args.arg(fixIndex + 2);  //key
                String message = VenvyLVLibBinder.luaValueToString(keyValue);
                if (TextUtils.isEmpty(message)) {
                    return LuaValue.NIL;
                }
                switch (LuaUtil.getString(args, fixIndex + 1)) {
                    case "0":
                        Report.report(Report.ReportLevel.i, TAG, message);
                        break;
                    case "1":
                        Report.report(Report.ReportLevel.w, TAG, message);
                        break;
                    case "2":
                        Report.report(Report.ReportLevel.e, TAG, message);
                        break;
                    case "3":
                        Report.report(Report.ReportLevel.u, TAG, message);
                        break;
                    case "4":
                        Report.report(Report.ReportLevel.d, TAG, message);
                        break;
                    default:
                        break;
                }
            }
            return LuaValue.NIL;
        }
    }
}
