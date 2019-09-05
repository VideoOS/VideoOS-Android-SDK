package cn.com.venvy.common.media.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.io.File;

import cn.com.venvy.App;
import cn.com.venvy.Platform;
import cn.com.venvy.common.media.CacheListener;
import cn.com.venvy.common.media.HttpProxyCacheServer;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.receiver.VolumeChangerObserver;
import cn.com.venvy.common.statistics.StatisticsInfoBean;
import cn.com.venvy.common.statistics.ThreadManager;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.utils.VenvyLog;


public class CustomVideoView extends VenvyTextureView implements VideoControllerView.MediaPlayerControl, TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, CacheListener, VolumeChangerObserver.VolumeChangeListener {


    private static final String TAG = CustomVideoView.class.getName();

    public static final String MEDIA_STATUS = "media_status";
    public static final String VOLUME_STATUS = "media_volume";
    /**
     * 播放错误
     **/
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     **/
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     **/
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     **/
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     **/
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     **/
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
     **/
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     **/
    public static final int STATE_COMPLETED = 7;

    private int mCurrentState = STATE_IDLE;
    private int mLastState = STATE_IDLE;


    private int mCurrentPosition = 0;
    SurfaceTexture mSurfaceTexture = null;
    Surface mSurface;

    private Context mContext;
    private VideoControllerView mMediaController;
    //    private AudioManager mAudioManager = null;
    private VolumeChangerObserver mVolumeChangerObserver;
    private int mCurrentVoice = -1;
    private String mCurrentUrl;
    private MediaPlayer mMediaPlayer;
    private HttpProxyCacheServer mProxy;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mVolumeChangerObserver != null) {
            mVolumeChangerObserver.registerReceiver();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mVolumeChangerObserver != null) {
            mVolumeChangerObserver.unregisterReceiver();
        }
        if (mProxy != null) {
            if (TextUtils.isEmpty(getSource())) {
                mProxy.unregisterCacheListener(this);
            } else {
                mProxy.unregisterCacheListener(this, getSource());
            }
        }
    }

    private MediaPlayer.OnErrorListener mOnErrorListener
            = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mCurrentState = STATE_ERROR;
                stateChanged(mCurrentState);
                VenvyLog.d(TAG, "onError ——> STATE_ERROR ———— what：" + what + ", extra: " + extra);
            }
            return true;
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                // 播放器开始渲染
                mCurrentState = STATE_PLAYING;
                stateChanged(mCurrentState);
                VenvyLog.d(TAG, "onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                // MediaPlayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    stateChanged(mCurrentState);
                    VenvyLog.d(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    VenvyLog.d(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    stateChanged(mCurrentState);
                    VenvyLog.d(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    stateChanged(mCurrentState);
                    VenvyLog.d(TAG, "onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                VenvyLog.d(TAG, "视频不能seekTo，为直播视频");
            } else {
                VenvyLog.d(TAG, "onInfo ——> what：" + what);
            }
            return true;
        }
    };
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            stateChanged(mCurrentState);
        }
    };
    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            adjustVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
        }
    };

    public CustomVideoView(Context context) {
        this(context, null, 0);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mVolumeChangerObserver = new VolumeChangerObserver(context);
        mVolumeChangerObserver.setVolumeChangeListener(this);
        initView();
    }

    private void initView() {
        setSurfaceTextureListener(this);
    }

    public void startPlay(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (TextUtils.equals(mCurrentUrl, url) && mMediaPlayer != null) {
            restart();
            return;
        }
        if (!TextUtils.isEmpty(mCurrentUrl) && !TextUtils.equals(mCurrentUrl, url)) {
            stopPlay();
            initPlayer(url);
        }
        mCurrentUrl = url;
    }

    public void pausePlay() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            stateChanged(mCurrentState);
            VenvyLog.d(TAG, "STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            stateChanged(mCurrentState);
            VenvyLog.d(TAG, "STATE_BUFFERING_PAUSED");
        }
    }

    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    public String getSource() {
        return mCurrentUrl;
    }

    public void stateChanged(int state) {
        if (mLastState == state) {
            return;
        }
        mLastState = state;
        Bundle bundle = new Bundle();
        bundle.putInt(MEDIA_STATUS, state);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED, bundle);
    }

    public void restart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            stateChanged(mCurrentState);
            VenvyLog.d(TAG, "STATE_PLAYING");
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            stateChanged(mCurrentState);
            VenvyLog.d(TAG, "STATE_BUFFERING_PLAYING");
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
            initPlayer(mCurrentUrl);
        } else {
            VenvyLog.d(TAG, "NiceVideoPlayer在mCurrentState == " + mCurrentState + "时不能调用restart()方法.");
        }
    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentState = STATE_IDLE;
        Runtime.getRuntime().gc();
    }

    public void setVoice(float voice) {
        int maxVoice = mVolumeChangerObserver.getMaxMusicVolume();
        mCurrentVoice = (int) (maxVoice * voice);
        if (mMediaPlayer != null && mCurrentState == STATE_PLAYING && isMediaPlayerPlaying()) {
            mMediaPlayer.setVolume(voice, voice);
        }
    }

    public float getVoice() {
        int maxVoice = mVolumeChangerObserver.getMaxMusicVolume();
        return (float) mCurrentVoice / (float) maxVoice;
    }

    private void adjustVoice() {
        if (mCurrentVoice == -1) {
            mCurrentVoice = mVolumeChangerObserver.getCurrentMusicVolume();
        }
        mVolumeChangerObserver.setVolume(mCurrentVoice);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVoice,
//                AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (surface != null) {
            if (mSurfaceTexture == null) {
                mSurfaceTexture = surface;
                initPlayer(mCurrentUrl);
            } else {
                if (mMediaPlayer == null) {
                    initPlayer(mCurrentUrl);
                } else {
                    if (surface != mSurfaceTexture) {
                        mSurfaceTexture = surface;
                        mMediaPlayer.setSurface(new Surface(surface));
                    }
                    restart();
                }
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    private void initPlayer(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mCurrentState == STATE_IDLE) {
            if (mProxy == null) {
                mProxy = HttpProxyCacheServerFactory.getInstance().getProxy(App.getContext());
            }
            mProxy.registerCacheListener(this, url);
            String proxyUrl = mProxy.getProxyUrl(url);
            VenvyLog.d(CustomVideoView.class.getName(), "Use proxy url " + proxyUrl + " instead of original url " + url);
            stopPlay();
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnPreparedListener(this);
                if (mSurface == null) {
                    mSurface = new Surface(mSurfaceTexture);
                }
                mMediaPlayer.setSurface(mSurface);
                mMediaPlayer.setDataSource(mContext, Uri.parse(proxyUrl));
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setScreenOnWhilePlaying(true);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(mOnErrorListener);
                mMediaPlayer.setOnInfoListener(mOnInfoListener);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
                mCurrentState = STATE_PREPARING;
                stateChanged(mCurrentState);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            VenvyLog.d(TAG, "只有初始化状态才能初始化播放器");
        }
    }

    @Override
    public void mediaPlayerStart() {
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            stateChanged(mCurrentState);
            VenvyLog.d(TAG, "STATE_PLAYING");
        }
    }

    @Override
    public void mediaPlayerPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public int getMediaPlayerDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public int getMediaPlayerCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    @Override
    public void mediaPlayerSeekTo(int pos) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(pos);
        }

    }

    @Override
    public boolean isMediaPlayerPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public int getMediaPlayerBufferPercentage() {
        return 0;
    }

    @Override
    public boolean mediaPlayerCanPause() {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mCurrentState = STATE_PREPARED;
        stateChanged(mCurrentState);
        adjustVoice();
        this.setMediaController(mMediaController);
        if (mp != null) {
            if (mCurrentPosition > 0) {
                mp.seekTo(mCurrentPosition);
            }
            mp.start();
        }
    }

    public void setMediaController(VideoControllerView controller) {
        if (mMediaController != null) {
            ViewParent parent = mMediaController.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mMediaController);
            }
        }
        mMediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            ViewGroup anchorView = (ViewGroup) this.getParent();
            mMediaController.setAnchorView(anchorView);
            mMediaController.show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mMediaController != null) {
                if (mMediaController.isShowing()) {
                    mMediaController.hide();
                } else {
                    mMediaController.show();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private volatile boolean isStatistic = true;
    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if(isStatistic && cacheFile != null && cacheFile.exists() && cacheFile.getAbsolutePath().endsWith(".download")){
            isStatistic = false;
            statisticVideoFileSize();
        }
    }

    private void statisticVideoFileSize() {
        if(TextUtils.isEmpty(mCurrentUrl)){
            return;
        }
        ThreadManager.getInstance().createShortPool().execute(new Runnable() {
            @Override
            public void run() {
                StatisticsInfoBean.FileInfoBean fileInfoBean = new StatisticsInfoBean.FileInfoBean();
                fileInfoBean.fileName = mCurrentUrl.substring(mCurrentUrl.lastIndexOf("/") + 1);
                fileInfoBean.filePath = mCurrentUrl;
                fileInfoBean.fileSize = 0;
                VenvyStatisticsManager.getInstance().submitFileStatisticsInfo(fileInfoBean, Platform.STATISTICS_DOWNLOAD_STAGE_REALPLAY);
            }
        });
    }

    @Override
    public void onVolumeChanged(int maxVolume, int volume) {
        float volumeScale = (float) volume / (float) maxVolume;
        setVoice(volumeScale);
        Bundle bundle = new Bundle();
        bundle.putFloat(VOLUME_STATUS, volumeScale);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED, bundle);
    }
}
