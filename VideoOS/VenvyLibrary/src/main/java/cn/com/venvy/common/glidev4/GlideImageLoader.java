package cn.com.venvy.common.glidev4;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.RequestBuilder;
//import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;

import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;


/**
 * Created by mac on 18/3/1.
 */

public class GlideImageLoader implements IImageLoader {

    @Override
    public void loadImage(@NonNull final WeakReference<? extends IImageView> imageViewReference, final @NonNull VenvyImageInfo
            venvyImageInfo, final IImageLoaderResult result) {
//        IImageView iImageView = imageViewReference.get();
//        if (iImageView == null) {
//            return;
//        }
//        Context context = iImageView.getContext();
//        if (context == null) {
//            VenvyLog.i(iImageView + " image context is null");
//            return;
//        }
//        if (iImageView.getContext() instanceof Activity) {
//            Activity activity = (Activity) iImageView.getContext();
//            if (activity.isFinishing()) {
//                return;
//            }
//            if (Build.VERSION.SDK_INT >= 17) {
//                if (activity.isDestroyed()) {
//                    return;
//                }
//            }
//        } else {
//            context = context.getApplicationContext();
//        }
//        if (!(iImageView.getImageView() instanceof ImageView)) {
//            return;
//        }
//        ImageView imageView = (ImageView) iImageView.getImageView();
//        final String url = venvyImageInfo.getUrl();
//        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
//                .load(url);
//
//        RequestOptions myOptions = new RequestOptions()
//                .placeholder(venvyImageInfo.getPlaceHolderImage())
//                .error(venvyImageInfo.getFailedImage())
//                .override(venvyImageInfo.getResizeWidth(), venvyImageInfo.getResizeHeight())
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE).transform(new Transformation<Bitmap>() {
//                    @Override
//                    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
//                        return null;
//                    }
//
//                    @Override
//                    public void updateDiskCacheKey(MessageDigest messageDigest) {
//
//                    }
//                }).transform(new ColorFilterTransformation(venvyImageInfo, context));
//
//        requestBuilder.apply(myOptions).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object url, Target<Drawable> target, boolean isFirstResource) {
//                if (result != null) {
//                    result.loadFailure(imageViewReference, (String) url, e);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable resource, Object url, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                if (result != null) {
//                    result.loadSuccess(imageViewReference, (String) url, new VenvyBitmapInfo(null, resource));
//                }
//                return false;//不要return true 否则不展示图片
//            }
//        }).into(imageView);
    }

    @Override
    public void preloadImage(Context context, final VenvyImageInfo venvyImageInfo, final IImageLoaderResult result) {
//        if (context == null) {
//            VenvyLog.i("image context is null");
//            return;
//        }
//        if (venvyImageInfo == null || TextUtils.isEmpty(venvyImageInfo.getUrl())) {
//            return;
//        }
//        if (context instanceof Activity) {
//            Activity activity = (Activity) context;
//            if (activity.isFinishing()) {
//                return;
//            }
//            if (Build.VERSION.SDK_INT >= 17) {
//                if (activity.isDestroyed()) {
//                    return;
//                }
//            }
//        } else {
//            context = context.getApplicationContext();
//        }
//        final Context targetContext = context;
//        if (VenvyUIUtil.isOnUIThread()) {
//            new Thread() {
//                @Override
//                public void run() {
//                    startDownloadImage(targetContext, venvyImageInfo, result);
//                }
//            }.start();
//        } else {
//            startDownloadImage(targetContext, venvyImageInfo, result);
//        }
    }


    private void startDownloadImage(Context context, VenvyImageInfo venvyImageInfo, final IImageLoaderResult result) {
//        String url = venvyImageInfo.getUrl();
//        if (TextUtils.isEmpty(url)) {
//            return;
//        }
//        RequestBuilder<File> requestBuilder = Glide.with(context).download(url).listener(new RequestListener<File>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object url, Target<File> target, boolean isFirstResource) {
//                if (result != null) {
//                    result.loadFailure(null, (String) url, e);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(File resource, Object url, Target<File> target, DataSource dataSource, boolean isFirstResource) {
//                if (result != null) {
//                    result.loadSuccess(null, (String) url, null);
//                }
//                return false;
//            }
//        });

    }


    //
//    private static class ColorFilterTransformation extends com.bumptech.glide.load.resource.bitmap.BitmapTransformation {
//        private static final String ID = "cn.com.venvy.glide.v4.GlideImageloader.ColorFilterTransformation";
//        WeakReference<VenvyImageInfo> mWeakReferenceImageInfo;
//
//        ColorFilterTransformation(VenvyImageInfo venvyImageInfo, Context context) {
//            super(context);
//            mWeakReferenceImageInfo = new WeakReference<>(venvyImageInfo);
//        }
//
//        @Override
//        protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
//            return paintColor(bitmapPool, bitmap);
//        }
//
//        private Bitmap paintColor(BitmapPool bitmapPool, Bitmap source) {
//            VenvyLog.i("--glide paintColor-");
//            VenvyImageInfo info = mWeakReferenceImageInfo.get();
//            if (info == null || !info.isNeedPaintColor()) {
//                return source;
//            }
//            int width = source.getWidth();
//            int height = source.getHeight();
//
//            Bitmap.Config config =
//                    source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = bitmapPool.get(width, height, config);
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColorFilter(new PorterDuffColorFilter(info.getDrawColor(), PorterDuff.Mode.SRC_ATOP));
//            canvas.drawBitmap(source, 0, 0, paint);
//            return bitmap;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (o == null || !(o instanceof ColorFilterTransformation)) {
//                return false;
//            }
//            ColorFilterTransformation other = (ColorFilterTransformation) o;
//            VenvyImageInfo info = mWeakReferenceImageInfo.get();
//            VenvyImageInfo otherInfo = other.mWeakReferenceImageInfo.get();
//            if (info == null || otherInfo == null) {
//                return false;
//            }
//
//            return info.equals(otherInfo);
//
//        }
//
//        public int hashCode() {
//            return ID.hashCode();
//        }

//        @Override
//        public void updateDiskCacheKey(MessageDigest messageDigest) {
//            try {
//                messageDigest.update(ID.getBytes("utf-8"));
//            } catch (UnsupportedEncodingException e) {
//                messageDigest.update(ID.getBytes());
//            }
//        }
//    }

}
