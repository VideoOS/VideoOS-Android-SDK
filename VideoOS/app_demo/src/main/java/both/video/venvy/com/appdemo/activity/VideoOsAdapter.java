package both.video.venvy.com.appdemo.activity;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.FullScreenWebViewDialog;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.VideoOSMediaController;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.mqtt.VenvyMqtt;
import cn.com.venvy.common.okhttp.OkHttpHelper;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.glide.GlideImageLoader;
import cn.com.videopls.pub.Provider;
import cn.com.videopls.pub.VideoPlusAdapter;

/**
 * 通过的VideoPlusAdapter
 * Created by Lucas on 2019/5/21.
 */
public class VideoOsAdapter extends VideoPlusAdapter {
    private static final String TAG = VideoOsAdapter.class.getSimpleName();
    protected static final String TAG_CREATIVE_NAME = "creativeName";
    private StandardVideoOSPlayer mPlayer;
    private boolean isLive; // 是否为直播

    // 本例中为了演示状态栏的影响，故通过getVideoPlayerSize()供UI层支持修改内容区Size（考虑状态栏，异形屏等），确保内容区始终为屏幕宽高
    private VideoPlayerSize videoPlayerSize = new VideoPlayerSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()), VenvyUIUtil.getScreenHeight(MyApp.getInstance()),
            VenvyUIUtil.getScreenWidth(MyApp.getInstance()), VenvyUIUtil.dip2px(MyApp.getInstance(), 200));

    public VideoOsAdapter(StandardVideoOSPlayer mPlayer, boolean isLive) {
        this.mPlayer = mPlayer;
        this.isLive = isLive;
    }

    /**
     * 外部可根据业务动态设置VideoPlaySize的值
     *
     * @return
     */
    public VideoPlayerSize getVideoPlayerSize() {
        return videoPlayerSize;
    }


    /**
     * 统一通过此方法生成SDK所需的Provider
     *
     * @param appKey
     * @param appSecret
     * @param videoId
     * @return
     */
    public Provider generateProvider(String appKey, String appSecret, String videoId) {
        return generateProvider(appKey, appSecret, videoId, null);
    }

    /**
     * @param appKey
     * @param appSecret
     * @param videoId      视频源
     * @param creativeName 素材名称
     * @return
     */
    public Provider generateProvider(String appKey, String appSecret, String videoId, String creativeName) {
        if (TextUtils.isEmpty(creativeName)) {
            return new Provider.Builder().setAppKey(appKey).setAppSecret(appSecret)
                    .setVideoType(isLive ? VideoType.LIVEOS : VideoType.VIDEOOS)
                    .setCustomUDID(String.valueOf(System.currentTimeMillis()))
                    .setVideoID(videoId).build();
        } else {
            Map<String, String> extendParams = new HashMap<>();
            extendParams.put(TAG_CREATIVE_NAME, creativeName);
            return new Provider.Builder().setAppKey(appKey).setAppSecret(appSecret)
                    .setVideoType(isLive ? VideoType.LIVEOS : VideoType.VIDEOOS)
                    .setCustomUDID(String.valueOf(System.currentTimeMillis()))
                    .setVideoID(videoId)
                    .setExtendJSONString(new JSONObject(extendParams).toString()).build();
        }

    }

    //设置参数
    @Override
    public Provider createProvider() {
        return generateProvider(ConfigUtil.getAppKey(), ConfigUtil.getAppSecret(), mPlayer.getPlayTag());
    }


    //注册网络图片架构插件
    @Override
    public Class<? extends IImageLoader> buildImageLoader() {
        return GlideImageLoader.class;
    }

    //注册网络请求架构插件
    @Override
    public Class<? extends IRequestConnect> buildConnectProvider() {
        return OkHttpHelper.class;
    }

    //MQTT长连接结构插件
    @Override
    public Class<? extends ISocketConnect> buildSocketConnect() {
        return VenvyMqtt.class;
    }

    /**
     * 广告展示监听
     *
     * @return
     */
    @Override
    public IWidgetShowListener buildWidgetShowListener() {
        return new IWidgetShowListener<WidgetInfo>() {
            @Override
            public void onShow(WidgetInfo info) {
                //展示回调方法
                if (info == null)
                    return;
                widgetAction(info);
            }
        };
    }

    /**
     * 广告点击监听
     */
    @Override
    public IWidgetClickListener buildWidgetClickListener() {
        return new IWidgetClickListener<WidgetInfo>() {
            @Override
            public void onClick(@Nullable WidgetInfo info) {
                widgetAction(info);
            }
        };
    }

    /**
     * 广告关闭监听
     *
     * @return
     */
    @Override
    public IWidgetCloseListener buildWidgetCloseListener() {
        return new IWidgetCloseListener<WidgetInfo>() {
            @Override
            public void onClose(WidgetInfo info) {
                widgetAction(info);
            }
        };
    }

    /**
     * 中插back按钮点击回调
     *
     * @return
     */
    @Override
    public WedgeListener buildWedgeListener() {
        return new WedgeListener() {
            @Override
            public void goBack() {

            }
        };
    }

    /**
     * 平台方播放器相关业务状态
     */
    @Override
    public IMediaControlListener buildMediaController() {
        return new VideoOSMediaController() {
            @Override
            public void start() {
                if (mPlayer != null) {
                    mPlayer.onVideoResume();
                }
            }

            @Override
            public void pause() {
                if (mPlayer != null) {
                    mPlayer.onVideoPause();
                }
            }

            @Override
            public MediaStatus getCurrentMediaStatus() {
                return mPlayer == null ? MediaStatus.PAUSE : mPlayer.isInPlayingState() ? MediaStatus.PLAYING : MediaStatus.PAUSE;
            }

            /**
             * 直播无需复写。仅针对点播
             * @return
             */
            @Override
            public long getCurrentPosition() {
                return mPlayer != null ? mPlayer.getCurrentPositionWhenPlaying() : -1;
            }

            @Override
            public VideoPlayerSize getVideoSize() {
                return videoPlayerSize;
            }
        };
    }

    /***
     * 处理广告行为
     * @param info
     */
    private void widgetAction(WidgetInfo info) {
        //注(actionType为广告出现，销毁，点击等等需要平台方处理事件类型)
        WidgetInfo.WidgetActionType actionType = info.getWidgetActionType();
        String url = info.getUrl();
        switch (actionType) {
            case ACTION_NONE:
                break;
            //平台方暂停播放器事件
            case ACTION_PAUSE_VIDEO:
                if (mPlayer != null) {
                    mPlayer.onVideoPause();
                }
                break;
            //平台方重新开启播放器事件
            case ACTION_PLAY_VIDEO:
                if (mPlayer != null) {
                    mPlayer.onVideoResume();
                }
                break;
            //平台方打开H5事件
            case ACTION_OPEN_URL:
                if (TextUtils.isEmpty(url))
                    return;
                loadUrl(url);
                break;
            case ACTION_GET_ITEM:

                break;
        }
    }

    private void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FullScreenWebViewDialog dialog = FullScreenWebViewDialog.getInstance(mPlayer.getContext());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //处理ACTION_OPEN_URL事件结束后 平台方需调用此事件 唤醒继续播放广告中插
                notifyMediaStatusChanged(MediaStatus.PLAYING);
            }
        });
        dialog.loadUrl(url);
    }
}
