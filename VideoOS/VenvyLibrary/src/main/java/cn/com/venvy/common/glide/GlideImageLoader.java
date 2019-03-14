package cn.com.venvy.common.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SquaringDrawable;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;


public class GlideImageLoader implements IImageLoader {
    private static final boolean isLowVersion_380 = true;  //对接方指定，平台使用的glide版本是否为3.8.0以下版本

    @Override
    public void loadImage(@NonNull WeakReference<? extends IImageView> imageViewReference, final @NonNull VenvyImageInfo
            venvyImageInfo, IImageLoaderResult result) {
        IImageView iImageView = imageViewReference.get();
        if (iImageView == null) {
            return;
        }

        Context context = iImageView.getContext();
        if (context == null) {
            VenvyLog.i(iImageView + " image context is null");
            return;
        }
        if (iImageView.getContext() instanceof Activity) {
            Activity activity = (Activity) iImageView.getContext();
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
        if (!(iImageView.getImageView() instanceof ImageView)) {
            return;
        }
        ImageView imageView = (ImageView) iImageView.getImageView();
        CustomImageTarget customImageTarget = new CustomImageTarget(imageView);
        final String url = venvyImageInfo.getUrl();
        DrawableRequestBuilder drawableRequestBuilder;
        drawableRequestBuilder = Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate().listener(new VenvyRequestListener(imageViewReference, venvyImageInfo, result, customImageTarget));


        if (venvyImageInfo.getPlaceHolderImage() != null) {
            drawableRequestBuilder.placeholder(venvyImageInfo.getPlaceHolderImage());
        }

        if (venvyImageInfo.getFailedImage() != null) {
            drawableRequestBuilder.error(venvyImageInfo.getFailedImage());
        }

        if (venvyImageInfo.getResizeWidth() > 0 && venvyImageInfo.getResizeHeight() > 0) {
            drawableRequestBuilder.override(venvyImageInfo.getResizeWidth(),
                    venvyImageInfo.getResizeHeight());
        }
        if (venvyImageInfo.isNeedPaintColor()) {
            drawableRequestBuilder.transform(new ColorFilterTransformation(venvyImageInfo, imageView.getContext()));
        }
        drawableRequestBuilder.into(customImageTarget);
    }

    @Override
    public void preloadImage(Context context, final VenvyImageInfo venvyImageInfo, final IImageLoaderResult result) {
        if (context == null) {
            VenvyLog.i("image context is null");
            return;
        }
        if (venvyImageInfo == null || TextUtils.isEmpty(venvyImageInfo.getUrl())) {
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
        final Context targetContext = context;
        if (VenvyUIUtil.isOnUIThread()) {
            new Thread() {
                @Override
                public void run() {
                    startDownloadImage(targetContext, venvyImageInfo, result);
                }
            }.start();
        } else {
            startDownloadImage(targetContext, venvyImageInfo, result);
        }
    }


    private void startDownloadImage(Context context, VenvyImageInfo venvyImageInfo, IImageLoaderResult result) {
        String url = venvyImageInfo.getUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            final int defaultWidth = venvyImageInfo.getResizeWidth() > 0 ? venvyImageInfo.getResizeWidth() : 200;
            final int defaultHeight = venvyImageInfo.getResizeHeight() > 0 ? venvyImageInfo.getResizeHeight() : 200;
            FutureTarget<File> future = Glide.with(context)
                    .load(url)
                    .downloadOnly(defaultWidth, defaultHeight);
            File cacheFile = future.get();
            if (result != null) {
                if (cacheFile.exists()) {
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(cacheFile));
                    result.loadSuccess(null, url, new VenvyBitmapInfo(bitmap,null));
                } else {
                    result.loadFailure(null, url, new Exception("[preload image error] and cause by: native file is not exists"));
                }
            }
        } catch (Exception e) {
            if (result != null) {
                result.loadFailure(null, url, e);
            }
        }
    }


    private class VenvyRequestListener implements RequestListener<String, GlideDrawable> {
        WeakReference<? extends IImageView> mImageViewRef;
        VenvyImageInfo mVenvyImageInfo;
        String mUrl = null;
        IImageLoaderResult mResult;
        CustomImageTarget mCustomImageTarget;

        VenvyRequestListener(WeakReference<? extends IImageView> imageRef, VenvyImageInfo venvyImageInfo,
                             IImageLoaderResult result, CustomImageTarget customImageTarget) {
            mImageViewRef = imageRef;
            mVenvyImageInfo = venvyImageInfo;
            mUrl = mVenvyImageInfo.getUrl();
            mResult = result;
            this.mCustomImageTarget = customImageTarget;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            VenvyLog.e("errorImage", "---glide图片加载失败--,url==" + mUrl + (e != null ? " ," +
                    "exception==" + e.toString() : ""));
            if (mResult != null) {
                mResult.loadFailure(mImageViewRef, mUrl, e);
            }
            return false;
        }

        /***
         * 该函数在transForm方法之后调用
         */
        @Override
        public boolean onResourceReady(GlideDrawable glideDrawable, String model,
                                       Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            if (mResult != null) {
                mResult.loadSuccess(mImageViewRef, mUrl, new VenvyBitmapInfo(null, glideDrawable));
            }
            if (glideDrawable instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) glideDrawable;
                int count = gifDrawable.getDecoder().getLoopCount();
                //此处是为了兼容Glide低于3.8.0 版本的gif 循环次数Bug
                if (count > 1) {
                    count += 1;
                } else if (count == 0) {
                    count = Integer.MAX_VALUE;
                }
                if (mCustomImageTarget != null) {
                    mCustomImageTarget.setMaxLoopCount(count);
                }
            }
            mResult = null;
            return false;
        }

    }

    private static class ColorFilterTransformation extends BitmapTransformation {
        WeakReference<VenvyImageInfo> mWeakReferenceImageInfo;

        ColorFilterTransformation(VenvyImageInfo venvyImageInfo, Context context) {
            super(context);
            mWeakReferenceImageInfo = new WeakReference<>(venvyImageInfo);
        }

        @Override
        protected Bitmap transform(BitmapPool bitmapPool, Bitmap bitmap, int i, int i1) {
            return paintColor(bitmapPool, bitmap);
        }

        private Bitmap paintColor(BitmapPool bitmapPool, Bitmap source) {
            VenvyLog.i("--glide paintColor-");
            VenvyImageInfo info = mWeakReferenceImageInfo.get();
            if (info == null || !info.isNeedPaintColor()) {
                return source;
            }
            int width = source.getWidth();
            int height = source.getHeight();

            Bitmap.Config config =
                    source.getConfig() != null ? source.getConfig() : Bitmap.Config.ARGB_8888;
            Bitmap bitmap = bitmapPool.get(width, height, config);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(width, height, config);
            }
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColorFilter(new PorterDuffColorFilter(info.getDrawColor(), PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(source, 0, 0, paint);
            return bitmap;
        }

        @Override
        public String getId() {
            return "cn.com.venvy.com.img" + System.currentTimeMillis();
        }
    }

    private class CustomImageTarget extends ImageViewTarget<GlideDrawable> {
        private int maxLoopCount;
        private GlideDrawable resource;

        CustomImageTarget(ImageView view) {
            this(view, -1);
        }

        CustomImageTarget(ImageView view, int maxLoopCount) {
            super(view);
            this.maxLoopCount = maxLoopCount;
        }

        void setMaxLoopCount(int count) {
            if (count > 0) {
                maxLoopCount = count;
            }
        }

        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
            if (!resource.isAnimated()) {
                float viewRatio = (float) this.view.getWidth() / (float) this.view.getHeight();
                float drawableRatio = (float) resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                if (Math.abs(viewRatio - 1.0F) <= 0.05F && Math.abs(drawableRatio - 1.0F) <= 0.05F) {
                    resource = new SquaringDrawable(resource, this.view.getWidth());
                }
            }

            super.onResourceReady(resource, animation);
            this.resource = resource;
            resource.setLoopCount(this.maxLoopCount);
            resource.start();
        }

        protected void setResource(GlideDrawable resource) {
            this.view.setImageDrawable(resource);
        }

        public void onStart() {
            if (this.resource != null) {
                this.resource.start();
            }

        }

        public void onStop() {
            if (this.resource != null) {
                this.resource.stop();
            }

        }
    }
}
