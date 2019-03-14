package cn.com.venvy.lua.view;

import android.app.Activity;
import android.content.Context;
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
import cn.com.venvy.lua.ud.VenvyUDKeyboardCallback;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_KEYBOARD_STATUS_CHANGED;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyLVKeyboardCallback extends View implements ILVView, VenvyObserver {

    private VenvyUDKeyboardCallback mLuaUserdata;
    VenvyKeyboardProvider provider;
    private Context mContext;

    public VenvyLVKeyboardCallback(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        mContext = globals.getContext();
        this.mLuaUserdata = new VenvyUDKeyboardCallback(this, globals, metaTable, varargs);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mContext != null && mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (this.provider == null) {
                provider = new VenvyKeyboardProvider(activity);
                provider.start();
            }
            ObservableManager.getDefaultObserable().addObserver(TAG_KEYBOARD_STATUS_CHANGED, this);
        }
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (mLuaUserdata != null) {
            mLuaUserdata.handleKeyboardBundle(bundle);
        }
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (provider != null) {
            provider.dismiss();
        }
        ObservableManager.getDefaultObserable().removeObserver(TAG_KEYBOARD_STATUS_CHANGED, this);
        super.onDetachedFromWindow();
    }
}
