package cn.com.venvy.common.download;

import android.content.Context;
import android.text.TextUtils;

import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.image.VenvyImageLoaderFactory;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public class DownloadImageTaskRunner extends QueueTaskRunner<DownloadImageTask, Boolean> {
    private IImageLoader mImageLoader = null;
    private Context mContext;

    public DownloadImageTaskRunner(Context context) {
        mImageLoader = VenvyImageLoaderFactory.getImageLoader();
        mContext = context;
    }

    @Override
    protected Boolean execute(DownloadImageTask task) throws Throwable {
        String url = task.getImageUrl();
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("download image failed,because download url is null");
        }
        VenvyLog.i("start download image,url==" + task.getImageUrl());
        mImageLoader.preloadImage(mContext, new VenvyImageInfo.Builder().setUrl(url).build(), null);
        return true;
    }
}
