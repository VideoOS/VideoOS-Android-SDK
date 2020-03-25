package cn.com.venvy.lua.view;

import android.os.Bundle;
import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.Platform;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.ud.VenvyUDAcrClouldCallback;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_ACR_DATA_MESSAGE;

/**
 * Created by lgf on 2020/2/26.
 */

public class VenvyLVAcrClouldCallback extends View implements ILVView, VenvyObserver {
    private VenvyUDAcrClouldCallback mLuaUserdata;

    public VenvyLVAcrClouldCallback(Platform platform, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDAcrClouldCallback(platform, this, globals, metaTable, varargs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(TAG_ACR_DATA_MESSAGE, this);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLuaUserdata != null) {
            mLuaUserdata.destroyRecognize();
        }
        ObservableManager.getDefaultObserable().removeObserver(TAG_ACR_DATA_MESSAGE, this);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (mLuaUserdata != null) {
            mLuaUserdata.handleAcrMessageBundle(bundle);
        }
    }
}
