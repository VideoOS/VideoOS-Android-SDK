package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.report.Report;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua: report("1","yourTag","yourMessage")
 * Created by mac on 18/2/6.
 */

public class LVReportPlugin {

    private static LuaReport sLuaReport;
    private static final String TAG = "LVReportPlugin";

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("logReport", sLuaReport == null ? sLuaReport = new LuaReport() : sLuaReport);
    }

    private static class LuaReport extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaValue messageValue = args.arg(fixIndex + 1);//key
                String message = VenvyLVLibBinder.luaValueToString(messageValue);
                if (TextUtils.isEmpty(message)) {
                    return LuaValue.NIL;
                }
                int level = LuaUtil.getInt(args, fixIndex + 2) == null ? 0 : LuaUtil.getInt(args, fixIndex + 2);
                boolean needReport = LuaUtil.getBoolean(args, fixIndex + 3) == null ? true : false;
                switch (level) {
                    case 0:
                        VenvyLog.i(message);
                        if (true) {
                            Report.report(Report.ReportLevel.i, TAG, message);
                        }
                        break;
                    case 1:
                        VenvyLog.i(message);
                        if (needReport) {
                            Report.report(Report.ReportLevel.w, TAG, message);
                        }
                        break;
                    default:
                        VenvyLog.i(message);
                        if (true) {
                            Report.report(Report.ReportLevel.i, TAG, message);
                        }
                        break;
                }
            }
            return LuaValue.NIL;
        }
    }
}
