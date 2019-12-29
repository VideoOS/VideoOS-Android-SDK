package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.json.JSONObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Map;

import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by videojj_pls on 2018/9/4.
 */

public class LVTableToJsonPlugin {
    private static LVTableToJsonPlugin.TableToJsonFunction mTableToJson;
    private static LVTableToJsonPlugin.JsonToTableFunction mJsonToTable;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("tableToJson", mTableToJson == null ? mTableToJson = new LVTableToJsonPlugin.TableToJsonFunction() : mTableToJson);
        venvyLVLibBinder.set("jsonToTable", mJsonToTable == null ? mJsonToTable = new LVTableToJsonPlugin.JsonToTableFunction() : mJsonToTable);
    }

    private static class TableToJsonFunction extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                Map<String, String> params = LuaUtil.toMap(table);
                if (params == null) {
                    return LuaValue.NIL;
                }
                JSONObject jData = new JSONObject(params);
                return jData != null ? LuaValue.valueOf(jData.toString()) : LuaValue.NIL;
            }
            return LuaValue.NIL;
        }
    }

    private static class JsonToTableFunction extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                String jsonStr = LuaUtil.getString(args, fixIndex + 1);
                return JsonUtil.toLuaTable(jsonStr);
            }
            return LuaValue.NIL;
        }
    }
}
