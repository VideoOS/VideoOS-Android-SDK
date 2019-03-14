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
import cn.com.venvy.lua.ud.VenvyUDActivityLifeCycle;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_ACTIVITY_CHANGED;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyLVActivityCallback extends View implements ILVView, VenvyObserver {
    private VenvyUDActivityLifeCycle mLuaUserdata;

    public VenvyLVActivityCallback(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDActivityLifeCycle(this, globals, metaTable, varargs);
        ObservableManager.getDefaultObserable().addObserver(TAG_ACTIVITY_CHANGED, this);
    }


    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ObservableManager.getDefaultObserable().removeObserver(TAG_ACTIVITY_CHANGED, this);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        mLuaUserdata.handleActivityBundle(bundle);
    }
}

