package cn.com.venvy.common.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.interf.ISvgaImageView;


/**
 * Created by yanjiangbo on 2017/6/6.
 */

public class VenvyImageView extends FrameLayout implements IImageView {

    protected IImageLoader mImageLoader;
    private IImageView mImageView;
    private ISvgaImageView mSvgaImageView;

    public VenvyImageView(Context context) {
        super(context);
        initImageLoader();
        mImageView = initImageView();
        mSvgaImageView = initSvgaImageView();
        if (mImageView != null && mImageView instanceof View) {
            if (((View) mImageView).getParent() == null) {
                this.addView((View) mImageView);
            }
        }
        if (mSvgaImageView != null && mSvgaImageView instanceof View) {
            if (((View) mSvgaImageView).getParent() == null) {
                this.addView((View) mSvgaImageView);
            }
        }
    }

    public ISvgaImageView getSvgaImageView() {
        return mSvgaImageView;
    }


    private void initImageLoader() {
        mImageLoader = VenvyImageLoaderFactory.getImageLoader();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    @Override
    public void setImageURI(Uri uri) {
        mImageView.setImageURI(uri);
    }

    public void setScaleType(ImageView.ScaleType type) {
        mImageView.setScaleType(type);
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return mImageView != null ? mImageView.getScaleType() : null;
    }

    @Override
    public View getImageView() {
        return mImageView != null ? mImageView.getImageView() : null;
    }

    @Override
    public void setImageResource(int resourceId) {
        mImageView.setImageResource(resourceId);
    }


    public void loadImage(@NonNull String url) {
        VenvyImageInfo venvyImageInfo =
                new VenvyImageInfo.Builder().setUrl(url).build();
        loadImage(venvyImageInfo);
    }

    public void loadImage(@NonNull String url, @Nullable IImageLoaderResult result) {
        VenvyImageInfo venvyImageInfo =
                new VenvyImageInfo.Builder().setUrl(url).build();
        loadImage(venvyImageInfo, result);
    }

    public void loadImage(@NonNull VenvyImageInfo info) {
        loadImage(info, null);
    }

    public void loadImage(@NonNull VenvyImageInfo info, IImageLoaderResult result) {
        String url = info.getUrl();
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(".svga")) {
                if (mImageView != null) {
                    removeView((View) mImageView);
                    mImageView = null;
                }
                if (mSvgaImageView == null) {
                    mSvgaImageView = initSvgaImageView();
                }
                if (mSvgaImageView != null && mSvgaImageView instanceof View && mSvgaImageView instanceof IImageLoader) {
                    ViewParent parent = ((View) mSvgaImageView).getParent();
                    if (parent == null) {
                        this.addView((View) mSvgaImageView);
                    }
                    ((IImageLoader) mSvgaImageView).loadImage(null, info, new ImageLoadResultAdapter(result));
                }
            } else {
                if (mSvgaImageView != null) {
                    removeView((View) mSvgaImageView);
                    mSvgaImageView = null;
                }
                if (mImageView == null) {
                    mImageView = initImageView();
                }
                if (mImageView != null && mImageView instanceof View) {
                    ViewParent parent = ((View) mImageView).getParent();
                    if (parent == null) {
                        this.addView((View) mImageView);
                    }
                    if (mImageLoader != null) {
                        mImageLoader.loadImage(new WeakReference<>(mImageView), info, new ImageLoadResultAdapter(result));
                    }
                }
            }
        }
    }

    private ISvgaImageView initSvgaImageView() {
        ISvgaImageView svagImageView = VenvyImageFactory.createSvgaImage(getContext());
        if (svagImageView != null) {
            if (svagImageView instanceof View) {
                FrameLayout.LayoutParams svgaParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                ((View) svagImageView).setLayoutParams(svgaParams);
            }
        }
        return svagImageView;
    }

    private IImageView initImageView() {
        IImageView imageView = VenvyImageFactory.createImage(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        return imageView;
    }
}
