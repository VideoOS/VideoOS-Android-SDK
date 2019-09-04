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

import cn.com.venvy.common.bean.NotificationInfo;
import cn.com.venvy.lua.ud.VenvyUDNotificationCallback;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20190828已对标"})
public class VenvyNotificationMapper<U extends VenvyUDNotificationCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyNotificationMapper";
    private static final String[] sMethods = new String[]{
            "registerNotification",
            "postNotification",
            "removeNotification",
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
                return registerNotification(target, varargs);
            case 1:
                return postNotification(target, varargs);
            case 2:
                return removeNotification(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue registerNotification(U target, Varargs args) {
        if (args.narg() > 0) {
            final String tag = LuaUtil.getString(args, 2);
            final LuaFunction callback = args.optfunction(2, null);
            if (TextUtils.isEmpty(tag)) {
                return LuaValue.NIL;
            }
            if (callback != null && callback.isfunction()) {
                return target.registerNotification(callback, tag);
            }
        }
        return LuaValue.NIL;
    }

    public LuaValue postNotification(U target, Varargs args) {
        if (args.narg() > 0) {
            String tag = LuaUtil.getString(args, 2);
            if (TextUtils.isEmpty(tag)) {
                return LuaValue.NIL;
            }
            LuaTable table = LuaUtil.getTable(args, 3);
            Map<String, String> messageMap = LuaUtil.toMap(table);
            NotificationInfo info = new NotificationInfo();
            info.messageInfo = messageMap;
            target.postNotification(tag, info);
        }
        return LuaValue.NIL;
    }

    public LuaValue removeNotification(U target, Varargs args) {
        if (args.narg() > 0) {
            String tag = LuaUtil.getString(args, 2);
            if (TextUtils.isEmpty(tag)) {
                return LuaValue.NIL;
            }
            target.removeNotification(tag);
        }
        return LuaValue.NIL;
    }
}
