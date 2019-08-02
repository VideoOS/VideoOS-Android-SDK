package cn.com.venvy.lua.ud;

import android.os.Bundle;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.Platform;
import cn.com.venvy.VideoPositionHelper;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.lua.view.VenvyLVMediaCallback;

/**
 * Created by mac on 18/3/27.
 */

public class VenvyUDMediaLifeCycle extends UDView<VenvyLVMediaCallback> {
    private LuaValue mOnMediaPause;
    private LuaValue mOnMediaPlay;
    private LuaValue mOnMediaEnd;
    private LuaValue mOnMediaSeeking;
    private LuaValue mOnMediaDefault;
    private LuaValue mOnMediaProgress;
    private LuaValue mOnPlayerSize;
    private Platform platform;

    public static final String KEY_TIME = "time";
    public static final String KEY_PLAY_STATUS = "play_status";
    public VenvyUDMediaLifeCycle(Platform platform, VenvyLVMediaCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        this.platform = platform;
    }


    public VenvyUDMediaLifeCycle setMediaCallback(LuaTable callbacks) {
        if (callbacks != null) {
            mOnMediaPause = LuaUtil.getFunction(callbacks, "onMediaPause", "onMediaPause");
            mOnMediaPlay = LuaUtil.getFunction(callbacks, "onMediaPlay", "onMediaPlay");
            mOnMediaEnd = LuaUtil.getFunction(callbacks, "onMediaEnd", "onMediaEnd");
            mOnMediaSeeking = LuaUtil.getFunction(callbacks, "onMediaSeeking", "onMediaSeeking");
            mOnMediaDefault = LuaUtil.getFunction(callbacks, "onMediaDefault", "onMediaDefault");
            mOnMediaProgress = LuaUtil.getFunction(callbacks, "onMediaProgress", "onMediaProgress");
            mOnPlayerSize = LuaUtil.getFunction(callbacks, "onPlayerSize", "onPlayerSize");
        }
        return this;
    }


    public VenvyUDMediaLifeCycle setOnMediaPause(LuaValue onMediaPause) {
        this.mOnMediaPause = onMediaPause;
        return this;
    }

    public VenvyUDMediaLifeCycle setOnMediaPlay(LuaValue onMediaPlay) {
        this.mOnMediaPlay = onMediaPlay;
        return this;
    }


    public VenvyUDMediaLifeCycle setOnMediaEnd(LuaValue onMediaEnd) {
        this.mOnMediaEnd = onMediaEnd;
        return this;
    }


    public VenvyUDMediaLifeCycle setOnMediaSeeking(LuaValue onMediaSeeking) {
        this.mOnMediaSeeking = onMediaSeeking;
        return this;
    }

    public VenvyUDMediaLifeCycle setOnMediaDefault(LuaValue onMediaDefault) {
        this.mOnMediaDefault = onMediaDefault;
        return this;
    }

    public VenvyUDMediaLifeCycle setOnMediaProgress(LuaValue onMediaProgress) {
        this.mOnMediaProgress = onMediaProgress;
        return this;
    }

    public VenvyUDMediaLifeCycle setOnPlayerSize(LuaValue onPlayerSize) {
        this.mOnPlayerSize = onPlayerSize;
        return this;
    }


    MediaStatus mLastMediaStatus;
    public void handleMediaBundle(Bundle bundle) {
        long position = bundle.getLong(KEY_TIME);
        LuaUtil.callFunction(mOnMediaProgress, LuaValue.valueOf(position));
        MediaStatus media = (MediaStatus) bundle.getSerializable(KEY_PLAY_STATUS);
        if (mLastMediaStatus == media) {
            return;
        }
        if (media != null) {
            LuaValue callback = null;
            switch (media) {
                case PLAYING:
                    callback = mOnMediaPlay;
                    break;
                case PAUSE:
                    callback = mOnMediaPause;
                    break;
                case STOP:
                    callback = mOnMediaEnd;
                    break;
                case SEEKING:
                    callback = mOnMediaSeeking;
                    break;
                case DEFAULT:
                    callback = mOnMediaDefault;
                    break;
            }
            mLastMediaStatus = media;
            LuaUtil.callFunction(callback, LuaValue.valueOf(position));
        }
    }

    public void handleScreenChanged(Bundle bundle) {
        ScreenStatus screenStatus = (ScreenStatus) bundle.getSerializable("screen_changed");
        if (screenStatus == null) {
            return;
        }
        int type = -1;//0: 竖屏小屏幕，1 竖屏全凭，2 横屏全屏
        switch (screenStatus) {
            case SMALL_VERTICAL:
                type = 0;
                break;
            case FULL_VERTICAL:
                type = 1;
                break;
            case LANDSCAPE:
                type = 2;
                break;
        }
        LuaUtil.callFunction(mOnPlayerSize, LuaValue.valueOf(type));
    }


    /**
     * 获取AppKey
     */
    public void startVideoTime() {
        if (platform == null || platform.getMediaControlListener() == null) {
            return;
        }
        VideoPositionHelper.getInstance().setMediaPlayController(platform.getMediaControlListener());
        VideoPositionHelper.getInstance().start();
    }

    public void pauseVideoTime() {
        if (platform == null) {
            return;
        }
        VideoPositionHelper.getInstance().pause();
    }


    public void stopVideoTime() {
        if (platform == null) {
            return;
        }
        VideoPositionHelper.getInstance().cancel();
    }
}
