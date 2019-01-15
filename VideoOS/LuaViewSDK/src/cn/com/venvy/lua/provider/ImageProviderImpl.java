/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package cn.com.venvy.lua.provider;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.image.VenvyImageLoaderFactory;
import cn.com.venvy.common.utils.VenvyLog;

public class ImageProviderImpl implements ImageProvider {

    private IImageLoader mImageLoader;

    public ImageProviderImpl() {
        mImageLoader = VenvyImageLoaderFactory.getImageLoader();
    }

    @Override
    public void pauseRequests(final ViewGroup view, Context context) {

    }

    @Override
    public void resumeRequests(final ViewGroup view, Context context) {

    }

    /**
     * load url
     */
    public void load(final Context context, final WeakReference<BaseImageView> referImageView, final String url, DrawableLoadCallback callback) {
        if (mImageLoader == null) {
            VenvyLog.e("imageloader plugin not deploy");
            return;
        }

        IImageLoaderResult result = null;
        if (callback != null) {
            result = new ImageResult(callback);
        }
        VenvyImageInfo.Builder builder = new VenvyImageInfo.Builder();
        if (referImageView != null) {
            BaseImageView baseImageView = referImageView.get();
            if (baseImageView != null) {
                builder.setPlaceHolderImage(baseImageView.getPlaceHolderDrawable());
            }
        }

        builder.setUrl(url);
        mImageLoader.loadImage(referImageView, builder.build(), result);
    }

    @Override
    public void preload(final Context context, String url, final DrawableLoadCallback callback) {
        if (mImageLoader == null) {
            VenvyLog.e("imageloader plugin not deploy");
            return;
        }
        IImageLoaderResult result = null;
        if (callback != null) {
            result = new ImageResult(callback);
        }
        mImageLoader.preloadImage(context, new VenvyImageInfo.Builder().setUrl(url).build(), result);
    }

    private static class ImageResult implements IImageLoaderResult {

        WeakReference<DrawableLoadCallback> callback;

        private ImageResult(DrawableLoadCallback callback) {
            this.callback = new WeakReference<>(callback);
        }


        @Override
        public void loadSuccess(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable VenvyBitmapInfo bitmap) {
            if (callback == null) {
                return;
            }
            DrawableLoadCallback drawableLoadCallback = callback.get();
            if (drawableLoadCallback != null) {
                Drawable drawable = null;
                if (bitmap != null && bitmap.getDrawable() != null) {
                    drawable = bitmap.getDrawable();
                } else if (bitmap != null && bitmap.getBitmap() != null) {
                    drawable = new BitmapDrawable(bitmap.getBitmap());
                }
                drawableLoadCallback.onLoadResult(drawable);
            }
        }

        @Override
        public void loadFailure(@Nullable WeakReference<? extends IImageView> imageView, String url, @Nullable Exception e) {
            if (callback == null) {
                return;
            }
            DrawableLoadCallback drawableLoadCallback = callback.get();
            if (drawableLoadCallback != null) {
                drawableLoadCallback.onLoadResult(null);
            }
        }
    }
}
