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
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.StandardVideoOSPlayer;
import cn.com.venvy.VideoPositionHelper;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.Provider;
import cn.com.videopls.pub.os.VideoOsView;

public abstract class BasePlayerActivity extends AppCompatActivity {
    //Video++适配器
//    protected VideoPlusAdapter mVideoPlusAdapter;

    protected int mWidowPlayerHeight;
    protected ViewGroup mRootView;
//    protected VideoControllerView mController;
    //播放器类
//    protected CustomVideoView mCustomVideoView;


    protected StandardVideoOSPlayer mVideoPlayer;
    protected VideoOsView mVideoPlusView;
    protected OrientationUtils mOrientationUtils;
    protected VideoOsAdapter mAdapter;
    protected TextView tvVideoId;
    protected CheckBox cbShowStatusBar;
    protected static final String TAG_CREATIVE_NAME = "creativeName";

    private boolean isNavigationBarShow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRootView = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.activity_base_player, null);
        setContentView(mRootView);
        cbShowStatusBar = mRootView.findViewById(R.id.cbShowStatusBar);
        tvVideoId = mRootView.findViewById(R.id.tvVideoId);
        //播放器
        mVideoPlayer = mRootView.findViewById(R.id.player);
        mVideoPlusView = mRootView.findViewById(R.id.os_view);
        mAdapter = new VideoOsAdapter(mVideoPlayer, isLiveOS());
        mVideoPlusView.setVideoOSAdapter(mAdapter);

        tvVideoId.setText(ConfigUtil.getVideoId());
        // 设置旋转
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        initVideoPlayerSetting();

        cbShowStatusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleStatusBar(isChecked);
            }
        });
    }


    protected boolean isLiveOS() {
        return false;
    }


    private void toggleStatusBar(boolean isChecked) {
        if (isChecked) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
        }
        // 状态栏改变无法立刻获取内容区高度，需要得到重新渲染后获取才能得到准确值
        updateRootViewHeight(mVideoPlusView);
    }

    private void initVideoPlayerSetting() {
        //增加title
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
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
        mVideoPlayer.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                mVideoPlusView.stop();
                mAdapter.updateProvider(changeProvider(mVideoPlayer.getPlayTag()));
                mVideoPlusView.start();
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
        checkNavigationBarShow(mVideoPlusView);
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
        ViewGroup.LayoutParams osParams = mVideoPlusView.getLayoutParams();
        if (osParams == null) {
            osParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 手机竖屏
            cbShowStatusBar.setVisibility(View.VISIBLE);
            tvVideoId.setVisibility(View.VISIBLE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = VenvyUIUtil.dip2px(this, 200);
            //TODO : 通知SDK屏幕放心
            if (mAdapter != null) {
                // TODO : 先重新设置 VideoSize
                toggleStatusBar(cbShowStatusBar.isChecked());
                mAdapter.notifyVideoScreenChanged(ScreenStatus.SMALL_VERTICAL);
            }
        } else {
            // 手机横屏
            cbShowStatusBar.setVisibility(View.GONE);
            tvVideoId.setVisibility(View.GONE);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
            //TODO : 通知SDK屏幕放心
            if (mAdapter != null) {
                mAdapter.notifyVideoScreenChanged(ScreenStatus.LANDSCAPE);
            }
        }
        mVideoPlayer.setLayoutParams(params);
        mVideoPlusView.setLayoutParams(osParams);
    }


    private Provider changeProvider(String videoId) {
        return new Provider.Builder().setAppKey(ConfigUtil.getAppKey()).setAppSecret(ConfigUtil.getAppSecret())
                .setCustomUDID(String.valueOf(System.currentTimeMillis()))
                .setVideoID(videoId).build();
    }


    /**
     * 检查有无底部导航栏
     *
     * @param rootView
     */
    private void checkNavigationBarShow(final View rootView) {
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
//                Log.d("printSomeLog", "rootView height : " + rootView.getMeasuredHeight());
                isNavigationBarShow = rootView.getMeasuredHeight() < VenvyUIUtil.getScreenHeight(MyApp.getInstance());
                cbShowStatusBar.setChecked(true);// 触发onCheckChanged事件
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private void updateRootViewHeight(final View rootView) {
        if (VenvyUIUtil.isScreenOriatationPortrait(this)) {
            // 仅为竖屏的时候才获取高度
            rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
//                Log.d("printSomeLog", "updateRootViewHeight : " + rootView.getMeasuredHeight());
                    mAdapter.getVideoPlayerSize().mHorVideoHeight = rootView.getMeasuredHeight();
                    rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

}
