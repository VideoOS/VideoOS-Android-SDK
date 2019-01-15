package cn.com.venvy.common.fresco;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

/**
 * Created by mac on 18/2/7.
 */

public class ColorFilterPostprocessor extends BasePostprocessor {

    private int color;

    public ColorFilterPostprocessor(int color) {
        this.color = color;
    }

    @Override public void process(Bitmap dest, Bitmap source) {
        super.process(dest, source);

        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(source, 0, 0, paint);
    }

    @Override public String getName() {
        return getClass().getSimpleName();
    }

    @Override public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("color=" + color);
    }
}