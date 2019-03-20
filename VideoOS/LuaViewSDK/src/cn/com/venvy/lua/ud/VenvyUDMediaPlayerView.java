package cn.com.venvy.lua.ud;

import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.media.view.CustomVideoView;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.lua.view.VenvyMediaPlayerView;


/**
 * Created by lgf on 2018/1/19.UIRefreshScrollerMethodMapper
 */

public class VenvyUDMediaPlayerView extends UDViewGroup<VenvyMediaPlayerView> implements VenvyObserver {

    private LuaValue mOnStart;
    private LuaValue mOnPause;
    private LuaValue mOnFinished;
    private LuaValue mOnStop;
    private LuaValue mOnError;
    private LuaValue mOnPrepare;
    private LuaValue lastStatus;
    private LuaValue mOnVolume;

    public VenvyUDMediaPlayerView(VenvyMediaPlayerView view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    public void startPlay(String url) {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            customVideoView.startPlay(url);
        }
    }

    public void stopPlay() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            customVideoView.stopPlay();
        }
    }

    public void pausePlay() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            customVideoView.pausePlay();
        }
    }

    public void restartPlay() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            customVideoView.restart();
        }
    }

    public long getPosition() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            return customVideoView.getCurrentPosition();
        }
        return 0;
    }

    public int getStatus() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            return customVideoView.getCurrentState();
        }
        return CustomVideoView.STATE_IDLE;
    }

    public String getSource() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            return customVideoView.getSource();
        }
        return null;
    }

    public long getDuration() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            return customVideoView.getDuration();
        }
        return 0;
    }

    public void setVoice(float voice) {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            customVideoView.setVoice(voice);
        }
    }

    public float getVoice() {
        CustomVideoView customVideoView = getView().getCustomVideoView();
        if (customVideoView != null) {
            return customVideoView.getVoice();
        }
        return -1;
    }

    @Override
    public UDView setCornerRadius(float radius) {
        getView().setCornerRadius(radius);
        return this;
    }

    @Override
    public float getCornerRadius() {
        return super.getCornerRadius();
    }

    @Override
    public UDView setBorderColor(Integer borderColor) {
        getView().setStrokeColor(borderColor);
        return this;
    }

    @Override
    public UDView setBorderWidth(int borderWidth) {
        getView().setStrokeWidth(borderWidth);
        return this;
    }

    @Override
    public UDViewGroup setCallback(LuaValue callbacks) {
        mOnStart = LuaUtil.getFunction(callbacks, "OnStart", "onStart");
        mOnPause = LuaUtil.getFunction(callbacks, "OnPause", "onPause");
        mOnStop = LuaUtil.getFunction(callbacks, "OnStop", "onStop");
        mOnFinished = LuaUtil.getFunction(callbacks, "OnFinished", "onFinished");
        mOnPrepare = LuaUtil.getFunction(callbacks, "OnPrepare", "onPrepare");
        mOnError = LuaUtil.getFunction(callbacks, "OnError", "onError");
        mOnVolume = LuaUtil.getFunction(callbacks, "OnChangeVolume", "onChangeVolume");
        return super.setCallback(callbacks);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (TextUtils.equals(tag, VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED)) {
            if (bundle == null) {
                return;
            }
            int status = bundle.getInt(CustomVideoView.MEDIA_STATUS);
            String currentSource = getSource();
            switch (status) {
                case CustomVideoView.STATE_PLAYING:
                case CustomVideoView.STATE_BUFFERING_PLAYING:
                    if (lastStatus != mOnStart) {
                        LuaUtil.callFunction(mOnStart, valueOf(currentSource));
                        lastStatus = mOnStart;
                    }
                    break;
                case CustomVideoView.STATE_BUFFERING_PAUSED:
                case CustomVideoView.STATE_PAUSED:
                    if (lastStatus != mOnPause) {
                        LuaUtil.callFunction(mOnPause, valueOf(currentSource));
                        lastStatus = mOnPause;
                    }
                    break;
                case CustomVideoView.STATE_COMPLETED:
                    if (lastStatus != mOnFinished) {
                        LuaUtil.callFunction(mOnFinished, valueOf(currentSource));
                        lastStatus = mOnFinished;
                    }
                    break;
                case CustomVideoView.STATE_ERROR:
                    if (lastStatus != mOnError) {
                        LuaUtil.callFunction(mOnError, valueOf(currentSource));
                        lastStatus = mOnError;
                    }
                    break;
                case CustomVideoView.STATE_PREPARING:
                case CustomVideoView.STATE_PREPARED:
                    if (lastStatus != mOnPrepare) {
                        LuaUtil.callFunction(mOnPrepare, valueOf(currentSource));
                        lastStatus = mOnPrepare;
                    }
                    break;
            }
        } else if (TextUtils.equals(tag, VenvyObservableTarget.TAG_MEDIA_CHANGED)) {
            if (bundle == null)
                return;
            VenvyMediaPlayerView playerView = getView();
            if (playerView == null)
                return;
            CustomVideoView videoView = playerView.getCustomVideoView();
            if (videoView == null)
                return;
            Object object = bundle.getSerializable("media_changed");
            if (object != null && object instanceof MediaStatus) {
                MediaStatus status = (MediaStatus) object;
                if (status == MediaStatus.PAUSE) {
                    videoView.pausePlay();

                } else if (status == MediaStatus.PLAYING) {
                    videoView.mediaPlayerStart();
                }
            }
        } else if (TextUtils.equals(tag, VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED)) {
            if (bundle == null)
                return;
            int volume = bundle.getInt(CustomVideoView.VOLUME_STATUS);
            if (volume >= 0) {
                LuaUtil.callFunction(mOnVolume, valueOf(volume));
            }
        }
    }
}
