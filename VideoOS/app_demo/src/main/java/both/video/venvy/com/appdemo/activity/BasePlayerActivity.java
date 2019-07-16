package both.video.venvy.com.appdemo.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.VideoPositionHelper;
import cn.com.venvy.common.interf.ScreenStatus;
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
    private static final String DEFAULT_VIDEO = "http://qa-video.oss-cn-beijing.aliyuncs.com/mp4/mby02.mp4";
    protected ViewGroup mRootView; //  Activity 根布局
    protected StandardVideoOSPlayer mVideoPlayer; // 播放器控件
    protected VideoOsView mVideoPlusView; // VideoOs 视图（填充根布局）
    protected VideoOsAdapter mAdapter;// VideoOS 视图适配器
    protected OrientationUtils mOrientationUtils;
    protected CheckBox cbShowStatusBar; // 控制是否显示状态栏
    private boolean isFirstPlayVideo = true;
    private boolean isFirstDraw = true;

    private int statusBarHeight;
    private int osViewHasStatusHeight;// 有状态栏时的高度
    private int osViewNotIncludeHeight;// 状态栏隐藏时的高度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRootView = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.activity_base_player, null);
        setContentView(mRootView);

        // Step1 : 初始化VideoOsView和播放器相关控件
        initViews();

        // Step2 : 为VideoOsView设置Adapter
        mAdapter = new VideoOsAdapter(mVideoPlayer, isLiveOS());
        mVideoPlusView.setVideoOSAdapter(mAdapter);

        // Step3 : 在播放器的相关CallBack中启动VideoOsView  ---  mVideoPlusView.start()
        initVideoPlayerSetting();

        // Step4 : 启动播放
        startDefaultVideo(null);

        statusBarHeight = VenvyUIUtil.getStatusBarHeight(this);
        // 默认显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏

        cbShowStatusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleStatusBar(isChecked);
            }
        });

    }

    private void initViews() {
        cbShowStatusBar = mRootView.findViewById(R.id.cbShowStatusBar);
        mVideoPlayer = mRootView.findViewById(R.id.player);
        mVideoPlusView = mRootView.findViewById(R.id.os_view);
    }

    /**
     * 默认自动播放上次缓存的VideoId,子类有需要可自己实现
     */
    protected void startDefaultVideo(String videoId) {
        if (isLiveOS()) {
            // 直播
            mVideoPlayer.setUp(LIVE_DEFAULT_VIDEO, true, ConfigUtil.getVideoName());
        } else {
            // 点播
            mVideoPlayer.setUp(DEFAULT_VIDEO, true, ConfigUtil.getVideoName());
        }
        mVideoPlayer.setPlayTag(TextUtils.isEmpty(videoId) ? ConfigUtil.getVideoId() : videoId);
        mVideoPlayer.startPlayLogic();

    }


    /**
     * 子类实现该方法表示当前业务是否为直播
     *
     * @return
     */
    protected boolean isLiveOS() {
        return false;
    }


    private void toggleStatusBar(boolean isChecked) {
        if (isChecked) {
            mAdapter.getVideoPlayerSize().mFullScreenContentHeight = osViewHasStatusHeight;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        } else {
            mAdapter.getVideoPlayerSize().mFullScreenContentHeight = osViewNotIncludeHeight;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        }

        // 横竖屏切换无法立刻获取内容区高度，需要得到重新渲染后获取才能得到准确值
//        updateRootViewHeight(mVideoPlusView);
    }

    private void initVideoPlayerSetting() {
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer); // 设置旋转
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);  //增加title
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);//设置返回键
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        mVideoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        mVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                    mAdapter.updateProvider(mAdapter.generateProvider(ConfigUtil.getAppKey(), ConfigUtil.getAppSecret(), ConfigUtil.getVideoId()));
//                    mVideoPlusView.start();
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

            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickResume(String url, Object... objects) {

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
        calculateHeight(mVideoPlusView);
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
        ViewGroup.LayoutParams params = mVideoPlayer.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 手机竖屏
            cbShowStatusBar.setVisibility(View.VISIBLE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = VenvyUIUtil.dip2px(this, 200);
            // 竖屏根据状态栏设置，重新设置VideoOSView的Size,
            toggleStatusBar(cbShowStatusBar.isChecked());
            if (mAdapter != null) {
                mAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
            }
        } else {
            // 手机横屏

            cbShowStatusBar.setVisibility(View.GONE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //横屏隐藏状态栏
            // 横屏状态下，重置内容区为屏幕宽度
            mAdapter.getVideoPlayerSize().mFullScreenContentHeight = VenvyUIUtil.getScreenWidth(MyApp.getInstance());
            if (mAdapter != null) {
                mAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
            }
        }
        mVideoPlayer.setLayoutParams(params);
    }


    private void calculateHeight(final View rootView) {
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
//                Log.d("printSomeLog", "rootView height : " + rootView.getMeasuredHeight());
                osViewHasStatusHeight = rootView.getMeasuredHeight();
                osViewNotIncludeHeight = osViewHasStatusHeight + statusBarHeight;
                cbShowStatusBar.setChecked(true);
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }
}
