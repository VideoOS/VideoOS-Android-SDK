package cn.com.venvy.common.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cn.com.venvy.common.image.IImageSize;
import cn.com.venvy.common.image.IImageSizeResult;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by videojj_pls on 2019/3/18.
 */

public class GlideImageSize implements IImageSize {
    @Override
    public void sizeImage(Context context, final String url, @Nullable final IImageSizeResult result) {
        if (context == null) {
            VenvyLog.i(" image context is null");
            return;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing()) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                if (activity.isDestroyed()) {
                    return;
                }
            }
        } else {
            context = context.getApplicationContext();
        }
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                result.loadSuccess(url, new VenvyBitmapInfo(resource, null));
            }
        });
    }
}
