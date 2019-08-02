package cn.com.venvy.lua.view;

import android.os.Bundle;
import android.text.TextUtils;
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
import cn.com.venvy.lua.ud.VenvyUDMediaLifeCycle;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_MEDIA_POSITION_CHANGED;
import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_SCREEN_CHANGED;

/**
 * 虽然是一个view，但是不能作为view处理，是监听播放进度的callback
 * Created by mac on 18/3/29.
 */

public class VenvyLVMediaCallback extends View implements ILVView, VenvyObserver {
    private VenvyUDMediaLifeCycle mLuaUserdata;

    public VenvyLVMediaCallback(Platform platform, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDMediaLifeCycle(platform, this, globals, metaTable, varargs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(TAG_MEDIA_POSITION_CHANGED, this);
        ObservableManager.getDefaultObserable().addObserver(TAG_SCREEN_CHANGED, this);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (TextUtils.equals(tag, TAG_MEDIA_POSITION_CHANGED)) {
            mLuaUserdata.handleMediaBundle(bundle);
        } else if (TextUtils.equals(tag, TAG_SCREEN_CHANGED)) {
            mLuaUserdata.handleScreenChanged(bundle);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ObservableManager.getDefaultObserable().removeObserver(TAG_MEDIA_POSITION_CHANGED, this);
        ObservableManager.getDefaultObserable().removeObserver(TAG_SCREEN_CHANGED, this);
    }
}
