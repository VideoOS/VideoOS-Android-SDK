package cn.com.venvy.common.download;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.exception.HttpException;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyIOUtils;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.videopls.venvy.library.BuildConfig;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public class DownloadTaskRunner extends QueueTaskRunner<DownloadTask, Boolean> {

    private BaseRequestConnect mBaseRequestConnect;

    public DownloadTaskRunner(BaseRequestConnect connect) {
        super();
        mBaseRequestConnect = connect;
    }

    public DownloadTaskRunner(Platform platform) {
        super();
        mBaseRequestConnect = RequestFactory.initConnect(platform);
    }

    @Deprecated
    public DownloadTaskRunner() {
        super();
        PlatformInfo platformInfo = new PlatformInfo.Builder().builder();
        mBaseRequestConnect = RequestFactory.initConnect(new Platform(platformInfo));
    }

    @Override
    protected Boolean execute(@NonNull DownloadTask task) throws Throwable {
        String url = task.getDownloadUrl();
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("download failed,because download url is null");
        }
        BaseRequestConnect connect = mBaseRequestConnect;
        if (connect == null) {
            throw new NullPointerException("download connect can't be null");
        }
        task.startWork();
        Request request = HttpRequest.get(url);
        IResponse response = connect.syncConnect(request);
        InputStream is = null;
        try {
            is = response.getByteStream();
            //获取文件总长度
            long totalLength = response.getContentLength();
            int code = response.getHttpCode();
            if (code != 200) {
                return null;
            }
            //已经下载的文件长度
            long tempLength = 0;

            String filePath = task.getDownloadCacheUrl();
            int lastIndex = filePath.lastIndexOf("/");
            String fileDirPath = filePath.substring(0, lastIndex + 1);
            String fileName = filePath.substring(lastIndex + 1);
            File dir = new File(fileDirPath);
            if (!dir.exists()) {
                dir.mkdirs();//创建目录
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                tempLength = file.length();
            }
            if (totalLength != 0 && tempLength == totalLength) {//下载成功
                VenvyLog.i(fileName + " already download,return immediately");
                task.success(tempLength, totalLength);
                return true;
            } else {
                if (tempLength > 0) {
                    file.delete();
                    file.createNewFile();
                }
            }
            setProgress(task, 0);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            byte buf[] = new byte[1024];
            long downloadFileSize = 0;
            int percent = 0;
            int downloadCount = 0;
            do {
                int numRead = is.read(buf);
                if (numRead > 0) {
                    downloadCount = 0;
                    randomAccessFile.write(buf, 0, numRead);
                    downloadFileSize += numRead;
                    int currentPercent = (int) (((double) downloadFileSize / (double) totalLength) * 100.0d);
                    if (currentPercent >= percent + 10) {
                        //每次只有进度是达到10%才回调前端
                        setProgress(task, currentPercent);
                        task.progress(downloadFileSize, totalLength);
                        percent = currentPercent;
                    }
                } else {
                    downloadCount++;
                    if (downloadCount > 20) {
                        task.failed();
                        throw new HttpException("download failed, and retry count is more than max count");
                    }
                }
            } while (downloadFileSize < totalLength);

            if (downloadFileSize == totalLength) {
                VenvyLog.i("---download complete----url ==" + url);
                task.success(downloadFileSize, totalLength);
                setProgress(task, 1);
                return true;
            } else {
                task.failed();
                throw new HttpException("download failed, because file totalSize is wrong");
            }
        } finally {
            VenvyIOUtils.close(is);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mBaseRequestConnect != null) {
            mBaseRequestConnect.abortAll();
        }
    }
}
