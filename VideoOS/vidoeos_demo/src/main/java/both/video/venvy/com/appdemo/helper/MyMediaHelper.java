package both.video.venvy.com.appdemo.helper;

import both.video.venvy.com.appdemo.widget.VideoControllerView;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.VideoOSMediaController;

/**
 * Create by bolo on 08/06/2018
 */
public class MyMediaHelper extends VideoOSMediaController {

    private VideoControllerView.MediaPlayerControl mMediaPlayerControl;
    private VideoControllerView.IVideoControllerListener mVideoControllerListener;
    private VideoPlayerSize mVideoSizes;

    public void setMediaPlayerControl(VideoControllerView.MediaPlayerControl control) {
        mMediaPlayerControl = control;
    }

    public void setVideoController(VideoControllerView.IVideoControllerListener control) {
        mVideoControllerListener = control;
    }

    public void setVideoSize(VideoPlayerSize size) {
        mVideoSizes = size;
    }

    public void screenChange(boolean isPortrait) {
        if (mVideoControllerListener != null) {
            mVideoControllerListener.screenChange(isPortrait);
        }
    }

    @Override
    public void start() {
        if (mMediaPlayerControl != null) {
            mMediaPlayerControl.mediaPlayerStart();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayerControl != null) {
            mMediaPlayerControl.mediaPlayerPause();
        }
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public MediaStatus getCurrentMediaStatus() {
        return mMediaPlayerControl == null ? MediaStatus.PAUSE : mMediaPlayerControl
                .isMediaPlayerPlaying()
                ? MediaStatus.PLAYING : MediaStatus.PAUSE;
    }

    @Override
    public long getCurrentPosition() {
        if (mMediaPlayerControl != null) {
            return mMediaPlayerControl.getMediaPlayerCurrentPosition();
        } else {
            return -1;
        }
    }

    @Override
    public VideoPlayerSize getVideoSize() {
        return mVideoSizes;
    }
}
