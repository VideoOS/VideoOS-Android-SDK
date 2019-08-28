package cn.com.venvy.lua.maper;

import android.text.TextUtils;

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
import cn.com.venvy.lua.ud.VenvyUDNotificationCallback;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20190828已对标"})
public class VenvyNotificationMapper<U extends VenvyUDNotificationCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyNotificationMapper";
    private static final String[] sMethods = new String[]{
            "notificationCallback",
            "startNotification",
            "stopNotification",//TODO key不固定必须Lua调用
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
                return notificationCallback(target, varargs);
            case 1:
                return startNotification(target, varargs);
            case 2:
                return stopNotification(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue notificationCallback(U target, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        if (callback != null && callback.isfunction()) {
            return target.setNotificationCallback(callback);
        }
        return LuaValue.NIL;
    }

    public LuaValue startNotification(U target, Varargs args) {
        if (args.narg() > 0) {
            String key = LuaUtil.getString(args, 2);
            if (TextUtils.isEmpty(key)) {
                return LuaValue.NIL;
            }
            target.startNotification(key);
        }
        return LuaValue.NIL;
    }

    public LuaValue stopNotification(U target, Varargs args) {
        target.stopNotification();
        return LuaValue.NIL;
    }
}
