package cn.com.venvy.common.image;

import android.support.annotation.Nullable;

/**
 * Created by videojj_pls on 2019/3/18.
 */

public interface IImageSizeResult {
    void loadSuccess(String url, @Nullable VenvyBitmapInfo bitmap);

    void loadFailure(String url, @Nullable Exception e);
}
