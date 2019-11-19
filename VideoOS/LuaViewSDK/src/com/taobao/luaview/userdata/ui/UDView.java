/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root
 * directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.userdata.base.BaseUserdata;
import com.taobao.luaview.userdata.constants.UDViewEffect;
import com.taobao.luaview.util.AnimatorUtil;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.LVImageView;
import com.taobao.luaview.view.LVRecyclerView;
import com.taobao.luaview.view.drawable.LVGradientDrawable;
import com.taobao.luaview.view.foreground.ForegroundDelegate;
import com.taobao.luaview.view.interfaces.ILVNativeViewProvider;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.List;

/**
 * View 数据封装
 *
 * @param <T>
 * @author song
 */
public class UDView<T extends View> extends BaseUserdata {
    //回调
    public LuaValue mCallback;
    //Mediao生命周期回调
    public LuaValue mMqttCallback;
    //点击
    LuaValue mOnClick;
    LuaValue mOnLongClick;
    LuaValue mOnTouch;
    LuaTable mOnTouchEventData;


    //动画列表
    List<Animator> mAnimators;

    //Flexbox layout

    //effects
    Integer mEffects;

    //values remove by yanqiu
//     float[]  = null;

    public UDView(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        setSize(0, 0);//默认初始化size全0
    }

    public T getView() {
        Object userdata = userdata();
        return userdata != null ? (T) userdata : null;
    }

    /**
     * get native view
     *
     * @return
     */
    public LuaValue getNativeView() {
        View view = getView();
        if (view instanceof ILVNativeViewProvider) {
            view = ((ILVNativeViewProvider) view).getNativeView();
        }
        return view != null ? CoerceJavaToLua.coerce(view) : LuaValue.NIL;
    }

    public Context getContext() {
        return getView() != null ? getView().getContext() : null;
    }

    /**
     * 刷新
     *
     * @return
     */
    public UDView invalidate() {
        View view = getView();
        if (view != null) {
            view.invalidate();
        }
        return this;
    }

    /**
     * 设置padding
     *
     * @param top
     * @param left
     * @param right
     * @param bottom
     * @return
     */
    public UDView setPadding(int left, int top, int right, int bottom) {
        View view = getView();
        if (view != null) {
            view.setPadding(left, top, right, bottom);
        }
        return this;
    }


    public UDView setMqttCallback(final LuaValue callbacks) {
        mMqttCallback = callbacks;
        return this;
    }

    public LuaValue getMqttCallback() {
        return mMqttCallback != null ? mMqttCallback : LuaValue.NIL;
    }


    public int getPaddingLeft() {
        return getView() != null ? getView().getPaddingLeft() : 0;
    }

    public int getPaddingTop() {
        return getView() != null ? getView().getPaddingTop() : 0;
    }

    public int getPaddingRight() {
        return getView() != null ? getView().getPaddingRight() : 0;
    }

    public int getPaddingBottom() {
        return getView() != null ? getView().getPaddingBottom() : 0;
    }

    /**
     * 设置Margin
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public UDView setMargin(Integer left, Integer top, Integer right, Integer bottom) {//TODO
        // 这里的margin，MarginLayoutParams上有一个问题，当left+right=width或者top+bottom=height有一个问题
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (left != null) {
                layoutParams.leftMargin = left;
            }
            if (top != null) {
                layoutParams.topMargin = top;
            }
            if (right != null) {
                layoutParams.rightMargin = right;
            }
            if (bottom != null) {
                layoutParams.bottomMargin = bottom;
            }
            view.setLayoutParams(layoutParams);
        }
        return this;
    }


    public int getMarginLeft() {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return layoutParams.leftMargin;
        }
        return 0;
    }

    public int getMarginTop() {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return layoutParams.topMargin;
        }
        return 0;
    }

    public int getMarginRight() {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return layoutParams.rightMargin;
        }
        return 0;
    }

    public int getMarginBottom() {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return layoutParams.bottomMargin;
        }
        return 0;
    }

    /**
     * 设置背景颜色 & alpha
     *
     * @param color int
     */
    public UDView setBackgroundColorAndAlpha(Integer color, Double alpha) {
        setBackgroundColor(color);
        setBackgroundAlpha(alpha);
        return this;
    }

    /**
     * 设置view的alpha
     *
     * @param alpha 百分比的alpha 0-1
     */
    public UDView setBackgroundAlpha(Double alpha) {
        if (alpha != null) {
            T view = getView();
            if (view != null) {
                Drawable drawable = view.getBackground() != null ? view.getBackground() : null;
                if (drawable != null) {
                    drawable.setAlpha((int) (alpha * 0xFF));
                    LuaViewUtil.setBackground(view, drawable);
                }
            }
        }
        return this;
    }

    /**
     * 获取alpha
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public float getBackgroundAlpha() {
        T view = getView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return (view != null && view.getBackground() != null) ?
                    view.getBackground().getAlpha() / 255.0f : 1;
        }
        return 1;
    }

    /**
     * 设置背景颜色
     *
     * @param color int
     */
    public UDView setBackgroundColor(Integer color) {
        if (color != null) {
            View view = getView();
            if (view != null) {
                LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable
                        ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
                drawable.setColor(color);
                LuaViewUtil.setBackground(view, drawable);
            }
        }
        return this;
    }

    /**
     * 获取background颜色
     *
     * @return
     */
    public int getBackgroundColor() {
        View view = getView();
        if (view != null) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof LVGradientDrawable) {
                return ((LVGradientDrawable) drawable).getColorValue();
            }
        }
        return 0;
    }

    public UDView setCornerRadius(float radius) {
        T view = getView();
        if (view != null) {
            LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ?
                    (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setCornerRadius(radius);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    public float getCornerRadius() {
        T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ?
                    ((LVGradientDrawable) view.getBackground()).getCornerRadius() : 0;
        }
        return 0;
    }

    public UDView setCornerRadii(float[] radii) {
        T view = getView();
        if (view != null) {
            LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ?
                    (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setCornerRadii(radii);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    /**
     * 边框宽度
     *
     * @param borderWidth
     * @return
     */
    public UDView setBorderWidth(int borderWidth) {
        T view = getView();
        if (view != null) {
            LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ?
                    (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setStrokeWidth(borderWidth);
            LuaViewUtil.setBackground(view, drawable);
        }
        return this;
    }

    public int getBorderWidth() {
        T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ?
                    ((LVGradientDrawable) view.getBackground()).getStrokeWidth() : 0;
        }
        return 0;
    }

    /**
     * 边框宽度
     *
     * @param borderColor
     * @return
     */
    public UDView setBorderColor(Integer borderColor) {
        if (borderColor != null) {
            T view = getView();
            if (view != null) {
                LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable
                        ? (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
                drawable.setStrokeColor(borderColor);
                LuaViewUtil.setBackground(view, drawable);
            }
        }
        return this;
    }

    public int getBorderColor() {
        T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ?
                    ((LVGradientDrawable) view.getBackground()).getStrokeColor() : 0;
        }
        return 0;
    }

    /**
     * 边框虚线(宽、间隔）
     *
     * @param dashWidth
     * @param dashGap
     * @return
     */
    public UDView setBorderDashSize(float dashWidth, float dashGap) {
        T view = getView();
        if (view != null) {
            LVGradientDrawable drawable = view.getBackground() instanceof LVGradientDrawable ?
                    (LVGradientDrawable) view.getBackground() : new LVGradientDrawable();
            drawable.setDashSize(dashWidth, dashGap);
        }
        return this;
    }

    public float getBorderDashWidth() {
        T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ?
                    ((LVGradientDrawable) view.getBackground()).getDashWidth() : 0;
        }
        return 0;
    }

    public float getBorderDashGap() {
        T view = getView();
        if (view != null) {
            return view.getBackground() instanceof LVGradientDrawable ?
                    ((LVGradientDrawable) view.getBackground()).getDashGap() : 0;
        }
        return 0;
    }


    /**
     * set position of a view
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public UDView setFrame(int x, int y, int width, int height) {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.leftMargin = x;
            layoutParams.topMargin = y;
            layoutParams.width = width;
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);

        }
        return this;
    }

    /**
     * 设置X坐标
     *
     * @param x
     * @return
     */
    public UDView setXY(int x, int y) {
        View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = x;
                layoutParams.topMargin = y;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 设置X坐标
     *
     * @param x
     * @return
     */
    public UDView setX(int x) {
        View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = x;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 获取x坐标
     *
     * @return
     */
    public int getX() {
        View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
            }
        }
        return 0;
    }

    /**
     * 设置Y坐标
     *
     * @param y
     * @return
     */
    public UDView setY(int y) {
        View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.topMargin = y;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }


    /**
     * 获取y坐标
     *
     * @return
     */
    public int getY() {
        View view = getView();
        if (view != null) {
            if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                return ((RelativeLayout.LayoutParams) view.getLayoutParams()).topMargin;
            }
        }
        return 0;
    }

    /**
     * 设置Translation X, Y
     *
     * @param tx
     * @param ty
     * @return
     */
    public UDView setTranslation(Float tx, Float ty) {
        View view = getView();
        if (view != null) {
            if (tx != null) {
                view.setTranslationX(tx);
            }
            if (ty != null) {
                view.setTranslationY(ty);
            }
        }
        return this;
    }

    public UDView setTranslation(float translationX, float translationY) {
        View view = getView();
        if (view != null) {
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
        return this;
    }


    /**
     * 获取TranslationX
     *
     * @return
     */
    public float getTranslationX() {
        View view = getView();
        if (view != null) {
            return view.getTranslationX();
        }
        return 0;
    }

    /**
     * 获取TranslationY
     *
     * @return
     */
    public float getTranslationY() {
        View view = getView();
        if (view != null) {
            return view.getTranslationY();
        }
        return 0;
    }


    /**
     * 获取宽度
     *
     * @param width
     * @return
     */
    public UDView setWidth(int width) {
        if (width >= 0) {
            View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = width;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 获取宽度
     *
     * @return
     */
    public int getWidth() {
        View view = getView();
        if (view != null && view.getLayoutParams() != null) {
            return view.getLayoutParams().width >= 0 ? view.getLayoutParams().width :
                    view.getWidth();
        }
        return view != null ? view.getWidth() : 0;
    }

    /**
     * 获取宽度
     *
     * @param width
     * @return
     */
    public UDView setMinWidth(int width) {
        if (width >= 0) {
            View view = getView();
            if (view != null) {
                view.setMinimumWidth(width);
            }
        }
        return this;
    }

    /**
     * 获取宽度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMinWidth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getView() != null ? getView().getMinimumWidth() : 0;
        } else {
            return 0;
        }
    }


    /**
     * 获取高度
     *
     * @param height
     * @return
     */
    public UDView setHeight(int height) {
        if (height > 0) {
            View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 获取高度
     *
     * @return
     */
    public int getHeight() {
        View view = getView();
        if (view != null && view.getLayoutParams() != null) {
            return view.getLayoutParams().height >= 0 ? view.getLayoutParams().height :
                    view.getHeight();
        }
        return view != null ? view.getHeight() : 0;
    }

    /**
     * 设置view的宽、高
     *
     * @param width
     * @param height
     */
    public UDView setSize(int width, int height) {
        if (width >= 0 && height >= 0) {
            View view = getView();
            if (view != null && view.getLayoutParams() != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                view.setLayoutParams(layoutParams);
            }
        }
        return this;
    }

    /**
     * 设置背景和透明度
     */
    public UDView setBackgroundResourceAndAlpha(String picName, Double alpha) {
        setBackgroundResource(picName);
        setBackgroundAlpha(alpha);
        return this;
    }

    /**
     * setBackgroundResource
     *
     * @param picName String
     */
    public UDView setBackgroundResource(String picName) {
        View view = getView();
        if (view != null) {
            if (getLuaResourceFinder() != null) {
                Drawable drawable = getLuaResourceFinder().findDrawable(picName);
                if (drawable != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(drawable);
                    } else {
                        view.setBackgroundDrawable(drawable);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 设置view位于父容器的中心
     *
     * @return
     */
    public UDView align(Integer... rules) {
        View view = getView();
        if (view != null && view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (rules != null) {
                for (int rule : rules) {
                    layoutParams.addRule(rule);
                }
            }
            view.setLayoutParams(layoutParams);
        }
        return this;
    }

    /**
     * 设置view的中心移动到x,y
     *
     * @param x
     * @param y
     */
    public UDView setCenter(int x, int y) {
        return setXY(x - getWidth() / 2, y - getHeight() / 2);
    }

    /**
     * 设置view的中心移动到x
     *
     * @param x
     */
    public UDView setCenterX(int x) {
        return setX(x - getWidth() / 2);
    }

    /**
     * 设置view的中心移动到y
     *
     * @param y
     */
    public UDView setCenterY(int y) {
        return setY(y - getHeight() / 2);
    }


    /**
     * 开始动画
     *
     * @return
     */
    public UDView show() {
        T view = getView();
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShow() {
        return getView() != null && getView().getVisibility() == View.VISIBLE;
    }

    /**
     * 隐藏动画
     *
     * @return
     */
    public UDView hide() {
        T view = getView();
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 是否隐藏
     *
     * @return
     */
    public boolean isHide() {
        return getView() == null || getView().getVisibility() != View.VISIBLE;
    }

    /**
     * 设置是否有效
     *
     * @param enable
     * @return
     */
    public UDView setEnabled(boolean enable) {
        View view = getView();
        if (view != null) {
            if (view instanceof LVRecyclerView) {
                LVRecyclerView view2 = (LVRecyclerView) view;
                view2.setNestedScrollingEnabled(false);
                return this;
            }
            view.setEnabled(enable);
        }
        return this;
    }

    /**
     * 是否有效
     *
     * @return
     */
    public boolean isEnabled() {
        return getView() != null && getView().isEnabled();
    }

    /**
     * 设置view的alpha
     */
    public UDView setAlpha(float alpha) {
        View view = getView();
        if (view != null) {
            view.setAlpha(alpha);
        }
        return this;
    }

    /**
     * 获取alpha
     *
     * @return
     */
    public float getAlpha() {
        return getView() != null ? getView().getAlpha() : 1f;
    }

    /**
     * 从父容器中移除
     * <p><strong>Note:</strong> do not invoke this method from
     * draw(Canvas)}, onDraw(Canvas),
     * dispatchDraw(Canvas) or any related method.</p>
     * <p/>
     * 见 SwipeRefreshLayout.OnRefreshListener
     *
     * @return
     */
    public UDView removeFromParent() {
        View view = getView();
        if (view != null && view.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view.getParent();
            LuaViewUtil.removeView(parent, view);
        }
        return this;
    }

    /**
     * 设置锚点
     *
     * @param pivotX
     * @param pivotY
     * @return
     */
    public UDView setPivot(float pivotX, float pivotY) {
        View view = getView();
        if (view != null) {
            view.setPivotX(pivotX);
            view.setPivotY(pivotY);
        }
        return this;
    }

    /**
     * 获取x锚点
     *
     * @return
     */
    public float getPivotX() {
        return getView() != null ? getView().getPivotX() : 0f;
    }

    /**
     * 获取y锚点
     *
     * @return
     */
    public float getPivotY() {
        return getView() != null ? getView().getPivotY() : 0f;
    }

    /**
     * 旋转并缩放
     *
     * @param rotation
     * @return
     */
    public UDView setRotation(float rotation) {
        View view = getView();
        if (view != null) {
            view.setRotation(rotation);
        }
        return this;
    }

    public float getRotation() {
        return getView() != null ? getView().getRotation() : 0f;
    }

    /**
     * 旋转并缩放
     *
     * @param rotationX
     * @param rotationY
     * @return
     */
    public UDView setRotationXY(float rotationX, float rotationY) {
        View view = getView();
        if (view != null) {
            view.setRotationX(rotationX);
            view.setRotationY(rotationY);
        }
        return this;
    }

    public float getRotationX() {
        return getView() != null ? getView().getRotationX() : 0f;
    }

    public float getRotationY() {
        return getView() != null ? getView().getRotationY() : 0f;
    }

    /**
     * 缩放
     *
     * @param scaleX
     * @param scaleY
     * @return
     */
    public UDView setScale(float scaleX, float scaleY) {
        View view = getView();
        if (view != null) {
            view.setScaleX(scaleX);
            view.setScaleY(scaleY);
        }
        return this;
    }

    public UDView setScaleX(float scaleX) {
        View view = getView();
        if (view != null) {
            view.setScaleX(scaleX);
        }
        return this;
    }

    public float getScaleX() {
        return getView() != null ? getView().getScaleX() : 0f;
    }

    public UDView setScaleY(float scaleY) {
        View view = getView();
        if (view != null) {
            view.setScaleY(scaleY);
        }
        return this;
    }

    public float getScaleY() {
        return getView() != null ? getView().getScaleY() : 0f;
    }

    /**
     * set callback
     *
     * @param callbacks 回调集合
     */
    public UDView setCallback(LuaValue callbacks) {
        this.mCallback = callbacks;
        if (this.mCallback != null) {
            mOnClick = mCallback.isfunction() ? mCallback : LuaUtil.getFunction(mCallback,
                    "onClick", "Click", "OnClick", "click");//TODO OnClick
            mOnLongClick = mCallback.istable() ? LuaUtil.getFunction(mCallback, "onLongClick",
                    "LongClick", "OnLongClick", "longClick") : null;//TODO OnLongClick
            mOnTouch = mCallback.istable() ?
                    LuaUtil.getFunction(mCallback, "onTouch", "OnTouch") : null;//TODO OnTouch

            //setup listener
            setOnClickListener();
            setOnLongClickListener();
            setOnTouchListener();

            //setup click effects
            setupClickEffects(LuaUtil.isValid(mOnClick) || LuaUtil.isValid(mOnLongClick) || LuaUtil.isValid(mOnTouch));
        }
        return this;
    }

    void setupClickEffects(boolean isValid) {
        //setup effect
        if (LuaViewConfig.isAutoSetupClickEffects()) {
//            if (isValid) {
//                setEffects(UDViewEffect.EFFECT_CLICK);
//            } else {
//                setEffects(UDViewEffect.EFFECT_NONE);
//            } remove by yanqiu
            setEffects(isValid ? UDViewEffect.EFFECT_CLICK : UDViewEffect.EFFECT_NONE);
        }
    }

    /**
     * 设置点击事件
     *
     * @param callback
     * @return
     */
    public UDView setOnClickCallback(LuaValue callback) {
        this.mOnClick = callback;
        setOnClickListener();
        return this;
    }

    /**
     * 设置长按事件
     *
     * @param callback
     * @return
     */
    public UDView setOnLongClickCallback(LuaValue callback) {
        this.mOnLongClick = callback;
        setOnLongClickListener();
        return this;
    }

    /**
     * 设置触摸事件
     *
     * @param callback
     * @return
     */
    public UDView setOnTouchCallback(LuaValue callback) {
        this.mOnTouch = callback;
        setOnTouchListener();
        return this;
    }

    /**
     * 点击
     *
     * @return
     */
    public void callOnClick(Object... params) {
        LuaUtil.callFunction(this.mOnClick, params);
    }

    public boolean callOnLongClick() {
        return LuaUtil.callFunction(this.mOnLongClick).optboolean(false);
    }

    public boolean callOnTouch(Object... params) {
        return LuaUtil.callFunction(this.mOnTouch, params).arg1().optboolean(false);
    }

    /**
     * 点击
     */
    void setOnClickListener() {
        final T view = getView();
        if (view != null) {
            if (LuaUtil.isValid(this.mOnClick)) {
                view.setOnTouchListener(mCommontOnTouchListener);
            }//TODO mOnClick 为nil的时候（非null）的时候如何清空onClick，且不影响事件传递
        }
    }

    /**
     * 长按
     */
    void setOnLongClickListener() {
        T view = getView();
        if (view != null) {
            if (LuaUtil.isValid(this.mOnLongClick)) {
                view.setOnTouchListener(mCommontOnTouchListener);
            }//TODO mOnClick 为nil的时候（非null）的时候如何清空onClick，且不影响事件传递
        }
    }

    /**
     * 设置触摸事件
     */

    void setOnTouchListener() {
        T view = getView();
        if (view != null) {
            if (LuaUtil.isValid(this.mOnTouch)) {
                view.setOnTouchListener(mCommontOnTouchListener);
            }//TODO mOnClick 为nil的时候（非null）的时候如何清空onClick，且不影响事件传递
        }
    }

    private long mActionDownTime = 0;
    private boolean mCallOnLongClick = false;
    private boolean onLongClickCallback = false;
    private View.OnTouchListener mCommontOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            T view = getView();
            if (view != null) {
                //onTouch
                if (LuaUtil.isValid(UDView.this.mOnTouch)) {
                    if (mOnTouchEventData == null) {
                        mOnTouchEventData = new LuaTable();
                    }
                    if (event != null) {
                        mOnTouchEventData.set("action", event.getActionMasked());//0按下，1起来，2移动，3取消，4外部
                        mOnTouchEventData.set("pointer", event.getPointerId(event.getActionIndex()));
                        mOnTouchEventData.set("x", DimenUtil.pxToDpi(event.getX()));
                        mOnTouchEventData.set("y", DimenUtil.pxToDpi(event.getY()));
                        mOnTouchEventData.set("gx", DimenUtil.pxToDpi(event.getRawX()));
                        mOnTouchEventData.set("gy", DimenUtil.pxToDpi(event.getRawY()));
                    }
                    if (callOnTouch(mOnTouchEventData)) {
                        return true;
                    }
                }

                if(!LuaUtil.isValid(UDView.this.mOnClick) && !LuaUtil.isValid(UDView.this.mOnLongClick)){
                    return false;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mActionDownTime = System.currentTimeMillis();
                    return true;
                }

                //onLongClick
                if (!mCallOnLongClick && LuaUtil.isValid(UDView.this.mOnLongClick) && (System.currentTimeMillis() - mActionDownTime) >= 500) {
                    mCallOnLongClick = true;
                    if (callOnLongClick()) {
                        onLongClickCallback = true;
                        return true;
                    }
                }

                //onClick
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mCallOnLongClick = false;
                    if (LuaUtil.isValid(UDView.this.mOnClick) && !onLongClickCallback) {
                        double x = 0, y = 0, mx = 0, my = 0;
                        if (event != null) {
                            x = DimenUtil.pxToDpi(event.getX());
                            y = DimenUtil.pxToDpi(event.getY());
                            mx = DimenUtil.pxToDpi(event.getRawX());
                            my = DimenUtil.pxToDpi(event.getRawY());
                        }
                        callOnClick(LuaValue.valueOf(x), LuaValue.valueOf(y), LuaValue.valueOf(mx), LuaValue.valueOf(my));
                    }
                }
            }
            return false;
        }
    };

    /**
     * get callback
     *
     * @return
     */

    public LuaValue getCallback() {
        return mCallback != null ? mCallback : LuaValue.NIL;
    }

    public LuaValue getOnClickCallback() {
        return this.mOnClick;
    }

    public LuaValue getOnLongClickCallback() {
        return this.mOnLongClick;
    }

    public LuaValue getOnTouchCallback() {
        return this.mOnTouch;
    }

    /**
     * 是否有焦点
     *
     * @return
     */
    public boolean hasFocus() {
        return getView() != null && getView().hasFocus();
    }

    /**
     * 请求焦点
     *
     * @return
     */
    public UDView requestFocus() {
        View view = getView();
        if (view != null) {
            view.requestFocus();
        }
        return this;
    }

    /**
     * 清空焦点
     *
     * @return
     */
    public UDView clearFocus() {
        View view = getView();
        if (view != null) {
            view.clearFocus();
        }
        return this;
    }

    /**
     * bring a view to front
     *
     * @return
     */
    public UDView bringToFront() {
        View view = getView();
        if (view != null) {
            view.bringToFront();
        }
        return this;
    }

    /**
     * 滚动到某个位置
     *
     * @param x
     * @param y
     * @return
     */
    public UDView scrollTo(int x, int y) {
        View view = getView();
        if (view != null) {
            view.scrollTo(x, y);
        }
        return this;
    }

    /**
     * 滚动dx, dy
     *
     * @param dx
     * @param dy
     * @return
     */
    public UDView scrollBy(int dx, int dy) {
        View view = getView();
        if (view != null) {
            view.scrollBy(dx, dy);
        }
        return this;
    }

    /**
     * 获取滚动x
     *
     * @return
     */
    public int getScrollX() {
        return getView() != null ? getView().getScrollX() : 0;
    }

    /**
     * 获取滚动y
     *
     * @return
     */
    public int getScrollY() {
        return getView() != null ? getView().getScrollY() : 0;
    }

    /**
     * 设置横向滚动条
     *
     * @param enabled
     * @return
     */
    public UDView setHorizontalScrollBarEnabled(boolean enabled) {
        View view = getView();
        if (view != null) {
            view.setHorizontalScrollBarEnabled(enabled);
        }
        return this;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return getView() != null && getView().isHorizontalScrollBarEnabled();
    }

    /**
     * 设置纵向滚动条
     *
     * @param enabled
     * @return
     */
    public UDView setVerticalScrollBarEnabled(boolean enabled) {
        View view = getView();
        if (view != null) {
            view.setVerticalScrollBarEnabled(enabled);
        }
        return this;
    }

    public boolean isVerticalScrollBarEnabled() {
        return getView() != null && getView().isVerticalScrollBarEnabled();
    }

    /**
     * 调整大小以适应内容
     *
     * @return
     */
    public UDView adjustSize() {
        //TODO 由子类实现
        return this;
    }


    //-------------------------------------------动画相关--------------------------------------------

    /**
     * 开始动画
     * TODO 这里的start／stop需要跟 UDAnimator的保持同步
     *
     * @param animators
     * @return
     */
    public UDView startAnimation(LuaValue[] animators) {
        View view = getView();
        if (view != null && animators.length > 0) {
            if (!isAnimating()) {//不在动画中才可以动画
                if (mAnimators != null) {
                    AnimatorUtil.cancel(mAnimators);
                    mAnimators.clear();
                } else {
                    mAnimators = new ArrayList<>();
                }

                for (LuaValue animator : animators) {
                    if (animator instanceof UDAnimator) {
                        Animator anim = ((UDAnimator) animator).with(this).build();
                        mAnimators.add(anim);
                    }
                }

                startAnimation();
            }
        }
        return this;
    }

    /**
     * 开始动画
     *
     * @return
     */
    public UDView startAnimation() {
        AnimatorUtil.start(mAnimators);
        return this;
    }

    /**
     * 停止动画，回到终点位置
     * 返回是否结束成功
     *
     * @return
     */
    public boolean cancelAnimation() {
        return AnimatorUtil.cancel(mAnimators);
    }

    /**
     * pause animation
     *
     * @return
     */
    public UDView pauseAnimation() {
        AnimatorUtil.pause(mAnimators);
        return this;
    }

    /**
     * resume animation
     *
     * @return
     */
    public UDView resumeAnimation() {
        AnimatorUtil.resume(mAnimators);
        return this;
    }


    /**
     * end and call function
     *
     * @return
     */
    public boolean endAnimation() {
        return AnimatorUtil.end(mAnimators);
    }

    /**
     * 是否暂停
     *
     * @return
     */
    public boolean isAnimationPaused() {
        return AnimatorUtil.isPaused(mAnimators);
    }

    /**
     * 是否在动画
     *
     * @return
     */
    public boolean isAnimating() {
        return AnimatorUtil.isRunning(mAnimators);
    }


    /**
     * 设置View的effects
     * 无特效 <= -1
     * 点击特效 1
     *
     * @param effects
     * @return
     */
    public UDView setEffects(int effects) {
        return setEffects(effects, null);
    }

    @LuaViewApi(since = VmVersion.V_540, revisions = "新增 UDViewEffect.EFFECT_PARALLAX")
    public UDView setEffects(Varargs varargs) {
        setEffects(LuaUtil.getInt(varargs, 2), varargs);
        return this;
    }

    UDView setEffects(Integer effects, Varargs varargs) {
        View view = getView();
        if (view != null) {
            this.mEffects = effects;
            if (effects != null) {
                switch (effects) {
                    case UDViewEffect.EFFECT_NONE:
                        ForegroundDelegate.clearForeground(view);
                        break;
                    case UDViewEffect.EFFECT_CLICK:
                        ForegroundDelegate.setupDefaultForeground(view,
                                ColorUtil.parse(LuaUtil.getValue(varargs, 3)),
                                LuaUtil.getAlphaInt(LuaUtil.getValue(varargs, 4)));
                        break;
                    case UDViewEffect.EFFECT_PARALLAX:
                        ((LVImageView) view).setMotionDistanceXY(DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 0f, 3)),
                                DimenUtil.dpiToPx(LuaUtil.getFloat(varargs, 0f, 4)));
                        break;
                }
            }
        }
        return this;
    }

    public Integer getEffects() {
        return this.mEffects != null ? this.mEffects : UDViewEffect.EFFECT_NONE;
    }


}