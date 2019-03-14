/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.view.View;

import com.taobao.luaview.userdata.ui.UDRefreshLayout;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;
import com.taobao.luaview.view.widget.SuperSwipeRefreshLayout;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * Created by tuoli on 12/20/16.
 */

public class LVRefreshLayout extends SuperSwipeRefreshLayout implements ILVViewGroup {
    UDRefreshLayout mLuaUserdata;

    //root view
    LVViewGroup mContainer;

    public LVRefreshLayout(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new UDRefreshLayout(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(globals);
    }

    private void init(Globals globals) {
        mContainer = new LVViewGroup(globals, mLuaUserdata.getmetatable(), null);
        super.addView(mContainer, LuaViewUtil.createRelativeLayoutParamsMM());
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void addView(View view, LayoutParams params) {
        if (mContainer != view) {
            mContainer.addView(view, params);
        }
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
    }

    public LVViewGroup getContainer() {
        return mContainer;
    }
}
