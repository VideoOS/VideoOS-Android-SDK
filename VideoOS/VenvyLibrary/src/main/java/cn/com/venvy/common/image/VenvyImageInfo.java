package cn.com.venvy.common.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;

import cn.com.venvy.IgnoreHttps;
import cn.com.venvy.processor.annotation.VenvyRouter;

/**
 * Created by yanjiangbo on 2017/6/6.
 */

public class VenvyImageInfo {

    private final String url;  //加载url
    private final Drawable placeHolderImage;  //加载图
    private final Drawable failedImage;     // 失败图
    private final Drawable backgroundImage;  // 背景图
    private final Drawable retryImage;  // 重试图
    private final int radius; // 圆角大小
    @ColorInt
    private final int drawColor;
    private final boolean needPaintColor;
    private final int resizeWidth;
    private final int resizeHeight;
    private final boolean isLocalMedia;
    private Builder builder;


    private VenvyImageInfo(Builder builder) {
        url = builder.url;
        placeHolderImage = builder.placeHolderImage;
        failedImage = builder.failedImage;
        backgroundImage = builder.backgroundImage;
        retryImage = builder.retryImage;
        radius = builder.radius;
        drawColor = builder.drawColor;
        needPaintColor = builder.needPaintColor;
        resizeWidth = builder.resizeWidth;
        resizeHeight = builder.resizeHeight;
        isLocalMedia = builder.isLocalMedia;
        this.builder = builder;
    }

    public boolean isLocalMedia() {
        return isLocalMedia;
    }

    public Builder getBuilder() {
        return builder;
    }

    public String getUrl() {
        return url;
    }


    public Drawable getPlaceHolderImage() {
        return placeHolderImage;
    }

    public Drawable getFailedImage() {
        return failedImage;
    }

    public Drawable getBackgroundImage() {
        return backgroundImage;
    }

    public Drawable getRetryImage() {
        return retryImage;
    }

    public int getRadius() {
        return radius;
    }

    public int getDrawColor() {
        return drawColor;
    }

    public boolean isNeedPaintColor() {
        return needPaintColor;
    }

    public int getResizeWidth() {
        return resizeWidth;
    }

    public int getResizeHeight() {
        return resizeHeight;
    }

    public static final class Builder {
        private String url;
        private Drawable placeHolderImage;
        private Drawable failedImage;
        private Drawable backgroundImage;
        private Drawable retryImage;
        private int radius;
        @ColorInt
        private int drawColor;
        private boolean needPaintColor;
        private boolean isLocalMedia = false;
        private int resizeWidth;
        private int resizeHeight;

        public Builder setUrl(String url) {
            this.url = WebpConvert.convertWebp(IgnoreHttps.ignore(url));
            return this;
        }

        public Builder setPlaceHolderImage(Drawable placeHolderImage) {
            this.placeHolderImage = placeHolderImage;
            return this;
        }

        public Builder isLocalMedia(boolean isLocalMedia) {
            this.isLocalMedia = isLocalMedia;
            return this;
        }

        public Builder setFailedImage(Drawable failedImage) {
            this.failedImage = failedImage;
            return this;
        }

        public Builder setBackgroundImage(Drawable backgroundImage) {
            this.backgroundImage = backgroundImage;
            return this;
        }

        public Builder setRetryImage(Drawable retryImage) {
            this.retryImage = retryImage;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Builder setDrawColor(@ColorInt int drawColor) {
            this.drawColor = drawColor;
            return this;
        }

        public Builder needPaintColor(boolean needPaintColor) {
            this.needPaintColor = needPaintColor;
            return this;
        }

        public Builder setResizeHeight(int resizeHeight) {
            this.resizeHeight = resizeHeight;
            return this;
        }

        public Builder setResizeWidth(int resizeWidth) {
            this.resizeWidth = resizeWidth;
            return this;
        }

        public VenvyImageInfo build() {
            return new VenvyImageInfo(this);
        }
    }
}
