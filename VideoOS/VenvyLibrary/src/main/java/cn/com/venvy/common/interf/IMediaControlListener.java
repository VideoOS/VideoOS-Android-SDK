package cn.com.venvy.common.interf;

import cn.com.venvy.common.bean.VideoPlayerSize;

/**
 * Created by yanjiangbo on 2017/5/14.
 */

public interface IMediaControlListener {

    void start();

    void pause();

    void restart();

    void seekTo(long position);

    void stop();

    long getCurrentPosition();

    @Deprecated
    boolean isMediaPlaying();

    boolean isPositive();

    MediaStatus getCurrentMediaStatus();

    float getVoice();

    //优先级高于Provided的设置
    VideoPlayerSize getVideoSize();

    long getDuration();
}
