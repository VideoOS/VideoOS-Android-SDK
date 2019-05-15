package cn.com.venvy.common.image;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by videojj_pls on 2019/3/18.
 */

public interface IImageSize {
    void sizeImage(Context context, String url, @Nullable IImageSizeResult result);
}
