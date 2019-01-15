package cn.com.venvy.common.bean;

/**
 * Created by yanjiangbo on 2018/4/10.
 */

public class VideoPlayerSize {

    public int mHorVideoWidth;
    public int mHorVideoHeight;
    public int mVerVideoHeight;
    public int mVerVideoWidth;
    public int mPortraitSmallScreenOriginY; //竖屏异形屏幕下离顶部的高度，例如iphoneX 顶部title栏大小

    public VideoPlayerSize() {

    }

    public VideoPlayerSize(int horVideoWidth, int horVideoHeight, int verVideoWidth, int verVideoHeight, int portraitSmallScreenOriginY) {
        this.mHorVideoHeight = horVideoHeight;
        this.mHorVideoWidth = horVideoWidth;
        this.mVerVideoWidth = verVideoWidth;
        this.mVerVideoHeight = verVideoHeight;
        this.mPortraitSmallScreenOriginY = portraitSmallScreenOriginY;
    }
}
