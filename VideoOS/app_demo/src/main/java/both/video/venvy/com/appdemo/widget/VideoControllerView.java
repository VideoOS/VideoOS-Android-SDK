
package both.video.venvy.com.appdemo.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import both.video.venvy.com.appdemo.R;
import cn.com.venvy.common.utils.VenvyUIUtil;

public class VideoControllerView extends FrameLayout implements View.OnClickListener {

    public static final int VIDEO_OS = 1;
    public static final int VIDEO_LIVE = 2;
    public static final int VIDEO_HY = 3;

    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    protected MediaPlayerControl mPlayer;
    protected Context mContext;
    private ViewGroup mAnchor;
    protected View mVerticalController;
    protected SeekBar mVerticalProgress;
    protected TextView mEndTime, mCurrentTime;
    protected ImageButton mPauseButton;
    private View mScreenChangeView;
    private View mButtonMall;
    private View mButtonHy;
    private View mImageChange;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;

    private Handler mHandler = new MessageHandler(this);

    private boolean mShowing;
    private boolean mDragging;
    private int mVideoType = VIDEO_OS;

    private IVideoControllerListener mControllerListener;


    public VideoControllerView(Context context) {
        super(context);
        mContext = context;
    }

    public void setVideoType(int videoType) {
        mVideoType = videoType;
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setVideoControllerListener(IVideoControllerListener listener) {
        mControllerListener = listener;
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;
        removeAllViews();

        makeControllerView();
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show
     *                the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mVerticalController != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            mVerticalController.setVisibility(VISIBLE);
            mShowing = true;
        }
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mVerticalController == null) {
            return;
        }
        try {
            mVerticalController.setVisibility(GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mVerticalProgress != null) {
            mVerticalProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(VideoControllerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(VideoControllerView.class.getName());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isShowing()) {
                hide();
            } else {
                show(sDefaultTimeout);
            }
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isMediaPlayerPlaying()) {
                mPlayer.mediaPlayerStart();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isMediaPlayerPlaying()) {
                mPlayer.mediaPlayerPause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mVideoType == VIDEO_OS) {
            resetControllerParams();
        }
    }

    @Override
    public void onClick(View v) {
        if (mControllerListener == null) {
            return;
        }
        int id = v.getId();
        if (id == mButtonMall.getId()) {
            mControllerListener.clickMall();
        } else if (id == mButtonHy.getId()) {
            mControllerListener.clickHy();
        } else if (id == mImageChange.getId()) {
            mControllerListener.screenTypeChange();
        } else if (id == mScreenChangeView.getId()) {
            mControllerListener.screenChange(VenvyUIUtil.isScreenOriatationPortrait(mContext));
        }
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected void makeControllerView() {
        mVerticalController = LayoutInflater.from(mContext)
                .inflate(R.layout.media_controller, mAnchor, false);
        initControllerView(mVerticalController);
        mAnchor.addView(mVerticalController);
        mVerticalController.bringToFront();
//        if (mVideoType == VIDEO_OS) {
//
//        }
        resetControllerParams();
    }

    protected void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.layout_media_pause);
        mPauseButton.setOnClickListener(mPauseListener);

        mCurrentTime = (TextView) v.findViewById(R.id.tv_media_time_current);
        mEndTime = (TextView) v.findViewById(R.id.tv_media_time);

        mVerticalProgress = (SeekBar) v.findViewById(R.id.media_controller_progress);
        mVerticalProgress.setOnSeekBarChangeListener(mSeekListener);
        mVerticalProgress.setMax(1000);

        mScreenChangeView = v.findViewById(R.id.ib_media_change_screen);
        mScreenChangeView.setOnClickListener(this);

        mButtonMall = v.findViewById(R.id.bt_media_mall);
        mButtonHy = v.findViewById(R.id.bt_media_huyu);
        mImageChange = v.findViewById(R.id.iv_media_change_screen);
        mButtonMall.setOnClickListener(this);
        mButtonHy.setOnClickListener(this);
        mImageChange.setOnClickListener(this);

        Group osGroup = (Group) v.findViewById(R.id.group_os_controller);

        switch (mVideoType) {
            case VIDEO_LIVE:
                osGroup.setVisibility(GONE);
                mButtonHy.setVisibility(GONE);
                break;
            case VIDEO_OS:
                mButtonMall.setVisibility(GONE);
                mImageChange.setVisibility(GONE);
                mButtonHy.setVisibility(GONE);
                break;
            case VIDEO_HY:
                osGroup.setVisibility(GONE);
                mImageChange.setVisibility(GONE);
                mScreenChangeView.setVisibility(GONE);
                break;
        }

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    private void resetControllerParams() {
        ConstraintLayout.LayoutParams progressParams = (ConstraintLayout.LayoutParams)
                mVerticalProgress.getLayoutParams();
        ConstraintLayout.LayoutParams pauseParams = (ConstraintLayout.LayoutParams) mPauseButton
                .getLayoutParams();
        ConstraintLayout.LayoutParams timeParams = (ConstraintLayout.LayoutParams) mEndTime
                .getLayoutParams();
        ConstraintLayout.LayoutParams changeIconParams = (ConstraintLayout.LayoutParams)
                mScreenChangeView.getLayoutParams();

        LayoutParams params = (LayoutParams) mVerticalController.getLayoutParams();
        if (!VenvyUIUtil.isScreenOriatationPortrait(mContext)) {
            params.height = (int) getResources().getDimension(R.dimen.media_controller_height);
            params.gravity = Gravity.BOTTOM;

            pauseParams.topToTop = ConstraintLayout.LayoutParams.UNSET;
            pauseParams.bottomMargin = (int) getResources().getDimension(R.dimen
                    .media_controller_pause_margin_bottom);

            changeIconParams.rightMargin = (int) getResources().getDimension(R.dimen
                    .media_play_margin_land_left);
            changeIconParams.bottomMargin = (int) getResources()
                    .getDimension(R.dimen.media_controller_pause_margin_bottom);
            changeIconParams.topToTop = ConstraintLayout.LayoutParams.UNSET;

            timeParams.rightToLeft = ConstraintLayout.LayoutParams.UNSET;
            timeParams.leftToRight = R.id.tv_media_time_current;

            progressParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
            progressParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            progressParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            progressParams.leftToRight = ConstraintLayout.LayoutParams.UNSET;
            progressParams.rightToLeft = ConstraintLayout.LayoutParams.UNSET;

        } else {
            params.height = (int) getResources().getDimension(R.dimen.media_controller_ver_height);
            params.gravity = Gravity.TOP;
            params.topMargin = VenvyUIUtil.dip2px(getContext(), 195) - params.height;

            pauseParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            pauseParams.bottomMargin = 0;

            changeIconParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            changeIconParams.bottomMargin = 0;
            changeIconParams.rightMargin = (int) getResources().getDimension(R.dimen
                    .media_play_margin_left);

            timeParams.leftToRight = ConstraintLayout.LayoutParams.UNSET;
            timeParams.rightToLeft = mScreenChangeView.getId();

            progressParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            progressParams.leftToRight = mCurrentTime.getId();
            progressParams.rightToLeft = mEndTime.getId();
            progressParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
            progressParams.rightToRight = ConstraintLayout.LayoutParams.UNSET;
        }

        mPauseButton.setLayoutParams(pauseParams);
        mScreenChangeView.setLayoutParams(changeIconParams);
        mEndTime.setLayoutParams(timeParams);
        mVerticalProgress.setLayoutParams(progressParams);
        mVerticalController.setLayoutParams(params);
    }


    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }

        try {
            if (mPauseButton != null && !mPlayer.mediaPlayerCanPause()) {
                mPauseButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }

        int position = mPlayer.getMediaPlayerCurrentPosition();
        int duration = mPlayer.getMediaPlayerDuration();
        if (mVerticalProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mVerticalProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getMediaPlayerBufferPercentage();
            mVerticalProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null) {
            String text = stringForTime(duration);
            if (!VenvyUIUtil.isScreenOriatationPortrait(mContext)) {
                text = "/" + text;
            }
            mEndTime.setText(text);
        }
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };


    private void updatePausePlay() {
        if (mVerticalController == null || mPauseButton == null || mPlayer == null) {
            return;
        }
        if (mPlayer.isMediaPlayerPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_media_play);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_media_pause);
        }
    }

    private void doPauseResume() {
        if (mPlayer == null) {
            return;
        }
        if (mPlayer.isMediaPlayerPlaying()) {
            mPlayer.mediaPlayerPause();
            if(mControllerListener!=null){
                mControllerListener.pause();
            }
        } else {
            mPlayer.mediaPlayerStart();
            if(mControllerListener!=null){
                mControllerListener.start();
            }
        }
        updatePausePlay();
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            mDragging = true;
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {

            if (mPlayer == null) {
                return;
            }

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getMediaPlayerDuration();
            long newposition = (duration * progress) / 1000L;
            mPlayer.mediaPlayerSeekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime((int) newposition));
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };


    public interface IVideoControllerListener {
        void screenChange(boolean isPortrait);

        void screenTypeChange();

        void clickMall();

        void clickHy();

        void pause();

        void start();
    }


    public interface MediaPlayerControl {
        void mediaPlayerStart();

        void mediaPlayerPause();

        int getMediaPlayerDuration();

        int getMediaPlayerCurrentPosition();

        void mediaPlayerSeekTo(int pos);

        boolean isMediaPlayerPlaying();

        int getMediaPlayerBufferPercentage();

        boolean mediaPlayerCanPause();
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            mView = new WeakReference(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing && view.mPlayer.isMediaPlayerPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }
}