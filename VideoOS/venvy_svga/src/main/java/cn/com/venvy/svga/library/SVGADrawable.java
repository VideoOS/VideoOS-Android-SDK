package cn.com.venvy.svga.library;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;


/**
 * Created by yanjiangbo on 2018/3/26.
 * Done
 */

public class SVGADrawable extends Drawable {

    public SVGAVideoEntity videoEntity;
    public SVGADynamicEntity dynamicEntity;
    private boolean cleared;
    private int currentFrame = 0;
    public ImageView.ScaleType scaleType = ImageView.ScaleType.MATRIX;
    public SVGACanvasDrawer drawer;

    public SVGADrawable(SVGAVideoEntity videoEntity) {
        this(videoEntity, new SVGADynamicEntity());
    }

    public SVGADrawable(SVGAVideoEntity videoEntity, SVGADynamicEntity dynamicEntity) {
        this.videoEntity = videoEntity;
        this.dynamicEntity = dynamicEntity;
        drawer = new SVGACanvasDrawer(videoEntity, dynamicEntity);
    }

    public void clear() {
        if (videoEntity != null) {
            videoEntity.images.clear();
            if (videoEntity.sprites != null) {
                videoEntity.sprites.clear();
            }

        }
        if (dynamicEntity != null) {
            dynamicEntity.clearDynamicObjects();
        }
        if (drawer != null) {
            drawer.clearCache();
        }
    }

    public final void setCleared(boolean paramBoolean) {
        if (this.cleared == paramBoolean) {
            return;
        }
        this.cleared = paramBoolean;
        invalidateSelf();
    }

    public final void setCurrentFrame(int paramInt) {
        if (this.currentFrame == paramInt) {
            return;
        }
        this.currentFrame = paramInt;
        invalidateSelf();
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public boolean getCleared() {
        return cleared;
    }

    @Override
    public void draw(Canvas canvas) {
        if (cleared) {
            return;
        }
        drawer.drawFrame(canvas, currentFrame, scaleType);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void setColorFilter(int color, PorterDuff.Mode mode) {
        super.setColorFilter(color, mode);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public void setAlpha(int alpha) {

    }
}
