package cn.com.venvy.common.download;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public class DownloadTask implements QueueTaskRunner.ITask {

    private String mUrl;
    private String mCachePath;
    private Context mContext;
    private DownloadDbHelper mHelper;
    private DownloadDbHelper.DownloadInfo mDownloadInfo;
    private boolean isForceDownload;

    public DownloadTask(@NonNull Context context, @NonNull String url, @NonNull String filePath) {
        this(context, url, filePath, false);
    }

    public DownloadTask(@NonNull Context context, @NonNull String url, @NonNull String filePath, boolean forceDownload) {
        mUrl = url;
        mCachePath = filePath;
        mContext = context;
        mHelper = new DownloadDbHelper(mContext);
        mDownloadInfo = new DownloadDbHelper.DownloadInfo();
        mDownloadInfo.status = DownloadDbHelper.DownloadStatus.NONE;
        mDownloadInfo.downloadSize = 0;
        mDownloadInfo.totalSize = 0;
        mDownloadInfo.url = url;
        mDownloadInfo.filePath = filePath;
        isForceDownload = forceDownload;
    }

    public void startWork() {
        mDownloadInfo.status = DownloadDbHelper.DownloadStatus.DOWNLOADING;
        mHelper.insertDownloadInfoForDB(mDownloadInfo);
    }

    public void progress(long currentPosition, long totalSize) {
        mDownloadInfo.downloadSize = currentPosition;
        mDownloadInfo.totalSize = totalSize;
        mHelper.updateDownloadInfoForDB(mDownloadInfo);
    }

    public void failed() {
        mDownloadInfo.status = DownloadDbHelper.DownloadStatus.DOWNLOAD_FAILED;
        mHelper.updateDownloadInfoForDB(mDownloadInfo);
    }

    public void success(long downloadSize, long totalSize) {
        mDownloadInfo.downloadSize = downloadSize;
        mDownloadInfo.totalSize = totalSize;
        mDownloadInfo.status = DownloadDbHelper.DownloadStatus.DOWNLOAD_SUCCESS;
        mHelper.updateDownloadInfoForDB(mDownloadInfo);
    }


    public String getDownloadUrl() {
        return mUrl;
    }

    public String getDownloadCacheUrl() {
        return mCachePath;
    }

    public DownloadDbHelper.DownloadInfo getmDownloadInfo() {
        return mDownloadInfo;
    }

    @Override
    public boolean isComplete() {
        DownloadDbHelper.DownloadInfo info = mHelper.queryDownloadInfo(mUrl);
        if (isForceDownload) {
            if (info != null) {
                mHelper.delete(info);
            }
            File file = new File(mDownloadInfo.filePath);
            if (file.exists()) {
                file.delete();
            }
            return false;
        }
        if (info != null) {
            //如果文件正在下载或者下载完成会直接返回而不进行下载
            if (info.status == DownloadDbHelper.DownloadStatus.DOWNLOAD_SUCCESS || info.status == DownloadDbHelper.DownloadStatus.DOWNLOADING) {
                File file = new File(mDownloadInfo.filePath);
                if (!file.exists()) {
                    mHelper.delete(info);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getTaskId() {
        return mUrl.hashCode();
    }
}
