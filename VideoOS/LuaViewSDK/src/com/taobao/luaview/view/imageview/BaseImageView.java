/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.imageview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.drawable.LVGradientDrawable;
import com.taobao.luaview.view.foreground.ForegroundImageView;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;

import java.lang.ref.WeakReference;

/**
 * Base ImageView
 *
 * @author song
 * @date 16/3/9
 */
public abstract class BaseImageView extends ForegroundImageView {
    LVGradientDrawable mStyleDrawable = null;
    Path mPath = null;

    String mUrl;
    LuaFunction mLuaCallBack;
    protected Boolean mAttachedWindow = null;
    protected boolean isNetworkMode = false;
    private String mPlaceHolderImg;
    private Drawable mPlaceHolderDrawable;

    public void setIsNetworkMode(boolean isNetworkMode) {
        this.isNetworkMode = isNetworkMode;
    }

    public BaseImageView(Context context) {
        super(context);
        if (context instanceof Activity) {
            ImageActivityLifeCycle.getInstance(((Activity) context).getApplication()).watch(this);
        }
    }


    public void loadUrl(String url, DrawableLoadCallback callback) {
        this.mUrl = url;
        ImageProvider provider = LuaView.getImageProvider();
        if (provider != null) {
            provider.load(getContext(), new WeakReference<>(this), url, callback);
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public String getPlaceHolderImg() {
        return mPlaceHolderImg;
    }

    public void setPlaceHolderImg(String placeHolderImg) {
        mPlaceHolderImg = placeHolderImg;
    }

    public void setLuaFunction(LuaFunction luaFunction) {
        this.mLuaCallBack = luaFunction;
    }

    public void setPlaceHolderDrawable(Drawable placeHolderDrawable) {
        mPlaceHolderDrawable = placeHolderDrawable;
    }

    public Drawable getPlaceHolderDrawable() {
        return mPlaceHolderDrawable;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        restoreImage();
        mAttachedWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        releaseBitmap();
        mAttachedWindow = false;
    }


    public void restoreImage() {
        if (isNetworkMode && mAttachedWindow != null) {// 恢复被清空的image，只有已经被加过才恢复
            if (mUrl != null) {
                loadUrl(mUrl, new DrawableLoadCallback() {
                    @Override
                    public void onLoadResult(Drawable drawable) {
                        if (mLuaCallBack != null) {
                            LuaUtil.callFunction(mLuaCallBack, drawable != null ? LuaBoolean.TRUE : LuaBoolean.FALSE);
                        }
                    }
                });
            } else {
                setImageDrawable(null);
            }
        }
    }

    public void releaseBitmap() {// 释放图片内存
        if (isNetworkMode) {//只有被加过才释放
            setImageDrawable(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean hasStyle = setupStyleDrawable();

        if (hasStyle && canvas != null) {
            try {
                canvas.clipPath(getClipPath());
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }

        super.onDraw(canvas);

        if (hasStyle) {//背景放到上面画，默认为透明颜色
            mStyleDrawable.setColor(Color.TRANSPARENT);
            mStyleDrawable.draw(canvas);
        }
    }

    //-------------------------------------background style-----------------------------------------

    /**
     * get clip path of StyleDrawable
     *
     * @return
     */
    Path getClipPath() {
//        if (mPath == null) {
//            mPath = new Path();
//        }
        mPath = new Path();
        Rect rect = mStyleDrawable.getBounds();
        float radius = mStyleDrawable.getCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius, radius, Path.Direction.CW);
        } else {
            mPath.addCircle(rect.left + radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.left + radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addRect(rect.left + radius, rect.top, rect.right - radius, rect.bottom, Path.Direction.CW);
            mPath.addRect(rect.left, rect.top + radius, rect.right, rect.bottom - radius, Path.Direction.CW);
        }
        return mPath;
    }


    /**
     * 设置好drawable的样式
     *
     * @return
     */
    boolean setupStyleDrawable() {
        if (mStyleDrawable != null) {
            mStyleDrawable.setBounds(0, 0, getWidth(), getHeight());
            return true;
        }
        return false;
    }

    synchronized LVGradientDrawable getStyleDrawable() {
        if (mStyleDrawable == null) {
            mStyleDrawable = new LVGradientDrawable();
        }
        return mStyleDrawable;
    }

    /**
     * set corner radius
     *
     * @param radius
     */
    public void setCornerRadius(float radius) {
        getStyleDrawable().setCornerRadius(radius);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setCornerRadius(DimenUtil.dpiToPx(radius));
        LuaViewUtil.setBackground(this, drawable);
    }

    public float getCornerRadius() {
        if (mStyleDrawable != null) {
            return mStyleDrawable.getCornerRadius();
        }
        return 0;
    }

    /**
     * 设置边框宽度
     */
    public void setStrokeWidth(int width) {
        getStyleDrawable().setStrokeWidth(width);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setStrokeWidth(width);
        LuaViewUtil.setBackground(this, drawable);
    }

    public int getStrokeWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeWidth() : 0;
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        getStyleDrawable().setStrokeColor(color);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setStrokeColor(color);
        LuaViewUtil.setBackground(this, drawable);
    }

    public int getStrokeColor() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeColor() : 0;
    }

    /**
     * Dash size
     *
     * @param dashWidth
     * @param dashGap
     */
    public void setBorderDash(Float dashWidth, Float dashGap) {
        getStyleDrawable().setDashSize(dashWidth, dashGap);
    }

    public float getBorderDashWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getDashWidth() : 0;
    }

    public float getBorderDashGap() {
        return mStyleDrawable != null ? mStyleDrawable.getDashGap() : 0;
    }

}
