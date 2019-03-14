package cn.com.venvy.lua.maper;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;
import java.util.Map;

import cn.com.venvy.common.bean.SocketUserInfo;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.ud.VenvyUDMqttCallback;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class VenvyMqttMapper<U extends VenvyUDMqttCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyMqttMapper";
    private static final String[] sMethods = new String[]{
            "mqttCallback",
            "startMqtt",
            "stopMqtt",
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return mqttCallback(target, varargs);
            case 1:
                return startMqtt(target, varargs);
            case 2:
                return stopMqtt(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue mqttCallback(U target, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        if (callback != null && callback.isfunction()) {
            return target.setMqttCallback(callback);
        }
        return LuaValue.NIL;
    }

    public LuaValue startMqtt(U target, Varargs args) {
        try {
            if (args.narg() > 0) {
                LuaTable table = LuaUtil.getTable(args, 2);
                LuaTable configTable = LuaUtil.getTable(args, 3);
                Map<String, String> topsMap = LuaUtil.toMap(table);
                if (topsMap != null && configTable != null) {
                    SocketUserInfo info = new SocketUserInfo(configTable.get("username").tojstring(), configTable.get("password").tojstring(), configTable.get("host").tojstring(), configTable.get("port").tojstring());
                    target.setMqttTopics(info, topsMap);
                }
            }
        } catch (Exception e) {
            VenvyLog.e(VenvyMqttMapper.class.getName(), e);
        }
        return LuaValue.NIL;
    }

    public LuaValue stopMqtt(U target, Varargs args) {
        try {
            target.removeTopics();
        } catch (Exception e) {
            VenvyLog.e(VenvyMqttMapper.class.getName(), e);
        }
        return LuaValue.NIL;
    }
}
