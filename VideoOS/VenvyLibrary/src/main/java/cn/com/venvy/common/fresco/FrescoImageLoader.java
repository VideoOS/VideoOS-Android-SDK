package cn.com.venvy.common.fresco;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageLoaderResult;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyBitmapInfo;
import cn.com.venvy.common.image.VenvyImageInfo;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/5/2.
 */

public class FrescoImageLoader implements IImageLoader {


    @Override
    public void loadImage(final WeakReference<? extends IImageView> imageViewReference, VenvyImageInfo venvyImageInfo, final @Nullable IImageLoaderResult result) {
        IImageView iImageView = imageViewReference.get();
        if (iImageView == null) {
            return;
        }
        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(iImageView.getContext());
        }
        if (venvyImageInfo == null) {
            return;
        }
        final String url = venvyImageInfo.getUrl();
        if (TextUtils.isEmpty(url)) {
            VenvyLog.e(getClass().getName(), "loaderImage error, because url is null");
            return;
        }

        if (!(iImageView.getImageView() instanceof SimpleDraweeView)) {
            return;
        }

        SimpleDraweeView imageView = (SimpleDraweeView) iImageView.getImageView();

        ImageRequest imageRequest = buildImageRequest(imageView, venvyImageInfo);
        DraweeController draweeController =
                Fresco.newDraweeControllerBuilder()
                        .setControllerListener(new BaseControllerListener<ImageInfo>() {
                            @Override
                            public void onFailure(String id, Throwable throwable) {
                                super.onFailure(id, throwable);
                                VenvyLog.e("ImageLoader", "[onFailureImpl] " + (throwable == null
                                        ? "load image error" : throwable));
                                Exception exception = throwable != null ? new Exception
                                        (throwable) : new Exception();
                                if (result != null) {
                                    result.loadFailure(imageViewReference, url, exception);
                                }

                            }

                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo,
                                                        Animatable animatable) {
                                super.onFinalImageSet(id, imageInfo, animatable);
                                if (result == null) {
                                    return;
                                }
                                if (imageInfo != null) {//需要获取图片的大小
                                    result.loadSuccess(imageViewReference, url,
                                            new VenvyBitmapInfo());
                                }
                            }
                        })
                        .setImageRequest(imageRequest)
                        .setOldController(imageView.getController())
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        imageView.setController(draweeController);
    }

    @Override
    public void preloadImage(Context context, VenvyImageInfo venvyImageInfo, final @Nullable IImageLoaderResult result) {
        if (!Fresco.hasBeenInitialized()) {
            return;
        }

        if (context == null) {
            VenvyLog.i("image context is null");
            return;
        }

        if (venvyImageInfo == null || TextUtils.isEmpty(venvyImageInfo.getUrl())) {
            return;
        }
        final String url = venvyImageInfo.getUrl();
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
                .fetchDecodedImage(imageRequest, this);
        DataSubscriber dataSubscriber = new
                BaseDataSubscriber<CloseableReference<CloseableImage>>() {
                    @Override
                    protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>>
                                                           dataSource) {
                        if (!dataSource.isFinished()) {
                            return;
                        }
                        if (result != null) {
                            result.loadSuccess(null, url, null);
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>>
                                                         dataSource) {
                        Throwable throwable = dataSource.getFailureCause();
                        VenvyLog.e("ImageLoader", "[onFailureImpl] " + (throwable == null ? "load image " +
                                "error" : throwable));
                        Exception exception = throwable != null ? new Exception(throwable) : new
                                Exception();
                        result.loadFailure(null, url, exception);
                    }
                };
        dataSource.subscribe(dataSubscriber, UiThreadImmediateExecutorService.getInstance());
    }

    private ImageRequest buildImageRequest(@Nullable SimpleDraweeView imageView, final VenvyImageInfo
            venvyImageInfo) {
        ImageRequestBuilder builder;
        if (venvyImageInfo.isLocalMedia()) {
            builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse
                    ("file://" + venvyImageInfo.getUrl()));
        } else {
            builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse
                    (venvyImageInfo.getUrl()));
        }

        if (imageView != null) {
            GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
            if (hierarchy == null) {
                GenericDraweeHierarchyBuilder hierarchyBuilder =
                        new GenericDraweeHierarchyBuilder(imageView.getContext().getResources());
                hierarchy = hierarchyBuilder.build();
                imageView.setHierarchy(hierarchy);
            }
            hierarchy = imageView.getHierarchy();
            if (hierarchy != null) {
                hierarchy.setFadeDuration(300);
                hierarchy.setPlaceholderImage(venvyImageInfo.getPlaceHolderImage());
                hierarchy.setFailureImage(venvyImageInfo.getFailedImage());
                hierarchy.setBackgroundImage(venvyImageInfo.getBackgroundImage());
                hierarchy.setRetryImage(venvyImageInfo.getRetryImage());
                RoundingParams roundingParams = hierarchy.getRoundingParams();
                if (roundingParams != null) {
                    roundingParams.setCornersRadius(venvyImageInfo.getRadius());
                }
                hierarchy.setRoundingParams(roundingParams);
            }

            if (venvyImageInfo.isNeedPaintColor()) {
                int color = venvyImageInfo.getDrawColor();
                builder.setPostprocessor(new ColorFilterPostprocessor(color));
            }
            if (venvyImageInfo.getResizeHeight() > 0 && venvyImageInfo.getResizeWidth() > 0) {
                builder.setResizeOptions(new ResizeOptions(venvyImageInfo.getResizeWidth(),
                        venvyImageInfo.getResizeHeight()));
            }
        }
        return builder.build();
    }

}
