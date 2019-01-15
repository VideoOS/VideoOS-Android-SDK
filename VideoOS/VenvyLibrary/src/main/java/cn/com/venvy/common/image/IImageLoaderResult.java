package cn.com.venvy.common.image;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yanjiangbo on 2017/6/6.
 */

public interface IImageLoaderResult {

    void loadSuccess(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable VenvyBitmapInfo bitmap);

    void loadFailure(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable Exception e);
}
