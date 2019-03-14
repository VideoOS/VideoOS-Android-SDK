package cn.com.venvy.common.download;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.com.venvy.IgnoreHttps;
import cn.com.venvy.common.image.WebpConvert;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public class DownloadImageTask implements QueueTaskRunner.ITask {

    @Override
    public int getTaskId() {
        return mUrl.hashCode();
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    private String mUrl;
    private Context mContext;

    public DownloadImageTask(@NonNull Context context, @NonNull String url) {
        mUrl = WebpConvert.convertWebp(IgnoreHttps.ignore(url));
        mContext = context;
    }


    public String getImageUrl() {
        return mUrl;
    }
}
