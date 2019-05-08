package both.video.venvy.com.appdemo.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ScreenOrientationSwitcher;
import both.video.venvy.com.appdemo.widget.CustomVideoView;
import both.video.venvy.com.appdemo.widget.VideoControllerView;
import cn.com.venvy.VideoPositionHelper;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.videopls.pub.VideoPlusAdapter;
import cn.com.videopls.pub.VideoPlusView;

public abstract class BasePlayerActivity extends AppCompatActivity implements VideoControllerView
        .IVideoControllerListener {
    //Video++适配器
    protected VideoPlusAdapter mVideoPlusAdapter;
    //Video++
    protected VideoPlusView mVideoPlusView;

    protected int mWidowPlayerHeight;
    protected ViewGroup mRootView;
    protected VideoControllerView mController;
    //播放器类
    protected CustomVideoView mCustomVideoView;
    //播放器Content
    protected FrameLayout mVideoContentView;
    //自动选择屏幕类
    private ScreenOrientationSwitcher mScreenOrientationSwitcher;
    protected static final String TAG_CREATIVE_NAME = "creativeName";
    protected final String appKey = "96d4245d-427f-4964-bbe1-3b3b6ccaf7e1";
    protected final String appSecret = "ebb53e815fa9458d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.activity_base_player, null);
        setContentView(mRootView);
        initMediaPlayer();
        //Video++实例化 可通过XML配置
        mVideoPlusView = initVideoPlusView();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRootView.addView(mVideoPlusView, layoutParams);
        if (mController != null) {
            mController.bringToFront();
        }
        initOrientationSwitcher();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCustomVideoView != null) {
            mCustomVideoView.stopPlaying();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlusView != null) {
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
        if (mCustomVideoView != null && mController != null) {
            mController.onConfigurationChanged(newConfig);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoFullScreen();
        } else {
            setVideoVerticalScreen();
        }
    }

    @NonNull
    protected abstract VideoPlusView initVideoPlusView();

    @NonNull
    protected abstract VideoPlusAdapter initVideoPlusAdapter();

    protected abstract int getVideoType();

    protected void initMediaPlayer() {
        mWidowPlayerHeight = VenvyUIUtil.dip2px(this, 195);
        mVideoContentView = (FrameLayout) findViewById(R.id.root);
        mCustomVideoView = new CustomVideoView(this);
        mCustomVideoView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, mWidowPlayerHeight));
        mVideoContentView.addView(mCustomVideoView);
        mController = new VideoControllerView(this);
        mController.setVideoControllerListener(this);
        mController.setVideoType(getVideoType());
        mCustomVideoView.setMediaController(mController);
    }


    protected void setVideoFullScreen() {
        ViewGroup.LayoutParams params = mCustomVideoView.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mCustomVideoView.setLayoutParams(params);
        }
    }

    protected void setVideoVerticalScreen() {
        ViewGroup.LayoutParams params = mCustomVideoView.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = mWidowPlayerHeight;
            mCustomVideoView.setLayoutParams(params);
        }
    }

    @Override
    public void screenChange(boolean isPortrait) {
        //如果是竖屏
        if (!isPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void screenTypeChange() {

    }

    @Override
    public void clickMall() {

    }

    @Override
    public void clickHy() {

    }

    @Override
    public void pause() {
//        if (mVideoPlusAdapter == null)
//            return;
//        mVideoPlusAdapter.notifyMediaStatusChanged(MediaStatus.PAUSE);
    }

    @Override
    public void start() {
//        if (mVideoPlusAdapter == null)
//            return;
//        mVideoPlusAdapter.notifyMediaStatusChanged(MediaStatus.SEEKING);
    }

    /***
     * 自动选择屏幕设置
     */
    private void initOrientationSwitcher() {
        mScreenOrientationSwitcher = new ScreenOrientationSwitcher(
                this, SensorManager.SENSOR_DELAY_NORMAL);

        mScreenOrientationSwitcher.setChangeListener(mOrientationChangeListener);
        if (mScreenOrientationSwitcher.canDetectOrientation()) {
            mScreenOrientationSwitcher.enable();
        }
    }

    private ScreenOrientationSwitcher.OnChangeListener mOrientationChangeListener =
            new ScreenOrientationSwitcher.OnChangeListener() {
                @Override
                public void onChanged(int requestOrientation) {
                    if (mCustomVideoView == null) {
                        return;
                    }
                    switchFullScreenInternal(requestOrientation);
                }
            };

    private void switchFullScreenInternal(int requestOrientation) {
        int currOrientation = this.getRequestedOrientation();
        if (currOrientation == requestOrientation) {
            return;
        }

        if (requestOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            switchFullScreen(true);
        } else if (requestOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            switchScreenOrientation(true, true);
        } else if (requestOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            switchFullScreen(false);
        }
    }

    public void switchFullScreen(boolean isFullscreen) {
        switchScreenOrientation(isFullscreen, false);
    }

    private void switchScreenOrientation(boolean fullScreen, boolean reverseOrientation) {
        if (fullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (reverseOrientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (reverseOrientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }
}
