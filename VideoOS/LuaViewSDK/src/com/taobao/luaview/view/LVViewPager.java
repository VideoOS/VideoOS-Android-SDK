/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.support.v4.view.ViewPager;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewPager;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.adapter.LVLoopPagerAdapter;
import com.taobao.luaview.view.indicator.circle.PageIndicator;
import com.taobao.luaview.view.interfaces.ILVViewGroup;
import com.taobao.luaview.view.viewpager.AutoScrollViewPager;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView-ViewPager
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVViewPager extends AutoScrollViewPager implements ILVViewGroup {
    public UDViewPager mLuaUserdata;
    OnPageChangeListener mOnPageChangeListener;

    public LVViewPager(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = createUserdata(globals, metaTable, varargs);
        init(globals);
    }

    private UDViewPager createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
        return new UDViewPager(this, globals, metaTable, varargs);
    }

    private void init(Globals globals) {
        LuaViewUtil.setId(this);//TODO 必须设置，且每个ViewPager要有唯一id android.content.res.Resources$NotFoundException: Unable to find resource ID #0xffffffff
        globals.saveContainer(this);
        initData(globals);
        globals.restoreContainer();
    }

    void initData(Globals globals) {
        this.setAdapter(new LVLoopPagerAdapter(globals, mLuaUserdata));
        this.setCurrentItem(0);//TODO 可以定制
        initOnPageChangeListener();//初始化页面监听
    }

    public void initOnPageChangeListener() {
        mOnPageChangeListener = createOnPageChangeListener(this);
        this.setOnPageChangeListener(mOnPageChangeListener);
    }

    public void setViewPagerIndicator(LuaValue indicator) {
        if (indicator instanceof UDView && ((UDView) indicator).getView() instanceof PageIndicator) {
            final PageIndicator pageIndicator = (PageIndicator) ((UDView) indicator).getView();
            pageIndicator.setViewPager(this);
            pageIndicator.setOnPageChangeListener(mOnPageChangeListener);
        }
    }


    /**
     * 创建页面移动listener
     *
     * @return
     */
    OnPageChangeListener createOnPageChangeListener(final ViewPager viewPager) {
        if (this.mLuaUserdata.hasPageChangeListeners()) {
            return new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    mLuaUserdata.callPageCallbackScrolling(position, positionOffset, DimenUtil.pxToDpi(positionOffsetPixels));
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    switch (state) {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            mLuaUserdata.callPageCallbackScrollBegin(viewPager != null ? viewPager.getCurrentItem() : 0);
                            break;
                        case ViewPager.SCROLL_STATE_IDLE:
                            mLuaUserdata.callPageCallbackScrollEnd(viewPager != null ? viewPager.getCurrentItem() : 0);
                            break;
                        case ViewPager.SCROLL_STATE_SETTLING:
                            break;
                    }
                    mLuaUserdata.callPageCallbackStateChanged(state);
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {

    }

}
