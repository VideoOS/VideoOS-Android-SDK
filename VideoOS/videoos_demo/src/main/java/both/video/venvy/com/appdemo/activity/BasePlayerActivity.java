package both.video.venvy.com.appdemo.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.taobao.luaview.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.VideoPositionHelper;
import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.os.VideoOsView;

/**
 * SDK 集成步骤
 * 1、在layout文件中确保VideoOsView宽高填充整个屏幕（match_parent）
 * 2、在Activity、Fragment启动相关生命周期中（通常是onCreate），按照文档或者Step注释的顺序启动VideoOsView
 * 3、需要复写Activity的 onConfigurationChanged函数，以处理播放器横竖屏切换的逻辑
 * 4、onDestroy（）中释放相关资源避免造成内存泄漏
 */
public abstract class BasePlayerActivity extends AppCompatActivity {
    private static final String TAG = BasePlayerActivity.class.getSimpleName();
    private static final String LIVE_DEFAULT_VIDEO = "http://qa-video.oss-cn-beijing.aliyuncs.com/ai/buRan.mp4";
    private static final String DEFAULT_VIDEO = "https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/test/SwordArtOnlineAlicization22.mp4";
    protected ViewGroup mRootView; //  Activity 根布局
    protected StandardVideoOSPlayer mVideoPlayer; // 播放器控件
    protected VideoOsView mVideoPlusView; // VideoOs 视图（填充根布局）
    protected VideoOsAdapter mAdapter;// VideoOS 视图适配器
    protected OrientationUtils mOrientationUtils;
    protected CheckBox cbShowStatusBar; // 控制是否显示状态栏
    private boolean isFirstPlayVideo = true;
    protected TextView tvVideoId; // 当前VideoId
    protected SVGAImageView imgSvga;

    private SVGAParser svgaParser;
    private int statusBarHeight;
    private ServiceType mCurrentServiceType;
    private int hideStatusBarHeight = -1;// 状态栏隐藏时的高度
    private int existStatusBarHeight = -1;// 有状态栏时的高度

    private boolean isVisionMode = false; // 是否是视联网模式
    private boolean isAutoLaunchVisionMode = true;// 是否自动打开视联网模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRootView = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.activity_base_player, null);
        setContentView(mRootView);
        svgaParser = new SVGAParser(this);
        // Step1 : 初始化VideoOsView和播放器相关控件
        initViews();

        // Step2 : 为VideoOsView设置Adapter
        mAdapter = new VideoOsAdapter(mVideoPlayer, isLiveOS());
        mAdapter.setWedgeListener(new WedgeListener() {
            @Override
            public void goBack() {
                finish();
            }
        });
        mAdapter.setIOnWebViewDialogDismissCallback(new VideoOsAdapter.IOnWebViewDialogDismissCallback() {
            @Override
            public void onDismiss() {
                mVideoPlusView.reResumeService(mCurrentServiceType);
            }
        });
        mVideoPlusView.setVideoOSAdapter(mAdapter);

        // Step3 : 在播放器的相关CallBack中启动VideoOsView  ---  mVideoPlusView.start()
        initVideoPlayerSetting();

        // Step4 : 启动播放
        startDefaultVideo(null, null);

        statusBarHeight = VenvyUIUtil.getStatusBarHeight(this);

        hideStatusBarHeight = VenvyUIUtil.getScreenHeight(MyApp.getInstance());
        existStatusBarHeight = hideStatusBarHeight - statusBarHeight;

        // 默认显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        cbShowStatusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAdapter.getVideoPlayerSize(existStatusBarHeight);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
                } else {
                    mAdapter.getVideoPlayerSize(hideStatusBarHeight);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
                }
            }
        });
        cbShowStatusBar.setChecked(true);


        tvVideoId.setText(ConfigUtil.getVideoId());

    }

    private void initViews() {
        cbShowStatusBar = mRootView.findViewById(R.id.cbShowStatusBar);
        tvVideoId = mRootView.findViewById(R.id.tvVideoId);
        mVideoPlayer = mRootView.findViewById(R.id.player);
        mVideoPlusView = mRootView.findViewById(R.id.os_view);
        imgSvga = mRootView.findViewById(R.id.imgSvga);
        imgSvga.setVisibility(View.GONE);
    }

    /**
     * 默认自动播放上次缓存的VideoId,子类有需要可自己实现
     */
    protected void startDefaultVideo(String videoId, String videoUrl) {
        if (isLiveOS()) {
            // 直播
            mVideoPlayer.setUp(TextUtils.isEmpty(videoUrl) ? ConfigUtil.getVideoName() : videoUrl, true, ConfigUtil.getVideoName());
        } else {
            // 点播
            mVideoPlayer.setUp(TextUtils.isEmpty(videoUrl) ? ConfigUtil.getVideoName() : videoUrl, true, ConfigUtil.getVideoName());
        }
        mVideoPlayer.setPlayTag(TextUtils.isEmpty(videoId) ? ConfigUtil.getVideoId() : videoId);
        mVideoPlusView.start();
        // 开启前贴
        startMixStandAd(ServiceType.ServiceTypeFrontVideo);
//        mVideoPlayer.startPlayLogic();
    }


    /**
     * 子类实现该方法表示当前业务是否为直播
     *
     * @return
     */
    protected boolean isLiveOS() {
        return false;
    }

    /**
     * 开启前贴, 前贴结束后会开始播放正片
     */
    private void startMixStandAd(final ServiceType type) {
        mCurrentServiceType = type;
        HashMap<String, String> params = new HashMap<>();
        params.put(VenvySchemeUtil.QUERY_PARAMETER_DURATION, "60");
        mVideoPlusView.startService(type, params, new IServiceCallback() {
            @Override
            public void onCompleteForService() {

            }

            @Override
            public void onFailToCompleteForService(Throwable throwable) {
                if (type == ServiceType.ServiceTypeFrontVideo) {
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mVideoPlayer.startPlayLogic();
                        }
                    });
                }
            }
        });
    }


    private void initVideoPlayerSetting() {
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer); // 设置旋转
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);  //增加title
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);//设置返回键
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHorizontal()) {
                    // 横屏则处理视联网模式开关,如果正常动画中则不处理这次点击事件，防止快速连续拨动开关
                    if (imgSvga.isAnimating()) return;

                    if (isVisionMode) {
                        mVideoPlusView.stopService(ServiceType.ServiceTypeVideoMode);
                        isVisionMode = false;
                        updateVisionModeStatus();
                        startVisionModeAnim("scan_off.svga");
                    } else {
                        mVideoPlusView.startService(ServiceType.ServiceTypeVideoMode, new HashMap<String, String>(), new IServiceCallback() {

                            @Override
                            public void onCompleteForService() {
                                VenvyLog.d("onCompleteForService");
                                isVisionMode = true;
                                updateVisionModeStatus();
                                startVisionModeAnim("scan_on.svga");
                            }

                            @Override
                            public void onFailToCompleteForService(Throwable throwable) {
                                VenvyLog.d("onFailToCompleteForService");
                            }
                        });
                    }

                } else {
                    // 竖屏处理屏幕方向切换
                    mOrientationUtils.resolveByClick();
                }
            }
        });
        //是否可以滑动调整
        mVideoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        mVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHorizontal()) {
                    // 横屏处理 切回竖屏
                    mOrientationUtils.resolveByClick();
                } else {
                    // 竖屏处理返回
                    onBackPressed();
                }
            }
        });
        // 在播放器的回调用启动OsView
        mVideoPlayer.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {
                // 播放器播放视频前的回调
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                if (isFirstPlayVideo) {
//                    Log.d(TAG, "onStartPrepared : First");
                    // 首次播放，只需调用start启动
//                    mVideoPlusView.start();
                    isFirstPlayVideo = false;
                } else {
//                    Log.d(TAG, "onStartPrepared : Second + ");
                    // 非首次播放，在此demo种视为切集操作
                    mVideoPlusView.stop();
                    mAdapter.updateProvider(mAdapter.generateProvider(ConfigUtil.getAppKey(),
                            ConfigUtil.getAppSecret(), ConfigUtil.getVideoId()));
                    mVideoPlusView.start();
                }

            }

            @Override
            public void onPrepared(String url, Object... objects) {

            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {

            }

            @Override
            public void onClickStartError(String url, Object... objects) {

            }

            @Override
            public void onClickStop(String url, Object... objects) {
                // 暂停广告
                VenvyLog.i("videoCallBack onClickStop ----");
                mVideoPlusView.startService(ServiceType.ServiceTypePauseAd,
                        new HashMap<String, String>(), null);
            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickResume(String url, Object... objects) {
                VenvyLog.i("videoCallBack onClickResume ----");
                // 关闭暂停广告
                mVideoPlusView.stopService(ServiceType.ServiceTypePauseAd);
            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {

            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                VenvyLog.i("videoCallBack onAutoComplete -----------");
                // 播放后贴
                startMixStandAd(ServiceType.ServiceTypeLaterVideo);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {

            }

            @Override
            public void onPlayError(String url, Object... objects) {

            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {

            }

            @Override
            public void onClickBlank(String url, Object... objects) {

            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoPlayer != null) {
            mVideoPlayer.onVideoPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoPlayer != null) {
            mVideoPlayer.onVideoResume();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //先返回正常状态
        if (mOrientationUtils != null && mVideoPlayer != null) {
            if (mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mVideoPlayer.getFullscreenButton().performClick();
                return;
            }
            //释放所有
            mVideoPlayer.setVideoAllCallBack(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationUtils != null) {
            mOrientationUtils.releaseListener();
        }
        if (mVideoPlusView != null) {
            // cancel getCurrentPosition()
            mAdapter.setIOnWebViewDialogDismissCallback(null);
            VideoPositionHelper.getInstance().cancel();
            mVideoPlusView.stop();
        }
    }

    /***
     *
     * @param newConfig 屏幕切换回调此生命周期
     *                  需要调VideoPlusAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
     *                  其中ScreenStatus.LANDSCAPE横屏
     *                     ScreenStatus.SMALL_VERTICAL 竖小屏
     *                     ScreenStatus.FULL_VERTICAL  竖全屏
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoPlayer == null) {
            return;
        }

        if (imgSvga.isAnimating()) {
            // 正在执行动画的过程中如果触发了屏幕切换，则中断动态
            hideSvgaImg();
        }

        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 手机竖屏
            hideSvgaImg();
            mVideoPlayer.getFullscreenButton().setImageResource(R.drawable.video_enlarge);


            cbShowStatusBar.setVisibility(View.VISIBLE);
            tvVideoId.setVisibility(View.VISIBLE);

            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = VenvyUIUtil.dip2px(this, 200);

            // 竖屏根据状态栏设置，重新设置VideoOSView的Size
            cbShowStatusBar.setChecked(cbShowStatusBar.isChecked());

            if (mAdapter != null) {
                mAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
            }
        } else {
            // 手机横屏

            tryToLaunchVisionMode();


            tvVideoId.setVisibility(View.GONE);
            // 竖屏状态下获得的高度不包含状态栏，切换到横屏需要更新一下VideoPlayerSize的高
            mAdapter.getVideoPlayerSize(VenvyUIUtil.getScreenWidth(MyApp.getInstance()));
            cbShowStatusBar.setVisibility(View.GONE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //横屏隐藏状态栏
            if (mAdapter != null) {
                mAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
            }
        }
        mVideoPlayer.setLayoutParams(params);
    }


    private boolean isHorizontal() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void hideSvgaImg() {
        imgSvga.clearAnimation();
        imgSvga.setVisibility(View.GONE);
    }


    private void tryToLaunchVisionMode() {
        if (!isVisionMode && isAutoLaunchVisionMode) {
            mVideoPlayer.getFullscreenButton().performClick();
            isAutoLaunchVisionMode = false;
        } else {
            updateVisionModeStatus();
        }
    }

    private void updateVisionModeStatus() {
        mVideoPlayer.getFullscreenButton().setImageResource(isVisionMode ? R.mipmap.vision_mode_on : R.mipmap.vision_mode_off);
    }

    protected void startVisionModeAnim(String assetName) {
        imgSvga.setVisibility(View.VISIBLE);
        imgSvga.clearAnimation();
        svgaParser.parse(assetName, new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity svgaVideoEntity) {
                imgSvga.setVideoItem(svgaVideoEntity);
                imgSvga.setLoops(1); // 动画只执行一次
                imgSvga.startAnimation();

                imgSvga.setCallback(new SVGACallback() {
                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onFinished() {
                        hideSvgaImg();
                    }

                    @Override
                    public void onRepeat() {

                    }

                    @Override
                    public void onStep(int i, double v) {

                    }
                });
            }

            @Override
            public void onError() {
                imgSvga.setVisibility(View.GONE);
            }
        });
    }


}
