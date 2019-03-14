package cn.com.venvy.common.image;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by yanjiangbo on 2017/5/2.
 */

public interface IImageLoader {

    void loadImage(WeakReference<? extends IImageView> imageView, VenvyImageInfo venvyImageInfo, @Nullable IImageLoaderResult result);

    void preloadImage(Context context, VenvyImageInfo venvyImageInfo, @Nullable IImageLoaderResult result);
}
