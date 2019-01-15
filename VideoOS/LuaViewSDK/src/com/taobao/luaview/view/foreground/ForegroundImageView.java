/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.foreground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.image.VenvyImageFactory;

/**
 * ImageView can set foreground
 */
public class ForegroundImageView extends FrameLayout implements IForeground, IImageView {

    ForegroundDelegate mForegroundDelegate;
    boolean enableForeground;
    private IImageView mImageView;

    public ForegroundImageView(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate = new ForegroundDelegate();
        }
        mImageView = VenvyImageFactory.createImage(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
        if (mImageView instanceof View) {
            addView((View) mImageView);
        }
    }


    @Override
    public int getForegroundGravity() {
        //LogUtil.d("Foreground", "getForegroundGravity", enableForeground);
        return enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
                mForegroundDelegate.getForegroundGravity() :
                super.getForegroundGravity();
    }

    @Override
    public void setForegroundGravity(int foregroundGravity) {
        //LogUtil.d("Foreground", "setForegroundGravity", foregroundGravity, enableForeground);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.setForegroundGravity(this, foregroundGravity);
        } else {
            super.setForegroundGravity(foregroundGravity);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        //LogUtil.d("Foreground", "verifyDrawable", who, enableForeground, super.verifyDrawable(who), (who == mForegroundDelegate.getForeground()));
        return enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
                super.verifyDrawable(who) || (who == mForegroundDelegate.getForeground()) :
                super.verifyDrawable(who);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        //LogUtil.d("Foreground", "jumpDrawablesToCurrentState", enableForeground);
        super.jumpDrawablesToCurrentState();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.jumpDrawablesToCurrentState();
        }
    }

    @Override
    protected void drawableStateChanged() {
        //LogUtil.d("Foreground", "drawableStateChanged", enableForeground);
        super.drawableStateChanged();
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableStateChanged(this);
        }
    }


    @Override
    public void setForeground(Drawable foreground) {
        //LogUtil.d("Foreground", "setForeground", enableForeground);
        enableForeground = foreground != null;
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.setForeground(this, foreground);
        } else {
            super.setForeground(foreground);
        }
    }

    @Override
    public void clearForeground() {
        //LogUtil.d("Foreground", "clearForeground");
        enableForeground = false;
    }

    @Override
    public Drawable getForeground() {
        //LogUtil.d("Foreground", "getForeground", enableForeground);
        return enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
                mForegroundDelegate.getForeground() :
                super.getForeground();
    }

    @Override
    public boolean hasForeground() {
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return mForegroundDelegate.getForeground() != null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.getForeground() != null;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //LogUtil.d("Foreground", "onLayout", enableForeground, changed);
        super.onLayout(changed, left, top, right, bottom);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //LogUtil.d("Foreground", "onSizeChanged", enableForeground);
        super.onSizeChanged(w, h, oldw, oldh);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        //LogUtil.d("Foreground", "draw", enableForeground);
        super.draw(canvas);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.draw(this, canvas);
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        //LogUtil.d("Foreground", "drawableHotspotChanged", enableForeground);
        super.drawableHotspotChanged(x, y);
        if (enableForeground && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mForegroundDelegate.drawableHotspotChanged(x, y);
        }
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

    public ImageView.ScaleType getScaleType() {
        return mImageView.getScaleType();
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        mImageView.setScaleType(scaleType);
    }

    public ImageView getImageView() {
        return (ImageView) mImageView;
    }

    @Override
    public void setImageResource(int resourceId) {
        mImageView.setImageResource(resourceId);
    }
}