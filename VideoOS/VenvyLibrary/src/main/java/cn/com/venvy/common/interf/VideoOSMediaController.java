package cn.com.venvy.common.interf;

import cn.com.venvy.common.bean.VideoFrameSize;
import cn.com.venvy.common.bean.VideoPlayerSize;

/**
 * Created by yanjiangbo on 2018/1/29.
 */

public abstract class VideoOSMediaController implements IMediaControlListener {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public boolean isMediaPlaying() {
        return true;
    }

    @Override
    public void seekTo(long position) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void restart() {

    }

    @Override
    public float getVoice() {
        return -1;
    }

    @Override
    public MediaStatus getCurrentMediaStatus() {
        return MediaStatus.DEFAULT;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    //优先级高于Provided的设置
    public VideoPlayerSize getVideoSize() {
        return null;
    }

    @Override
    public VideoFrameSize getVideoFrameSize() {
        return null;
    }

    @Override
    public String getVideoEpisode() {
        return null;
    }

    @Override
    public String getVideoTitle() {
        return null;
    }
}
