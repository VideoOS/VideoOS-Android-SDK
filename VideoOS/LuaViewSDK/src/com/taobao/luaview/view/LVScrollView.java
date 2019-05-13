/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.taobao.luaview.userdata.ui.UDScrollView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * LuaView - ScrollView
 *
 * @author song
 * @date 15/8/20
 */
public class LVScrollView extends ScrollView implements ILVViewGroup {
    UDScrollView mLuaUserdata;
    //root view
    ViewGroup mContainer;
    LinearLayout mainLayout;
    int mTargetHeight;

    public static final int SCROLL_TYPE_DOWN = 0;  //从上往下滑动
    public static final int SCROLL_TYPE_UP = 1;   // 从下往上滑动
    private int mCurrentType = SCROLL_TYPE_UP;

    public LVScrollView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDScrollView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(globals);
    }

    void init(Globals globals) {
        this.setVerticalScrollBarEnabled(false);//不显示滚动条
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        mainLayout = new LinearLayout(globals.getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mContainer = initTargetViewGroup(mCurrentType);
        mainLayout.addView(mContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        super.addView(mainLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setOrientation(int model) {
        if (model == mCurrentType) {
            return;
        }
        if (mainLayout == null) {
            return;
        }
        if (mContainer != null) {
            mainLayout.removeView(mContainer);
        }
        mContainer = initTargetViewGroup(model);
        mainLayout.addView(mContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mCurrentType = model;
    }

    private ViewGroup initTargetViewGroup(int type) {
        if (type == SCROLL_TYPE_DOWN) {
            LinearLayout target = new LinearLayout(getContext());
            target.setOrientation(LinearLayout.VERTICAL);
            return target;
        } else {
            return new LVUpLayout(getContext());
        }
    }

    public int getOrientation() {
        return mCurrentType;
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
    }

    public ViewGroup getContainer() {
        return mContainer;
    }

    private boolean hasCallScrollBegin = false;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (l != oldl || t != oldt) {
            if (!hasCallScrollBegin) {
                mLuaUserdata.callOnScrollBegin();
                hasCallScrollBegin = true;
            }
            mLuaUserdata.callOnScrolling(l, t, oldl, oldt);
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        boolean value = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        //注意deltaX的值和isTouchEvent的值，deltaX可以认为是X轴变化的速度，isTouchEvent意思是移动事件是否来自手势。经过多次确认，当deltaX的值为1，2或-1，-2，以及isTouchEvent值为false的时候，scrollView的移动趋于停止
        if (deltaX <= 2 && deltaX >= -2 && !isTouchEvent) {
            //scrollView停止移动了
            VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                @Override
                public void run() {
                    mLuaUserdata.callOnScrollEnd();
                    hasCallScrollBegin = false;
                }
            }, 500);
        }
        return value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTargetHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public class LVUpLayout extends ViewGroup {
        int hadUsedVertical = 0;//垂直已经使用的距离
        int mTotalHeight = 0;


        public LVUpLayout(Context context) {
            super(context);
        }

        public LVUpLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LVUpLayout(Context context, AttributeSet attributeSet, int style) {
            super(context, attributeSet, style);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            // 宽度模式
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            // 测量宽度
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            // 高度模式
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            // 测量高度
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            // 子view数目
            int childCount = getChildCount();
            if (childCount == 0) {
                // 如果当前ViewGroup没有子View，就没有存在的意义，无需占空间
                setMeasuredDimension(0, 0);
            } else {
                // 如果宽高都是包裹内容
                if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                    // 宽度为所有子view宽度相加，高度取子view最大高度
                    int width = getMaxWidth();
                    int height = getTotalHeight();
                    setMeasuredDimension(width, height);
                    mTotalHeight = height;
                } else if (widthMode == MeasureSpec.AT_MOST) {
                    // 宽度为所有子View宽度相加，高度为测量高度
                    setMeasuredDimension(getMaxWidth(), heightSize);
                    mTotalHeight = heightSize;
                } else if (heightMode == MeasureSpec.AT_MOST) {
                    // 宽度为测量宽度，高度为子view最大高度
                    mTotalHeight = getTotalHeight();
                    setMeasuredDimension(widthSize, mTotalHeight);
                } else {
                    mTotalHeight = Math.max(getTotalHeight(), mTargetHeight);
                    setMeasuredDimension(getMaxWidth(), mTotalHeight);
                }
            }
        }

        /**
         * 获取子view最大高度
         */
        private int getMaxWidth() {
            // 最大高度
            int maxWidth = 0;
            // 子view数目
            int childCount = getChildCount();
            // 遍历子view拿取最大高度
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                if (childView.getMeasuredHeight() > maxWidth)
                    maxWidth = childView.getMeasuredWidth();
            }
            return maxWidth;
        }

        private int getTotalHeight() {
            // 所有子view宽度之和
            int totalHeight = 0;
            // 子View数目
            int childCount = getChildCount();
            // 遍历所有子view拿取所有子view宽度之和
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                totalHeight += childView.getMeasuredHeight();
            }
            return totalHeight;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int childCount = this.getChildCount();
            hadUsedVertical = t;//垂直已经使用的距离
            int height = getMeasuredHeight();
            for (int i = childCount - 1; i >= 0; i--) {
                View childView = getChildAt(i);
                int childHeight = childView.getMeasuredHeight();
                int childWidth = childView.getMeasuredWidth();
                childView.layout(l,0, l + childWidth, height - hadUsedVertical);
                hadUsedVertical += childHeight;
            }
        }
    }

}
