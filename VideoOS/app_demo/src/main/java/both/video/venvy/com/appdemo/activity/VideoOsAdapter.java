package both.video.venvy.com.appdemo.activity;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.FullScreenWebViewDialog;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.common.glide.GlideImageLoader;
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
import cn.com.videopls.pub.Provider;
import cn.com.videopls.pub.VideoPlusAdapter;

/**
 * 通过的VideoPlusAdapter
 * Created by Lucas on 2019/5/21.
 */
public class VideoOsAdapter extends VideoPlusAdapter {

    private static final String TAG = VideoOsAdapter.class.getSimpleName();

    private StandardVideoOSPlayer mPlayer;

    private boolean isLive;

    private VideoPlayerSize videoPlayerSize = new VideoPlayerSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()), VenvyUIUtil.getScreenHeight(MyApp.getInstance()),
            VenvyUIUtil.getScreenWidth(MyApp.getInstance()), VenvyUIUtil.dip2px(MyApp.getInstance(), 200), 0);

    public VideoOsAdapter(StandardVideoOSPlayer mPlayer, boolean isLive) {
        this.mPlayer = mPlayer;
        this.isLive = isLive;
    }


    public VideoPlayerSize getVideoPlayerSize() {
        return videoPlayerSize;
    }


    //设置参数
    @Override
    public Provider createProvider() {
        return new Provider.Builder().setAppKey(ConfigUtil.getAppKey()).setAppSecret(ConfigUtil.getAppSecret())
                .setVideoType(isLive ? VideoType.LIVEOS : VideoType.VIDEOOS)
                .setVideoID(mPlayer.getPlayTag()).build();
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

    //广告展示监听插件
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

    //广告点击监听插件
    @Override
    public IWidgetClickListener buildWidgetClickListener() {
        return new IWidgetClickListener<WidgetInfo>() {
            @Override
            public void onClick(@Nullable WidgetInfo info) {
                widgetAction(info);
            }
        };
    }

    //广告关闭监听插件
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

    //平台方播放器相关状态
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
