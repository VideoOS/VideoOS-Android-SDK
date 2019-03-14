
package cn.com.venvy.lua.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

import cn.com.venvy.common.media.view.CustomVideoView;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.ud.VenvyUDMediaPlayerView;


/**
 * Created by lgf on 2018/1/19.
 */

public class VenvyMediaPlayerView extends RelativeLayout implements ILVViewGroup {
    private VenvyUDMediaPlayerView mLuaUserdata;
    private CustomVideoView customVideoView;

    public VenvyMediaPlayerView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        mLuaUserdata = new VenvyUDMediaPlayerView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(getContext());
    }

    private void init(Context context) {
        customVideoView = new CustomVideoView(context);
        this.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        this.addView(customVideoView, params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_MEDIA_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED, mLuaUserdata);
    }

    @Override
    protected void onDetachedFromWindow() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_MEDIA_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED, mLuaUserdata);
        super.onDetachedFromWindow();
    }

    public CustomVideoView getCustomVideoView() {
        return customVideoView;
    }


    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
        mLuaUserdata.setChildNodeViews(childNodeViews);
    }
}
