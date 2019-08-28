package cn.com.venvy.lua.ud;

import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.view.VenvyLVNotificationCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyUDNotificationCallback extends UDView<VenvyLVNotificationCallback> implements VenvyObserver {
    LuaValue mNotificationCallback;
    private String mNotificationTag;

    public VenvyUDNotificationCallback(VenvyLVNotificationCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public VenvyUDNotificationCallback setNotificationCallback(LuaValue callbacks) {
        if (callbacks != null) {
            mNotificationCallback = callbacks;
        }
        return this;
    }

    public void startNotification(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mNotificationTag = key;
        ObservableManager.getDefaultObserable().addObserver(mNotificationTag, this);
    }

    public void stopNotification() {
        ObservableManager.getDefaultObserable().removeObserverByTag(mNotificationTag);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (bundle == null || !TextUtils.equals(tag, mNotificationTag)) {
            return;
        }
        //TODO未定义
        String data = bundle.getString("data");
        if (TextUtils.isEmpty(data)) {
            return;
        }
        LuaUtil.callFunction(mNotificationCallback, data);
    }
}
