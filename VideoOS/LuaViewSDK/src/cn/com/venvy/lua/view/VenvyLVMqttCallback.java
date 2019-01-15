package cn.com.venvy.lua.view;

import android.os.Bundle;
import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.ud.VenvyUDMqttCallback;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_ARRIVED_DATA_MESSAGE;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyLVMqttCallback extends View implements ILVView, VenvyObserver {

    private VenvyUDMqttCallback mLuaUserdata;

    public VenvyLVMqttCallback(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDMqttCallback(this, globals, metaTable, varargs);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        mLuaUserdata.handleMqttBundle(bundle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(TAG_ARRIVED_DATA_MESSAGE, this);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLuaUserdata != null) {
            mLuaUserdata.removeTopics();
        }
        ObservableManager.getDefaultObserable().removeObserver(TAG_ARRIVED_DATA_MESSAGE, this);
    }
}
