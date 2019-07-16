package cn.com.venvy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/5/15.
 */

public class VideoPositionHelper {


    public static final String KEY_TIME = "time";
    public static final String KEY_POSITIVE = "positive";
    public static final String KEY_PLAY_STATUS = "play_status";
    public static final String KEY_PLAY_VOICE = "play_voice";
    public static final int DEFAULT_SLEEP_TIME = 10;

    private static final int STATUS_IDLE = 0;  // 初始状态
    private static final int STATUS_START = 1;  //已经开始轮询状态
    private static final int STATUS_END = 2;   // 已经结束轮询状态
    private static final int STATUS_PAUSE = 3; //暂停轮询状态

    private WeakReference<IMediaControlListener> mediaControlListenerWeakReference;

    private Handler mHandler;

    private int mLooperStatus = STATUS_IDLE;

    private static VideoPositionHelper sVideoPositionHelper;

    public static synchronized VideoPositionHelper getInstance() {
        if (sVideoPositionHelper == null) {
            sVideoPositionHelper = new VideoPositionHelper();
        }
        return sVideoPositionHelper;
    }

    private VideoPositionHelper() {
    }

    public void setMediaPlayController(IMediaControlListener currentListener) {
        this.mediaControlListenerWeakReference = new WeakReference<>(currentListener);
        mHandler = new MyHandler(this, mediaControlListenerWeakReference);
    }

    public void start() {
        if (mLooperStatus == STATUS_START) {
            VenvyLog.w("VideoOS", "looper has started");
            return;
        }
        mLooperStatus = STATUS_START;
        VenvyLog.d("VideoOS", "time looper begin run");
        this.start(0);
    }

    private void start(long delayTime) {
        if (mLooperStatus == STATUS_END || mLooperStatus == STATUS_PAUSE) {
            return;
        }
        if (mediaControlListenerWeakReference == null) {
            return;
        }
        IMediaControlListener controlListener = mediaControlListenerWeakReference.get();
        if (controlListener == null) {
            return;
        }
        mLooperStatus = STATUS_START;
        if (mHandler == null) {
            mHandler = new MyHandler(this, mediaControlListenerWeakReference);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage().sendToTarget();
            }
        }, delayTime <= 0 ? DEFAULT_SLEEP_TIME : delayTime);
    }

    public void resume() {
        if (mLooperStatus == STATUS_PAUSE) {
            start();
        }
    }

    public void pause() {
        if (mLooperStatus == STATUS_END) {
            return;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mLooperStatus = STATUS_PAUSE;

    }

    public void cancel() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mLooperStatus = STATUS_END;
    }

    private static class MyHandler extends Handler {
        private WeakReference<VideoPositionHelper> reference;
        private WeakReference<IMediaControlListener> mediaControlListenerWeakReference;
        private MediaStatus mLastMediaStatus;
        private long mLastPosition;

        MyHandler(VideoPositionHelper helper, WeakReference<IMediaControlListener> controlListenerWeakReference) {
            reference = new WeakReference<>(helper);
            mediaControlListenerWeakReference = controlListenerWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (reference == null || mediaControlListenerWeakReference == null) {
                return;
            }
            VideoPositionHelper helper = reference.get();
            IMediaControlListener controlListener = mediaControlListenerWeakReference.get();
            if (helper == null || controlListener == null) {
                return;
            }
            long position = controlListener.getCurrentPosition();
            boolean isPositive = controlListener.isPositive();
            MediaStatus mediaStatus = controlListener.getCurrentMediaStatus();
            long delayTime = DEFAULT_SLEEP_TIME;
            if ((mLastMediaStatus != mediaStatus || mLastPosition != position) && isPositive) {
                Bundle bundle = new Bundle();
                bundle.putLong(KEY_TIME, position);
                bundle.putBoolean(KEY_POSITIVE, isPositive);
                bundle.putSerializable(KEY_PLAY_STATUS, mediaStatus);
                bundle.putFloat(KEY_PLAY_VOICE, controlListener.getVoice());
                delayTime = DEFAULT_SLEEP_TIME;
                ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_MEDIA_POSITION_CHANGED, bundle);
                mLastMediaStatus = mediaStatus;
                mLastPosition = position;
            }
            helper.start(delayTime);
        }
    }

}
