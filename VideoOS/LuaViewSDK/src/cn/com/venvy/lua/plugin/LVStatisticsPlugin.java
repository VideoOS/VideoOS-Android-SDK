package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.json.JSONObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;

import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by videopls on 2019/11/12.
 */

public class LVStatisticsPlugin {

    private static CommonTrack commonTrack;
    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("commonTrack", commonTrack == null ? commonTrack = new CommonTrack() : commonTrack);
    }

    private static class CommonTrack extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            Integer type = LuaUtil.getInt(args, fixIndex + 1);//事件类型
            LuaTable table = LuaUtil.getTable(args, fixIndex + 2);
            if(type == null || table == null){
                return LuaValue.NIL;
            }
            HashMap<String, String> miniAppMap = LuaUtil.toMap(table);
            if (miniAppMap == null) {
                return LuaValue.NIL;
            }
            JSONObject dataObj = new JSONObject(miniAppMap);
            if(dataObj != null){
                VenvyStatisticsManager.getInstance().submitCommonTrack(type,dataObj.toString());
            }
            return LuaValue.NIL;
        }
    }
}
