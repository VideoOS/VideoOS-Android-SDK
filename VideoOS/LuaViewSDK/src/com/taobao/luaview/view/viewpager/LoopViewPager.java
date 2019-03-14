

package com.taobao.luaview.view.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.taobao.luaview.view.adapter.LVLoopPagerAdapter;

public class LoopViewPager extends ViewPager {
    OnPageChangeListener mOuterPageChangeListener;
    LVLoopPagerAdapter mAdapter;

    float mPreviousOffset = -1;
    float mPreviousRealPosition = -1;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = (LVLoopPagerAdapter) adapter;
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public int getRealCount() {
        return mAdapter != null ? mAdapter.getRealCount() : 0;
    }

    public int getCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

    public void setLooping(boolean looping) {
        if (mAdapter != null && mAdapter.isLooping() != looping) {
             int currentPosition = super.getCurrentItem();
            super.setAdapter(null);
            mAdapter.setLooping(looping);
            super.setAdapter(mAdapter);
            if (looping) {
                super.setCurrentItem(mAdapter.toFakePosition(currentPosition), false);
            }
            mPreviousRealPosition = -1;
            mPreviousOffset = -1;
        }
    }

    public boolean isLooping() {
        return mAdapter != null && mAdapter.isLooping();
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : super.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public void setCurrentItem(int realItem, boolean smoothScroll) {
         int fakeItem = mAdapter.toFakePosition(realItem);
         int currentFakeItem = super.getCurrentItem();
        if (fakeItem != currentFakeItem) {
            super.setCurrentItem(fakeItem, smoothScroll);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOuterPageChangeListener = listener;
    }

    boolean isBoundaryPosition(int position) {
        return position == 0 || (position == getCount() - 1);
    }

    void init() {
        super.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                LogUtil.d("yesong", "onPageSelected", position);
                if (mAdapter != null && mAdapter.shouldLooping()) {
                    int realPosition = mAdapter.toRealPosition(position);
                    if (mPreviousRealPosition != realPosition) {
                        mPreviousRealPosition = realPosition;
                        if (mOuterPageChangeListener != null) {
                            mOuterPageChangeListener.onPageSelected(realPosition);
                        }
                    }
                } else {
                    mPreviousRealPosition = position;
                    if (mOuterPageChangeListener != null) {
                        mOuterPageChangeListener.onPageSelected(position);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtil.d("yesong", "onPageScrolled－offset", position, positionOffset, positionOffsetPixels);

                if (mAdapter != null && mAdapter.shouldLooping()) {
                     int realPosition = mAdapter.toRealPosition(position);
                    if (positionOffset == 0 && mPreviousOffset == 0 && isBoundaryPosition(position)) {
//                        LogUtil.d("yesong", "onPageScrolled", fakePosition, realPosition);
                        setCurrentItem(realPosition, false);
                    }
                    mPreviousOffset = positionOffset;
                    if (mOuterPageChangeListener != null) {
                        if (realPosition == mAdapter.getRealCount() - 1) {
                            if (positionOffset > 0.5) {
                                mOuterPageChangeListener.onPageScrolled(0, 0, 0);
                            } else {
                                mOuterPageChangeListener.onPageScrolled(realPosition, 0, 0);
                            }
                        } else {
                            mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                        }
                    }
                } else {
                    if (mOuterPageChangeListener != null) {
                        mOuterPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                LogUtil.d("yesong", "onPageScrollStateChanged", state);
                if (mAdapter != null && mAdapter.shouldLooping()) {
                    int fakePosition = LoopViewPager.super.getCurrentItem();
                    int realPosition = mAdapter.toRealPosition(fakePosition);

                    if (state == ViewPager.SCROLL_STATE_IDLE && isBoundaryPosition(fakePosition)) {
//                        LogUtil.d("yesong", "onPageScrollStateChanged", fakePosition, realPosition);
                        setCurrentItem(realPosition, false);
                    }
                }
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

}