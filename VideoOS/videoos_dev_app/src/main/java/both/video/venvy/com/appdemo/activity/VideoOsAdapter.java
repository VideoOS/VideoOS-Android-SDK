package both.video.venvy.com.appdemo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.taobao.luaview.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.FullScreenWebViewDialog;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.common.bean.VideoFrameSize;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.ISvgaImageView;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetRotationListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.RotateStatus;
import cn.com.venvy.common.interf.VideoOSMediaController;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.mqtt.VenvyMqtt;
import cn.com.venvy.common.okhttp.OkHttpHelper;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.glide.GlideImageLoader;
import cn.com.venvy.svga.view.VenvySvgaImageView;
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

    private WedgeListener wedgeListener;
    private IOnWebViewDialogDismissCallback mDismissCallback;

    private long latestPosition = 0;

    // 本例中为了演示状态栏的影响，故通过getVideoPlayerSize()供UI层支持修改内容区Size（考虑状态栏，异形屏等），确保内容区始终为屏幕宽高
    private VideoPlayerSize videoPlayerSize =
            new VideoPlayerSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()),
                    VenvyUIUtil.getScreenHeight(MyApp.getInstance()),
                    VenvyUIUtil.getScreenWidth(MyApp.getInstance()),
                    VenvyUIUtil.dip2px(MyApp.getInstance(), 200));

    public VideoOsAdapter(StandardVideoOSPlayer mPlayer, boolean isLive) {
        this.mPlayer = mPlayer;
        this.isLive = isLive;
    }

    public void setWedgeListener(WedgeListener wedgeListener) {
        this.wedgeListener = wedgeListener;
    }

    public void setIOnWebViewDialogDismissCallback(IOnWebViewDialogDismissCallback callback) {
        mDismissCallback = callback;
    }

    /**
     * 外部可根据业务动态设置VideoPlaySize的值
     *
     * @return
     */
    public VideoPlayerSize getVideoPlayerSize(int fullScreenHeight) {
        if (fullScreenHeight > 0) {
            videoPlayerSize.mFullScreenContentHeight = fullScreenHeight;
        }
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
    public Provider generateProvider(String appKey, String appSecret, String videoId,
                                     String creativeName) {
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
        return generateProvider(ConfigUtil.getAppKey(), ConfigUtil.getAppSecret(),
                mPlayer.getPlayTag());
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

    @Override
    public IWidgetRotationListener buildWidgetRotationListener() {
        return new IWidgetRotationListener() {
            @Override
            public void onRotate(RotateStatus status) {
                if (status == RotateStatus.TO_VERTICAL) {
                    ((Activity) mPlayer.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (status == RotateStatus.TO_LANDSCAPE) {
                    ((Activity) mPlayer.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
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
        return wedgeListener != null ? wedgeListener : new WedgeListener() {
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
                    mPlayer.onVideoResume(false);
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
                if (mPlayer == null) {
                    return MediaStatus.PAUSE;
                }
                return mPlayer.getCurrentState() == GSYVideoView.CURRENT_STATE_PLAYING ? MediaStatus.PLAYING : MediaStatus.PAUSE;
            }

            /**
             * 直播无需复写。仅针对点播
             *
             * 这里有一个bug,在拖动视频的时候，播放器中间会固定返回一次0秒的数据
             * @return
             */
            @Override
            public long getCurrentPosition() {
                long position = mPlayer != null ? mPlayer.getCurrentPositionWhenPlaying() : -1;
                if (latestPosition > 0 && position <= 0) {
                    return latestPosition;
                }
                latestPosition = position;
//                VenvyLog.w("video position is : "+position);
                return position;
            }

            @Override
            public VideoPlayerSize getVideoSize() {
                return videoPlayerSize;
            }

            @Override
            public VideoFrameSize getVideoFrameSize() {
                return new VideoFrameSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()),
                        VenvyUIUtil.getScreenHeight(MyApp.getInstance()), 0, 0);
            }

            @Override
            public String getVideoEpisode() {
                return "";
            }

            @Override
            public String getVideoTitle() {
                return "";
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
                VenvyLog.i("widget action play video ^^^^^^^^^^^^^^^");
                if (mPlayer == null || mPlayer.getCurrentState() == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE) {
                    return;
                }
                if (!mPlayer.isInPlayingState()) {
                    // 视频未开始播放, 前贴结束后开始播放
                    mPlayer.startPlayLogic();
                } else {
                    mPlayer.onVideoResume(false);
                }
                break;
            //平台方打开H5事件
            case ACTION_OPEN_URL:
                String infoUrl = info.getUrl();
                if (infoUrl.contains("cv")) {   //本地播放器切换视频源
                    String[] split = infoUrl.split("\\|");
                    String videoUrl = split[1];
                    mPlayer.setUp(videoUrl, true, "");
                    mPlayer.setPlayTag(videoUrl);
                    mPlayer.startPlayLogic();
                }

                String linkUrl = info.getLinkUrl();
                if (!TextUtils.isEmpty(linkUrl)) {
                    loadUrl(linkUrl);
                }

                String deepLink = info.getDeepLink();
                if (!TextUtils.isEmpty(deepLink)) {
                    if (isPlayInstall(Uri.parse(deepLink))) {
                        // 第三方deepLink跳转
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mPlayer.getContext().startActivity(intent);
                    } else {
                        ToastUtil.showToast(mPlayer.getContext(), "对应app未安装");
                    }
                }

                String selfLink = info.getSelfLink();
                if (!TextUtils.isEmpty(selfLink)) {
                    // ignore for the moment
                }
                break;
            case ACTION_GET_ITEM:

                break;
        }
    }

    @Override
    public Class<? extends ISvgaImageView> buildSvgaImageView() {
        return VenvySvgaImageView.class;
    }

    private void loadUrl(String url) {
        VenvyLog.i("widget action load url : " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FullScreenWebViewDialog dialog = FullScreenWebViewDialog.getInstance(mPlayer.getContext());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //处理ACTION_OPEN_URL事件结束后 平台方需调用此事件 唤醒继续播放广告中插
                notifyMediaStatusChanged(MediaStatus.PLAYING);
                if (mDismissCallback != null) {
                    mDismissCallback.onDismiss();
                }
            }
        });
        dialog.loadUrl(url);
    }


    private boolean isPlayInstall(Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        ComponentName componentName = intent.resolveActivity(mPlayer.getContext().getPackageManager());
        return componentName != null;
    }

    public interface IOnWebViewDialogDismissCallback {
        void onDismiss();
    }
}
