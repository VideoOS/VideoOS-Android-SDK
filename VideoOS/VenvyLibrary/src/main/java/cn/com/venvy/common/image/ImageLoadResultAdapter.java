package cn.com.venvy.common.image;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.report.Report;

/**
 * Created by yanjiangbo on 2018/1/11.
 */

public class ImageLoadResultAdapter implements IImageLoaderResult {

    private IImageLoaderResult mImageLoaderResult;

    public ImageLoadResultAdapter(IImageLoaderResult imageLoaderResult) {
        this.mImageLoaderResult = imageLoaderResult;
    }

    @Override
    public void loadSuccess(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable VenvyBitmapInfo bitmap) {
        if (mImageLoaderResult != null) {
            mImageLoaderResult.loadSuccess(imageView, url, bitmap);
        }
    }

    @Override
    public void loadFailure(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable Exception e) {
        if (mImageLoaderResult != null) {
            mImageLoaderResult.loadFailure(imageView, url, e);
        }
//        Report.report(Report.ReportLevel.w, ImageLoadResultAdapter.class.getName(), buildReportString(e, url));
    }

    private static String buildReportString(Exception exception, String url) {

        StringBuilder builder = new StringBuilder();
        builder.append("[image load failed], url = ").append(url);
        builder.append("\\n");
        if (exception != null) {
            if (!TextUtils.isEmpty(exception.toString())) {
                builder.append("Cause by:").append(exception.toString());
                builder.append("\\n");
            }
            StackTraceElement[] element = exception.getStackTrace();
            if (element != null) {
                for (StackTraceElement i : element) {
                    builder.append(i.toString());
                    builder.append("\\n");
                }
            }
        }
        return builder.toString();
    }
}
