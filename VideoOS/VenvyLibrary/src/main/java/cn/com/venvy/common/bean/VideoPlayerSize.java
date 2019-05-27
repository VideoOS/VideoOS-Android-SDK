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

    public int mFullScreenContentWidth;
    public int mFullScreenContentHeight;

    public VideoPlayerSize() {

    }

    public VideoPlayerSize(int horVideoWidth, int horVideoHeight, int verVideoWidth, int verVideoHeight, int portraitSmallScreenOriginY) {
        this.mHorVideoHeight = horVideoHeight;
        this.mHorVideoWidth = horVideoWidth;
        this.mVerVideoWidth = verVideoWidth;
        this.mVerVideoHeight = verVideoHeight;
        this.mPortraitSmallScreenOriginY = portraitSmallScreenOriginY;
    }

    /***
     * fullScreenContentWidth,fullScreenContentHeight为界面全屏可展示的最大宽高
     *
     * @param fullScreenContentWidth 横竖屏内容区宽度
     * @param fullScreenContentHeight 横竖屏内容区高度
     * @param verVideoWidth  播放器竖屏宽度
     * @param verVideoHeight 播放器竖屏高度
     */
    public VideoPlayerSize(int fullScreenContentWidth, int fullScreenContentHeight, int verVideoWidth, int verVideoHeight) {
        this.mFullScreenContentWidth = fullScreenContentWidth;
        this.mFullScreenContentHeight = fullScreenContentHeight;
        this.mVerVideoWidth = verVideoWidth;
        this.mVerVideoHeight = verVideoHeight;
    }
}
