package cn.com.venvy.common.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import cn.com.venvy.processor.annotation.VenvyRouter;

/**
 * Created by Arthur on 2017/6/15.
 */

public class VenvyBitmapInfo {
    private Bitmap mBitmap;
    private Drawable mDrawable;

    public VenvyBitmapInfo(@Nullable Bitmap bitmap, @Nullable Drawable drawable) {
        this.mBitmap = bitmap;
        this.mDrawable = drawable;
    }

    public VenvyBitmapInfo() {

    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public void setDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }
}
