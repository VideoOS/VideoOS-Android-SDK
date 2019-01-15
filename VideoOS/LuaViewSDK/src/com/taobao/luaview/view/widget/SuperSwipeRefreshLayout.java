/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import cn.com.venvy.common.interf.VenvyAnimationListener;

/**
 * Created by tuoli on 12/5/16.
 */

public class SuperSwipeRefreshLayout extends ViewGroup {
    static  int HEADER_VIEW_HEIGHT = 50;// HeaderView height (dp)

    static  float DECELERATE_INTERPOLATION_FACTOR = 2f;
    static  int INVALID_POINTER = -1;
    static  float DRAG_RATE = .5f;

    static  int ANIMATE_TO_TRIGGER_DURATION = 200;
    static  int ANIMATE_TO_START_DURATION = 200;
    static  int DEFAULT_CIRCLE_TARGET = 64;

    // SuperSwipeRefreshLayout内的目标View，因为包装了LVViewGroup,所以((ViewGroup)mTarget).getChildAt(0)拿到的才是真正的目标view, 比如RecyclerView,ListView,ScrollView,GridView
    // etc.
    View mTarget;

    OnPullRefreshListener mListener;// 下拉刷新listener

    boolean mRefreshing = false;
    int mTouchSlop;
    float mTotalDragDistance = -1;
    int mMediumAnimationDuration;
    int mCurrentTargetOffsetTop;
    boolean mOriginalOffsetCalculated = false;

    float mInitialMotionY;
    boolean mIsBeingDragged;
    int mActivePointerId = INVALID_POINTER;

    boolean mReturningToStart;
     DecelerateInterpolator mDecelerateInterpolator;
    static  int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};

    HeadViewContainer mHeadViewContainer;

    protected int mFrom;

    protected int mOriginalOffsetTop;

    Animation mScaleAnimation;

    // 最后停顿时的偏移量px，与DEFAULT_CIRCLE_TARGET正比
    float mSpinnerFinalOffset;

    boolean mNotify;

    int mHeaderViewWidth;// headerView的宽度

    int mHeaderViewHeight;

    boolean targetScrollWithLayout = true;

    int pushDistance = 0;

    boolean usingDefaultHeader = true;

    float density = 1.0f;


    /**
     * 下拉时，超过距离之后，弹回来的动画监听器
     */
    AnimationListener mRefreshListener = new VenvyAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            }
            mCurrentTargetOffsetTop = mHeadViewContainer.getTop();
            updateListenerCallBack();
        }
    };

    /**
     * 更新回调
     */
    void updateListenerCallBack() {
        int distance = mCurrentTargetOffsetTop + mHeadViewContainer.getHeight();
        if (mListener != null) {
            mListener.onPullDistance(mNotify, distance);
        }
    }

    public SuperSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
         */
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(
                DECELERATE_INTERPOLATION_FACTOR);

         TypedArray a = context
                .obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
         DisplayMetrics metrics = getResources().getDisplayMetrics();
        mHeaderViewWidth = display.getWidth();
        mHeaderViewHeight = (int) (HEADER_VIEW_HEIGHT * metrics.density);
        createHeaderViewContainer();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        density = metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
    }

    public void setRefreshingOffset(float offset) {
        mSpinnerFinalOffset = offset * density * 2;
    }

    /**
     * 创建头布局的容器
     */
    void createHeaderViewContainer() {
        mHeadViewContainer = new HeadViewContainer(getContext());
        addView(mHeadViewContainer);
    }

    /**
     * 设置
     *
     * @param listener
     */
    public void setOnRefreshListener(OnPullRefreshListener listener) {
        mListener = listener;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop, true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    void startScaleUpAnimation(AnimationListener listener) {
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime,
                                            Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mScaleAnimation);
    }

    void setAnimationProgress(float progress) {
        if (!usingDefaultHeader) {
            progress = 1;
        }
        ViewCompat.setScaleX(mHeadViewContainer, progress);
        ViewCompat.setScaleY(mHeadViewContainer, progress);
    }

    void setRefreshing(boolean refreshing,  boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                animateOffsetToStartPosition(mCurrentTargetOffsetTop, mRefreshListener);
            }
        }
    }

    /**
     * 确保mTarget不为空<br>
     * mTarget一般是可滑动的ScrollView,ListView,RecyclerView等
     */
    void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeadViewContainer)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
         int width = getMeasuredWidth();
         int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        int distance = mCurrentTargetOffsetTop + mHeadViewContainer.getMeasuredHeight();
        if (!targetScrollWithLayout) {
            // 判断标志位，如果目标View不跟随手指的滑动而滑动，将下拉偏移量设置为0
            distance = 0;
        }
         View child = mTarget;
         int childLeft = getPaddingLeft();
         int childTop = getPaddingTop() + distance - pushDistance;// 根据偏移量distance更新
         int childWidth = width - getPaddingLeft() - getPaddingRight();
         int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop
                + childHeight);// 更新目标View的位置
        int headViewWidth = mHeadViewContainer.getMeasuredWidth();
        int headViewHeight = mHeadViewContainer.getMeasuredHeight();
        mHeadViewContainer.layout((width / 2 - headViewWidth / 2),
                mCurrentTargetOffsetTop, (width / 2 + headViewWidth / 2),
                mCurrentTargetOffsetTop + headViewHeight);// 更新头布局的位置
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth()
                        - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight()
                                - getPaddingTop() - getPaddingBottom(),
                        MeasureSpec.EXACTLY));
        mHeadViewContainer.measure(MeasureSpec.makeMeasureSpec(
                mHeaderViewWidth, MeasureSpec.EXACTLY), MeasureSpec
                .makeMeasureSpec(3 * mHeaderViewHeight, MeasureSpec.EXACTLY));
        if (!mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mHeadViewContainer
                    .getMeasuredHeight();
            updateListenerCallBack();
        }
    }

    /**
     * 判断目标View是否滑动到顶部-还能否继续滑动
     *
     * @return
     */
    public boolean isChildScrollToTop() {
        if (Build.VERSION.SDK_INT < 14) {
            if (((ViewGroup) mTarget).getChildAt(0) instanceof AbsListView) {
                 AbsListView absListView = (AbsListView) ((ViewGroup) mTarget).getChildAt(0);
                return !(absListView.getChildCount() > 0 && (absListView
                        .getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop()));
            } else {
                return !(((ViewGroup) mTarget).getChildAt(0).getScrollY() > 0);
            }
        } else {
            return !ViewCompat.canScrollVertically(((ViewGroup) mTarget).getChildAt(0), -1);
        }
    }

    /**
     * 主要判断是否应该拦截子View的事件<br>
     * 如果拦截，则交给自己的OnTouchEvent处理<br>
     * 否者，交给子View处理<br>
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

         int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || mRefreshing || !isChildScrollToTop()) {
            // 如果子View可以滑动，不拦截事件，交给子View处理-下拉刷新
            // 或者子View没有滑动到底部不拦截事件-上拉加载更多
            return false;
        }

        // 下拉刷新判断
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(
                        mOriginalOffsetTop - mHeadViewContainer.getTop(), true);// 恢复HeaderView的初始位置
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                 float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;// 记录按下的位置

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                 float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                float yDiff = y - mInitialMotionY;// 计算下拉距离
                if (yDiff > mTouchSlop && !mIsBeingDragged) {// 判断是否下拉的距离足够
                    mIsBeingDragged = true;// 正在下拉
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;// 如果正在拖动，则拦截子View的事件
    }

    float getMotionEventY(MotionEvent ev, int activePointerId) {
         int index = MotionEventCompat.findPointerIndex(ev,
                activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
         int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        if (!isEnabled() || mReturningToStart || !isChildScrollToTop()) {
            // 如果子View可以滑动，不拦截事件，交给子View处理
            return false;
        }

        return handlerPullTouchEvent(ev, action);
    }

    boolean handlerPullTouchEvent(MotionEvent ev, int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                 int pointerIndex = MotionEventCompat.findPointerIndex(ev,
                        mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                 float y = MotionEventCompat.getY(ev, pointerIndex);
                 float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    float originalDragPercent = overscrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
                    if (overscrollTop < mTotalDragDistance) {
                        if (mListener != null) {
                            mListener.onPullEnable(false);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onPullEnable(true);
                        }
                    }
                    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                 int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                 int pointerIndex = MotionEventCompat.findPointerIndex(ev,
                        mActivePointerId);
                 float y = MotionEventCompat.getY(ev, pointerIndex);
                 float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true /* notify */);
                } else {
                    mRefreshing = false;
                    AnimationListener listener = null;
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mAnimateToCorrectPosition);
    }

    void animateOffsetToStartPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mHeadViewContainer.setAnimationListener(listener);
        }
        mHeadViewContainer.clearAnimation();
        mHeadViewContainer.startAnimation(mAnimateToStartPosition);
        resetTargetLayoutDelay(ANIMATE_TO_START_DURATION);
    }

    /**
     * 重置Target位置
     *
     * @param delay
     */
    public void resetTargetLayoutDelay(int delay) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                resetTargetLayout();
            }
        }, delay);
    }

    /**
     * 重置Target的位置
     */
    public void resetTargetLayout() {
         int width = getMeasuredWidth();
         View child = mTarget;
         int childLeft = getPaddingLeft();
         int childTop = getPaddingTop();
         int childWidth = child.getWidth() - getPaddingLeft()
                - getPaddingRight();
         int childHeight = child.getHeight() - getPaddingTop()
                - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop
                + childHeight);

        int headViewWidth = mHeadViewContainer.getMeasuredWidth();
        int headViewHeight = mHeadViewContainer.getMeasuredHeight();
        mHeadViewContainer.layout((width / 2 - headViewWidth / 2),
                -headViewHeight, (width / 2 + headViewWidth / 2), 0);// 更新头布局的位置
    }

     Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mHeadViewContainer.getTop() - (int) mSpinnerFinalOffset / 2;
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
        }
    };

    void moveToStart(float interpolatedTime) {
        int targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mHeadViewContainer.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }

     Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mHeadViewContainer.bringToFront();
        mHeadViewContainer.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mHeadViewContainer.getTop();
        if (requiresUpdate && Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
        updateListenerCallBack();
    }

    void onSecondaryPointerUp(MotionEvent ev) {
         int pointerIndex = MotionEventCompat.getActionIndex(ev);
         int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
             int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev,
                    newPointerIndex);
        }
    }

    class HeadViewContainer extends RelativeLayout {

        AnimationListener mAnimationListener;

        public HeadViewContainer(Context context) {
            super(context);
        }

        public void setAnimationListener(AnimationListener listener) {
            mAnimationListener = listener;
        }

        @Override
        public void onAnimationStart() {
            super.onAnimationStart();
            if (mListener != null && mAnimationListener != null) {
                mAnimationListener.onAnimationStart(getAnimation());
            }
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            if (mListener != null && mAnimationListener != null) {
                mAnimationListener.onAnimationEnd(getAnimation());
            }
        }
    }

    public interface OnPullRefreshListener {
        void onRefresh();

        void onPullDistance(boolean notify, int distance);

        void onPullEnable(boolean enable);
    }
}