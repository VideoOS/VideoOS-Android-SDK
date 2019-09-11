package cn.com.venvy.common.bean;

/**
 * Created by videojj_pls on 2019/8/28.
 * 视频真实宽高 以及 X Y
 */

public class VideoFrameSize {
    public int mVideoFrameHeight;
    public int mVideoFrameWidth;

    public int mVideoFrameX;
    public int mVideoFrameY;

    public VideoFrameSize() {

    }

    /***
     *
     * @param videoFrameWidth 视频宽
     * @param videoFrameHeight 视频高
     * @param x 偏移量
     * @param y 偏移量
     */
    public VideoFrameSize(int videoFrameWidth, int videoFrameHeight, int x, int y) {
        this.mVideoFrameWidth = videoFrameWidth;
        this.mVideoFrameHeight = videoFrameHeight;
        this.mVideoFrameX = x;
        this.mVideoFrameY = y;
    }
}
