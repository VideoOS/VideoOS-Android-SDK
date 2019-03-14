package cn.com.venvy.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.lang.ref.WeakReference;

/**
 * Created by videojj_pls on 2018/12/7.
 */

public class VolumeChangerObserver {
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";

    public interface VolumeChangeListener {
        void onVolumeChanged(int volume);
    }

    private Context mContext;
    private boolean mRegistered = false;
    private VolumeBroadcastReceiver mVolumeBroadcastReceiver;
    private AudioManager mAudioManager;
    private VolumeChangeListener mVolumeChangeListener;

    public VolumeChangerObserver(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /***
     * 注册音量广播
     */
    public void registerReceiver() {
        if (mVolumeBroadcastReceiver == null) {
            mVolumeBroadcastReceiver = new VolumeBroadcastReceiver(this);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mVolumeBroadcastReceiver, filter);
        mRegistered = true;
    }

    /***
     * 解除广播
     */
    public void unregisterReceiver() {
        if (mRegistered) {
            try {
                mContext.unregisterReceiver(mVolumeBroadcastReceiver);
//                mVolumeChangeListener = null;
                mRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *     * 获取当前媒体音量
     *     * @return
     *    
     */
    public int getCurrentMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : -1;
    }

    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                    AudioManager.FLAG_PLAY_SOUND);
        }
    }

    /***
     * 获取当前媒体最大音量
     * @return
     */
    public int getMaxMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 15;
    }

    public void setVolumeChangeListener(VolumeChangeListener l) {
        this.mVolumeChangeListener = l;
    }

    public VolumeChangeListener getVolumeChangeListener() {
        return mVolumeChangeListener;
    }

    private class VolumeBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<VolumeChangerObserver> mObserverWeakReference;

        public VolumeBroadcastReceiver(VolumeChangerObserver volumeChangeObserver) {
            mObserverWeakReference = new WeakReference<>(volumeChangeObserver);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction()) && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)) {
                VolumeChangerObserver observer = mObserverWeakReference.get();
                if (observer == null) {
                    return;
                }
                VolumeChangeListener listener = observer.getVolumeChangeListener();
                if (listener == null) {
                    return;
                }
                int volume = observer.getCurrentMusicVolume();
                if (volume >= 0) {
                    listener.onVolumeChanged(volume);
                }
            }
        }
    }
}
