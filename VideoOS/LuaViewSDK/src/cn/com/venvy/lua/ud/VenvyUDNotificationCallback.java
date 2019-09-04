package cn.com.venvy.lua.ud;

import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.Serializable;

import cn.com.venvy.common.bean.NotificationInfo;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.view.VenvyLVNotificationCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyUDNotificationCallback extends UDView<VenvyLVNotificationCallback> implements VenvyObserver {
    LuaValue registerNotificationCallback;
    public static final String OBSERVABLE_NOTIFICATION_MESSAGE = "Observable_notification_message";
    public static final String OBSERVABLE_NOTIFICATION_TAG = "Observable_notification_tag";

    public VenvyUDNotificationCallback(VenvyLVNotificationCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public VenvyUDNotificationCallback registerNotification(LuaValue callbacks, String tag) {
        if (callbacks != null) {
            registerNotificationCallback = callbacks;
        }
        return this;
    }

    public void postNotification(String tag, NotificationInfo message) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBSERVABLE_NOTIFICATION_MESSAGE, message);
        bundle.putString(OBSERVABLE_NOTIFICATION_TAG, tag);
        ObservableManager.getDefaultObserable().sendToTarget(tag, bundle);
    }

    public void removeNotification(String tag) {
        ObservableManager.getDefaultObserable().removeObserverByTag(tag);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (!TextUtils.equals(tag, bundle.getString(OBSERVABLE_NOTIFICATION_TAG))) {
            return;
        }
        Serializable serializable = bundle.getSerializable(OBSERVABLE_NOTIFICATION_MESSAGE);
        if (serializable == null) {
            if (registerNotificationCallback != null) {
                if (serializable != null && serializable instanceof NotificationInfo) {
                    LuaUtil.callFunction(registerNotificationCallback, LuaValue.NIL);
                }
            }
            return;
        }
        NotificationInfo info = (NotificationInfo) serializable;
        if (registerNotificationCallback != null) {
            if (serializable != null && serializable instanceof NotificationInfo) {
                LuaUtil.callFunction(registerNotificationCallback, LuaUtil.toTable(info.messageInfo));
            }
        }
    }
}
