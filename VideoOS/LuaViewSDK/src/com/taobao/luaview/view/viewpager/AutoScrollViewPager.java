/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view.viewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

public class AutoScrollViewPager extends LoopViewPager {

    public static  int DEFAULT_INTERVAL = 3000;

    public static  int LEFT = 0;
    public static  int RIGHT = 1;


    /**
     * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     **/
    long interval = DEFAULT_INTERVAL;
    /**
     * auto scroll direction, default is {@link #RIGHT}
     **/
    int direction = RIGHT;
    /**
     * whether stop auto scroll when touching, default is true
     **/
    boolean stopScrollWhenTouch = true;
    /**
     **/

    Handler handler;
    boolean reverseDirection = false;
    boolean isAutoScroll = false;
    boolean isStopByUIChange = false;
    boolean isStopByTouch = false;

    boolean canAutoScroll = false;

    public static final int SCROLL_WHAT = 0;

    public AutoScrollViewPager(Context context) {
        super(context);
        handler = new MyHandler(this);
    }

    /**
     * 设置是否可以自动滚动，所有自动滚动的逻辑都受该参数影响
     *
     * @param canAutoScroll
     */
    public void setCanAutoScroll(boolean canAutoScroll) {
        this.canAutoScroll = canAutoScroll;
    }

    /**
     */
    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage(interval);
    }


    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }


    void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    /**
     * scroll only once
     */
    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int realPosition = getCurrentItem();
        int realCount;
        if (adapter == null || (realCount = getRealCount()) <= 1) {
            return;
        }

        //调整方向
        if (reverseDirection) {
            if (direction == RIGHT && realPosition + 1 >= realCount) {
                direction = LEFT;
            } else if (direction == LEFT && realPosition - 1 < 0) {
                direction = RIGHT;
            }
        }

        if (isLooping()) {
            setCurrentItem(direction == LEFT ? (realPosition - 1) % getCount() : (realPosition + 1) % getCount(), true);
        } else {
            int nextItem = (direction == LEFT) ? --realPosition : ++realPosition;
            if (nextItem < 0) {
                setCurrentItem(realCount - 1, true);
            } else if (nextItem == realCount) {
                setCurrentItem(0, true);
            } else {
                setCurrentItem(nextItem, true);
            }
        }
    }

    /**
     * <ul>
     * if stopScrollWhenTouch is true
     * <li>if event is down, stop auto scroll.</li>
     * <li>if event is up, start auto scroll again.</li>
     * </ul>
     * <p>
     * bugfix: 增加ev.getAction() == MotionEvent.ACTION_CANCEL条件判断,Action Cancel事件发生的时候也要重新开始自动滚动
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!canAutoScroll) {
            return super.dispatchTouchEvent(ev);
        }
        int action = MotionEventCompat.getActionMasked(ev);

        if (stopScrollWhenTouch) {
            if ((action == MotionEvent.ACTION_DOWN) && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) && isStopByTouch) {
                startAutoScroll();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    static class MyHandler extends Handler {

         WeakReference<AutoScrollViewPager> autoScrollViewPager;

        public MyHandler(AutoScrollViewPager autoScrollViewPager) {
            this.autoScrollViewPager = new WeakReference<>(autoScrollViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    AutoScrollViewPager pager = this.autoScrollViewPager.get();
                    if (pager != null) {
                        pager.scrollOnce();
                        pager.sendScrollMessage(pager.interval);
                    }
                default:
                    break;
            }
        }
    }


    /**
     * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }


    /**
     * set whether stop auto scroll when touching, default is true
     *
     * @param stopScrollWhenTouch
     */
    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }


    public void setReverseDirection(boolean reverseDirection) {
        this.reverseDirection = reverseDirection;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (canAutoScroll && visibility == View.VISIBLE && isStopByUIChange) {
            isStopByUIChange = false;
            startAutoScroll();
        }
        super.onVisibilityChanged(changedView, visibility);
        if (canAutoScroll && visibility != View.VISIBLE) {
            isStopByUIChange = true;
            stopAutoScroll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!canAutoScroll) {
            return;
        }
        isStopByUIChange = true;
        stopAutoScroll();
    }
}