package both.video.venvy.com.appdemo.activity;

import android.widget.VideoView;

import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.VideoOSMediaController;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.mqtt.VenvyMqtt;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.glide.GlideImageLoader;
import cn.com.videopls.pub.Provider;
import cn.com.videopls.pub.VideoPlusAdapter;

/**
 * Created by videojj_pls on 2018/9/28.
 */

public class PlusAdapter extends VideoPlusAdapter {
    private VideoView player;

    public PlusAdapter(VideoView player) {
        this.player = player;
    }
    //设置配置信息(注:setVideoID为点播视频ID,直播为房间号)

    /***
     * 设置配置信息
     * @return Provider配置信息类
     * 注:setVideoID(String videoId)为点播视频ID,直播为房间号
     *    setVideoType(VideoType videoType)为视频类型，VideoType.VIDEOOS点播 VideoType.LIVEOS直播
     */
    @Override
    public Provider createProvider() {
        Provider provider = new Provider.Builder().setVideoID(String.valueOf(12)).setVideoType(VideoType.LIVEOS).build();
        return provider;
    }

    /***
     *
     * @return IMediaControlListener 平台方播放器相关状态
     * 注:     getVideoSize(int horVideoWidth, int horVideoHeight, int verVideoWidth, int verVideoHeight, int portraitSmallScreenOriginY)为视频播放器横竖屏Size(必填)
     *         getCurrentPosition()为播放器当前播放时间(单位:毫秒)，点播必须复写处理 直播无需此操作。
     */
    @Override
    public IMediaControlListener buildMediaController() {
        return new VideoOSMediaController() {
            @Override
            public VideoPlayerSize getVideoSize() {
                return new VideoPlayerSize(VenvyUIUtil.getScreenWidth(player.getContext()), VenvyUIUtil.getScreenHeight(player.getContext()),
                        VenvyUIUtil.getScreenWidth(player.getContext()), 200, 0);
            }

            @Override
            public long getCurrentPosition() {
                return player != null ? player.getCurrentPosition() : -1;
            }
        };
    }

    //广告展示监听
    @Override
    public IWidgetShowListener buildWidgetShowListener() {
        return super.buildWidgetShowListener();
    }

    //广告点击监听
    @Override
    public IWidgetClickListener buildWidgetClickListener() {
        return super.buildWidgetClickListener();
    }

    //广告关闭监听
    @Override
    public IWidgetCloseListener buildWidgetCloseListener() {
        return super.buildWidgetCloseListener();
    }

    //注册网络图片架构插件
    @Override
    public Class<? extends IImageLoader> buildImageLoader() {
        return GlideImageLoader.class;
    }

    //注册网络请求架构插件
    @Override
    public Class<? extends IRequestConnect> buildConnectProvider() {
        return cn.com.venvy.common.okhttp.OkHttpHelper.class;
    }

    //MQTT长连接结构插件
    @Override
    public Class<? extends ISocketConnect> buildSocketConnect() {
        return VenvyMqtt.class;
    }
}
