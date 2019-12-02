package cn.com.venvy;

import android.support.annotation.Nullable;
import android.view.ViewGroup;

import org.json.JSONArray;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.download.DownloadImageTask;
import cn.com.venvy.common.download.DownloadImageTaskRunner;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.interf.IAppletListener;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetPrepareShowListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.OnTagKeyListener;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.media.StorageUtils;
import cn.com.venvy.common.media.file.Md5FileNameGenerator;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.track.TrackHelper;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;

/**
 * Created by yanjiangbo on 2017/5/2.
 */

public class Platform implements Serializable {

    private static final long serialVersionUID = 259734984506L;

    private PlatformInfo mPlatformInfo;
    private boolean nvgShow = true; // 视联网小程序是否显示导航栏
    //手动上报逻辑
    private IPlatformLoginInterface mPlatformLoginInterface;
    private IMediaControlListener mMediaControlListener;
    private IWidgetShowListener mWidgetShowListener;
    private IWidgetCloseListener mWidgetCloseListener;
    private IWidgetClickListener mWidgetClickListener;
    private WedgeListener mWedgeListener;
    private IWidgetPrepareShowListener mPrepareShowListener;
    private OnTagKeyListener mTagKeyListener;
    private ViewGroup mContentViewGroup;
    private IAppletListener mAppletListener;

    private static final String PRE_LOAD_IMAGE = "pre_load_images";
    private static final String PRE_LOAD_MEDIA = "pre_load_medias";

    // track 类型
    public static final int STATISTICS_DOWNLOAD_STAGE_REAPP = 0;   // app启动的时候
    public static final int STATISTICS_DOWNLOAD_STAGE_REVIDEO = 1;  // 开始播放的时候
    public static final int STATISTICS_DOWNLOAD_STAGE_REALPLAY = 2; // 实时

    private DownloadImageTaskRunner mDownloadImageTaskRunner;
    private DownloadTaskRunner mDownloadTaskRunner;
    private PreloadLuaUpdate mPreloadLuaUpdate;

    public Platform(PlatformInfo platformInfo) {
        if (platformInfo != null) {
            mPlatformInfo = platformInfo;
        }
        TrackHelper.init(this);
    }

    public void setContentViewGroup(ViewGroup contentViewGroup) {
        this.mContentViewGroup = contentViewGroup;
    }

    public ViewGroup getContentViewGroup() {
        return mContentViewGroup;
    }

    public void setWedgeListener(WedgeListener mWedgeListener) {
        this.mWedgeListener = mWedgeListener;
    }

    public void setMediaControlListener(IMediaControlListener mMediaControlListener) {
        this.mMediaControlListener = mMediaControlListener;
    }

    public void setWidgetPrepareShowListener(IWidgetPrepareShowListener mPrepareShowListener) {
        this.mPrepareShowListener = mPrepareShowListener;
    }

    public void setWidgetShowListener(IWidgetShowListener mShowListener) {
        this.mWidgetShowListener = mShowListener;
    }

    public void setPlatformLoginInterface(IPlatformLoginInterface mPlatformLoginInterface) {
        this.mPlatformLoginInterface = mPlatformLoginInterface;
    }

    public void setWidgetClickListener(IWidgetClickListener mWidgetClickListener) {
        this.mWidgetClickListener = mWidgetClickListener;
    }

    public void setWidgetCloseListener(IWidgetCloseListener mCloseListener) {
        this.mWidgetCloseListener = mCloseListener;
    }

    public void setTagKeyListener(OnTagKeyListener mTagKeyListener) {
        this.mTagKeyListener = mTagKeyListener;
    }


    public void setAppletListener(IAppletListener AppletListener) {
        this.mAppletListener = mAppletListener;
    }

    public IAppletListener getAppletListener() {
        return mAppletListener;
    }

    public OnTagKeyListener getTagKeyListener() {
        return mTagKeyListener;
    }


    public IWidgetPrepareShowListener getPrepareShowListener() {
        return mPrepareShowListener;
    }

    public WedgeListener getWedgeListener() {
        return mWedgeListener;
    }

    public IMediaControlListener getMediaControlListener() {
        return mMediaControlListener;
    }

    public IWidgetClickListener getWidgetClickListener() {
        return mWidgetClickListener;
    }

    public IWidgetCloseListener getWidgetCloseListener() {
        return mWidgetCloseListener;
    }

    public IWidgetShowListener getWidgetShowListener() {
        return mWidgetShowListener;
    }


    public IPlatformLoginInterface getPlatformLoginInterface() {
        return mPlatformLoginInterface;
    }

    public void updatePlatformInfo(PlatformInfo platformInfo) {
        this.mPlatformInfo = platformInfo;
    }

    public PlatformInfo getPlatformInfo() {
        return mPlatformInfo;
    }

    public void onDestroy() {
        TrackHelper.onDestroy();
        if (mDownloadImageTaskRunner != null) {
            mDownloadImageTaskRunner.destroy();
        }
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        if (mPreloadLuaUpdate != null) {
            mPreloadLuaUpdate.destroy();
        }
    }

    public void preloadImage(final String[] imageUrls, final TaskListener taskListener) {
        if (imageUrls == null || imageUrls.length <= 0) {
            return;
        }
        if (mDownloadImageTaskRunner == null) {
            mDownloadImageTaskRunner = new DownloadImageTaskRunner(App.getContext());
        }
        ArrayList<DownloadImageTask> arrayList = new ArrayList<>();
        for (String url : imageUrls) {
            DownloadImageTask task = new DownloadImageTask(App.getContext(), url);
            arrayList.add(task);
        }
        mDownloadImageTaskRunner.startTasks(arrayList, new TaskListener<DownloadImageTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                if(taskListener != null){
                    return taskListener.isFinishing();
                }
                return false;
            }

            @Override
            public void onTaskStart(DownloadImageTask downloadImageTask) {
                if(taskListener != null){
                    taskListener.onTaskStart(downloadImageTask);
                }
            }

            @Override
            public void onTaskProgress(DownloadImageTask downloadImageTask, int progress) {
                if(taskListener != null){
                    taskListener.onTaskProgress(downloadImageTask, progress);
                }
            }

            @Override
            public void onTaskFailed(DownloadImageTask downloadImageTask, @Nullable Throwable throwable) {
                if(taskListener != null){
                    taskListener.onTaskFailed(downloadImageTask, throwable);
                }
            }

            @Override
            public void onTaskSuccess(DownloadImageTask downloadImageTask, Boolean aBoolean) {
                if(taskListener != null){
                    taskListener.onTaskSuccess(downloadImageTask, aBoolean);
                }
            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadImageTask> successfulTasks, @Nullable List<DownloadImageTask> failedTasks) {
                if(taskListener != null){
                    taskListener.onTasksComplete(successfulTasks, failedTasks);
                }
            }
        });
    }

    public void preloadMedia(final String[] mediaUrls, final TaskListener taskListener) {
        if (mediaUrls == null || mediaUrls.length <= 0) {
            return;
        }

        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(this);
        }
        ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String url : mediaUrls) {
            DownloadTask task = new DownloadTask(App.getContext(), url, StorageUtils.getIndividualCacheDirectory(App.getContext()).getAbsolutePath() + File.separator + new Md5FileNameGenerator().generate(url));
            arrayList.add(task);
        }
        mDownloadTaskRunner.startTasks(arrayList, new TaskListener<DownloadTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                if(taskListener != null){
                    return taskListener.isFinishing();
                }
                return false;
            }

            @Override
            public void onTaskStart(DownloadTask downloadTask) {
                if(taskListener != null){
                    taskListener.onTaskStart(downloadTask);
                }
            }

            @Override
            public void onTaskProgress(DownloadTask downloadTask, int progress) {
                if(taskListener != null){
                    taskListener.onTaskProgress(downloadTask, progress);
                }
            }

            @Override
            public void onTaskFailed(DownloadTask downloadTask, @Nullable Throwable throwable) {
                if(taskListener != null){
                    taskListener.onTaskFailed(downloadTask, throwable);
                }else {
                    downloadTask.failed();
                }
            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {
                if(taskListener != null){
                    taskListener.onTaskSuccess(downloadTask, aBoolean);
                }
            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                VenvyStatisticsManager.getInstance().submitFileStatisticsInfo(successfulTasks,Platform.STATISTICS_DOWNLOAD_STAGE_REVIDEO);
                if(taskListener != null){
                    taskListener.onTasksComplete(successfulTasks, failedTasks);
                }
            }
        });
    }

    public void preloadMiniAppLua(final Platform platform, final List<LuaFileInfo> listOfLuaInfo, final PreloadLuaUpdate.CacheLuaUpdateCallback cacheLuaUpdateCallback) {
        if (listOfLuaInfo == null || listOfLuaInfo.size() <= 0) {
            return;
        }
        mPreloadLuaUpdate = new PreloadLuaUpdate(Platform.STATISTICS_DOWNLOAD_STAGE_REVIDEO, platform, new PreloadLuaUpdate.CacheLuaUpdateCallback() {
            @Override
            public void updateComplete(boolean isUpdateByNetWork) {
                if (isUpdateByNetWork) {
                    try {
                        //TODO 反射 强制更新Lua目录
                        Class<?> mClass = Class.forName("cn.com.videopls.pub.view.VideoOSLuaView");
                        Method method = mClass.getMethod("destroyLuaScript");
                        method.setAccessible(true);
                        method.invoke(mClass, new Object[]{});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cacheLuaUpdateCallback != null) {
                    cacheLuaUpdateCallback.updateComplete(isUpdateByNetWork);
                }
            }

            @Override
            public void updateError(Throwable t) {
                if (cacheLuaUpdateCallback != null) {
                    cacheLuaUpdateCallback.updateError(t);
                }
            }
        });
        mPreloadLuaUpdate.startDownloadLuaFile(listOfLuaInfo);
    }

    public DownloadTaskRunner getDownloadTaskRunner() {
        return mDownloadTaskRunner;
    }

    public DownloadImageTaskRunner getDownloadImageTaskRunner() {
        return mDownloadImageTaskRunner;
    }

    public PreloadLuaUpdate getDownloadLuaTaskRunner() {
        return mPreloadLuaUpdate;
    }

    public void stopBackgroundThread() {
        VenvyAsyncTaskUtil.cancel(PRE_LOAD_MEDIA);
        VenvyAsyncTaskUtil.cancel(PRE_LOAD_IMAGE);
    }

    public boolean isNvgShow() {
        return nvgShow;
    }

    public void setNvgShow(boolean nvgShow) {
        this.nvgShow = nvgShow;
    }
}
