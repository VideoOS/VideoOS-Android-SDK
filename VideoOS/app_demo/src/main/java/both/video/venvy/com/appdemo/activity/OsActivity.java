package both.video.venvy.com.appdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import org.json.JSONObject;
import org.luaj.vm2.ast.Str;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;
import both.video.venvy.com.appdemo.helper.MyMediaHelper;
import both.video.venvy.com.appdemo.utils.AssetsUtil;
import both.video.venvy.com.appdemo.widget.FullScreenWebViewDialog;
import both.video.venvy.com.appdemo.widget.VideoControllerView;
import both.video.venvy.com.appdemo.widget.VideoOsConfigDialog;
import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.mqtt.VenvyMqtt;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyRandomUtils;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.Provider;
import cn.com.videopls.pub.VideoPlusAdapter;
import cn.com.videopls.pub.VideoPlusView;
import cn.com.videopls.pub.os.VideoOsView;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class OsActivity extends BasePlayerActivity implements View.OnClickListener {

    private VideoOsConfigDialog mConfigDialog;
    private String mVideoId, mCreativeName;
    private String userName, userPwd;
    private IPlatformLoginInterface.LoginCallback mLoginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View settingView = getSettingView();
        mRootView.addView(settingView);
        mConfigDialog = new VideoOsConfigDialog(this, VideoType.VIDEOOS);
        mConfigDialog.onChangeListener(new VideoOsConfigDialog.SettingChangedListener() {
            @Override
            public void onChangeStat(ConfigBean bean) {
                if (mVideoPlusAdapter == null || mVideoPlusView == null)
                    return;
                if (bean == null) {
                    Toast.makeText(OsActivity.this, "配置错误，请确认你输入的配置信息", Toast.LENGTH_LONG).show();
                    return;
                }
                //正在播放视频需要切集操作调用逻辑 没有必须重新创建VideoPlusView 以及VideoPlusAdapter
                mVideoPlusView.stop();
                mVideoId = bean.getVideoId();
                mCreativeName = bean.getCreativeName();
                mCustomVideoView.startPlay(mVideoId);
                mVideoPlusAdapter.updateProvider(changeProvider(mVideoId, mCreativeName));
                mVideoPlusView.start();
            }
        });
        mVideoId = mConfigDialog.getVideoId();
        mCreativeName = mConfigDialog.getCreativeName();
        mVideoPlusAdapter = initVideoPlusAdapter();
        mVideoPlusView.setVideoOSAdapter(mVideoPlusAdapter);
        mVideoPlusView.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (resultCode) {
            case RESULT_OK:
                userName = data.getStringExtra("userName");
                userPwd = data.getStringExtra("userPwd");
                if (mLoginCallback != null) {
                    PlatformUserInfo info = new PlatformUserInfo();
                    info.setUserName(userName);
                    info.setUid(userName);
                    mLoginCallback.loginSuccess(info);
                }
                break;
        }
    }

    @Override
    protected int getVideoType() {
        return VideoControllerView.VIDEO_OS;
    }

    @NonNull
    @Override
    protected VideoPlusView initVideoPlusView() {
        return new VideoOsView(this);
    }

    @NonNull
    @Override
    protected VideoPlusAdapter initVideoPlusAdapter() {
        return new VideoOsAdapter();
    }

    @Override
    protected void setVideoFullScreen() {
        super.setVideoFullScreen();
        if (mVideoPlusAdapter != null) {
            mVideoPlusAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
        }
    }

    @Override
    protected void setVideoVerticalScreen() {
        super.setVideoVerticalScreen();
        if (mVideoPlusAdapter != null) {
            mVideoPlusAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
        }
    }

    @Override
    public void onClick(View v) {
        int ID = v.getId();
        if (R.id.iv_os_setting == ID) {
            mConfigDialog.showOsSetting();
        } else if (R.id.bt_os_setting_mall == ID) {
            if (mVideoPlusView == null)
                return;
            mVideoPlusView.stop();
            Uri uri = Uri.parse("LuaView://defaultLuaView?template=os_red_envelope_hotspot111.lua&id=os_wedge");
//            Uri uri = Uri.parse("LuaView://defaultLuaView?template=os_card_hotspot.lua&id=os_bubble_hotspot");
            HashMap<String, String> params = new HashMap<>();
//            params.put("data", AssetsUtil.readFileAssets("local_bubble.json", OsActivity.this));
            params.put("data", AssetsUtil.readFileAssets("local_red.json", OsActivity.this));
            mVideoPlusView.navigation(uri, params, new IRouterCallback() {
                @Override
                public void arrived() {
                    mVideoPlusView.start();
                }

                @Override
                public void lost() {

                }
            });
        } else if (R.id.bt_os_setting_close_window == ID) {
            if (mVideoPlusView == null)
                return;
            mVideoPlusView.closeInfoView();
        }

    }

    private class VideoOsAdapter extends VideoPlusAdapter {
        private MyMediaHelper mMediaController;

        public VideoOsAdapter() {
            if (mMediaController == null) {
                mMediaController = new MyMediaHelper();
            }
            mMediaController.setMediaPlayerControl(mCustomVideoView);
            mMediaController.setVideoSize(new VideoPlayerSize(VenvyUIUtil.getScreenWidth(OsActivity.this), VenvyUIUtil.getScreenHeight(OsActivity.this),
                    VenvyUIUtil.getScreenWidth(OsActivity.this), mWidowPlayerHeight, VenvyUIUtil.getStatusBarHeight(OsActivity.this)));
        }

        //设置参数
        @Override
        public Provider createProvider() {
            return changeProvider(mVideoId, mCreativeName);
        }

        //注册网络图片架构插件
        @Override
        public Class<? extends IImageLoader> buildImageLoader() {
            return cn.com.venvy.common.glide.GlideImageLoader.class;
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

        @Override
        public WedgeListener buildWedgeListener() {
            return new WedgeListener() {
                @Override
                public void goBack() {
                    Toast.makeText(OsActivity.this, "中插返回按钮处理", Toast.LENGTH_LONG).show();
                }
            };
        }

        //平台方播放器相关状态
        @Override
        public IMediaControlListener buildMediaController() {
            return mMediaController;
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
                    if (mCustomVideoView != null) {
                        mCustomVideoView.mediaPlayerPause();
                    }
                    break;
                //平台方重新开启播放器事件
                case ACTION_PLAY_VIDEO:
                    if (mCustomVideoView != null) {
                        mCustomVideoView.mediaPlayerStart();
                    }
                    break;
                //平台方打开H5事件
                case ACTION_OPEN_URL:
                    if (TextUtils.isEmpty(url))
                        return;
                    loadUrl(url);
                    break;
                case ACTION_GET_ITEM:
                    Toast.makeText(OsActivity.this, url, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /***
     * 底部设置控件
     */
    private View getSettingView() {
        ConstraintLayout mSettingView = null;
        if (mSettingView == null) {
            mSettingView = (ConstraintLayout) LayoutInflater.from(this)
                    .inflate(R.layout.layout_os_setting_button, mRootView, false);
            mSettingView.findViewById(R.id.iv_os_setting).setOnClickListener(this);
            mSettingView.findViewById(R.id.bt_os_setting_mall).setOnClickListener(this);
            mSettingView.findViewById(R.id.bt_os_setting_close_window).setOnClickListener(this);
        }
        return mSettingView;
    }

    private Provider changeProvider(String videoId, String creativeName) {
        Provider provider;
        if (TextUtils.isEmpty(creativeName)) {
            provider = new Provider.Builder().setVideoType(VideoType.VIDEOOS).setCustomUDID(System.currentTimeMillis() + VenvyRandomUtils.getRandomNumbersAndLetters(10))
                    .setVideoID(videoId)//视频地址
                    .build();
        } else {
            Map<String, String> extendParams = new HashMap<>();
            extendParams.put(TAG_CREATIVE_NAME, creativeName);
            provider = new Provider.Builder().setVideoType(VideoType.VIDEOOS).setCustomUDID(System.currentTimeMillis() + VenvyRandomUtils.getRandomNumbersAndLetters(10))
                    .setVideoID(videoId)//视频地址
                    .setExtendJSONString(new JSONObject(extendParams).toString()).build();
        }
        return provider;
    }

    /***
     *
     * @param url 广告 ACTION_OPEN_URL事件处理
     *            ACTION_OPEN_URL为用户操作广告SDK通知事件传递出URL 平台方自行实现UI 操作逻辑等
     */
    private void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FullScreenWebViewDialog dialog = FullScreenWebViewDialog.getInstance(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //处理ACTION_OPEN_URL事件结束后 平台方需调用此事件 唤醒继续播放广告中插
                if (mVideoPlusAdapter != null) {
                    mVideoPlusAdapter.notifyMediaStatusChanged(MediaStatus.PLAYING);
                }
            }
        });
        dialog.loadUrl(url);
    }
}
