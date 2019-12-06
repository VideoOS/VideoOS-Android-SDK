/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.taobao.luaview.userdata.ui.UDHorizontalScrollView;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * LuaView - HorizontalScrollView
 *
 * @author song
 * @date 15/8/20
 */
public class LVHorizontalScrollView extends HorizontalScrollView implements ILVViewGroup {
    UDHorizontalScrollView mLuaUserdata;
    private boolean hasCallScrollBegin = false;

    //root view
    LinearLayout mContainer;

    public LVHorizontalScrollView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDHorizontalScrollView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(globals);
    }

    void init(Globals globals) {
        this.setHorizontalScrollBarEnabled(false);//不显示滚动条
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        mContainer = new LinearLayout(globals.getContext());
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        super.addView(mContainer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mLuaUserdata.callOnScrollEnd();
            hasCallScrollBegin = false;
        }
        return super.onTouchEvent(ev);
    }
}
