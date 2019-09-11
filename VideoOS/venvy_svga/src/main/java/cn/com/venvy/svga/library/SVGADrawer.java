package cn.com.venvy.svga.library;

import android.graphics.Canvas;
import android.widget.ImageView;

/**
 * Created by yanjiangbo on 2018/3/27.
 * Done
 */

public class SVGADrawer {
    private SVGAVideoEntity videoEntity;
    protected ScaleEntity scaleEntity;

    public SVGADrawer(SVGAVideoEntity entity) {
        this.videoEntity = entity;
        this.scaleEntity = new ScaleEntity();
    }

    public void drawFrame(Canvas canvas, int frameIndex, ImageView.ScaleType scaleType) {
        preformScaleType(canvas, scaleType);
    }

    public void preformScaleType(Canvas canvas, ImageView.ScaleType scaleType) {
        if (scaleEntity == null || videoEntity == null) {
            return;
        }
        scaleEntity.performScaleType(canvas.getWidth(), canvas.getHeight(), (float) videoEntity.videoSize.width, (float) videoEntity.videoSize.height, scaleType);
    }

}
