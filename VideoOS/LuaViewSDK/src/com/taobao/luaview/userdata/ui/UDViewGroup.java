/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.ui;

import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.global.LuaViewManager;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

/**
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class UDViewGroup<T extends ViewGroup> extends UDView<T> {
    LuaValue mOnShow;
    LuaValue mOnHide;
    LuaValue mOnBack;
    LuaValue mOnLayout;
    LuaValue mOnHome;

    public UDViewGroup(T view, Globals globals, LuaValue initParams) {
        this(view, globals, LuaViewManager.createMetatable(UIViewGroupMethodMapper.class), initParams);
    }

    public UDViewGroup(T view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        init();
    }

    private void init() {
    }

    public ViewGroup getContainer() {
        return getView();
    }

    @Override
    public UDViewGroup setCallback(LuaValue callbacks) {
        super.setCallback(callbacks);
        if (this.mCallback != null) {
            mOnShow = LuaUtil.getFunction(mCallback, "onShow", "OnShow");
            mOnHide = LuaUtil.getFunction(mCallback, "onHide", "OnHide");
            mOnBack = LuaUtil.getFunction(mCallback, "onBack", "OnBack");
            mOnLayout = LuaUtil.getFunction(mCallback, "onLayout", "OnLayout");
            mOnHome = LuaUtil.getFunction(mCallback,"onHome","OnHome");
            //是否需要统一成OnShow, OnHide?
            if (mOnBack != null && getContainer() != null) {
                getContainer().setFocusableInTouchMode(true);
                getContainer().setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
        }
        return this;
    }

    public UDViewGroup setOnShowCallback(LuaValue callback) {
        mOnShow = callback;
        return this;
    }

    public LuaValue getOnShowCallback() {
        return this.mOnShow;
    }

    public UDViewGroup setOnHideCallback(LuaValue callback) {
        mOnHide = callback;
        return this;
    }

    public LuaValue getOnHideCallback() {
        return this.mOnHide;
    }

    public UDViewGroup setOnBackCallback(LuaValue callback) {
        mOnBack = callback;
        return this;
    }

    public LuaValue getOnHome() {
        return mOnHome;
    }

    public UDViewGroup setOnHome(LuaValue onHome) {
        this.mOnHome = onHome;
        return this;
    }

    public LuaValue getOnBackCallback() {
        return this.mOnBack;
    }

    public UDViewGroup setOnLayoutCallback(LuaValue callback) {
        mOnLayout = callback;
        return this;
    }

    public LuaValue getOnLayoutCallback() {
        return this.mOnLayout;
    }

    public LuaValue callOnShow() {
        return LuaUtil.callFunction(mOnShow);
    }

    public LuaValue callOnHide() {
        return LuaUtil.callFunction(mOnHide);
    }

    public LuaValue callOnHome() {
        return LuaUtil.callFunction(mOnHome);
    }

    public LuaValue callOnBack() {
        return LuaUtil.callFunction(mOnBack);
    }

    public LuaValue callOnLayout() {
        return LuaUtil.callFunction(mOnLayout);
    }

    /**
     * add a subview
     *
     * @return
     */
    public UDViewGroup addView(UDView udView, Varargs varargs) {
        final Integer pos = LuaUtil.getInt(varargs, 3);
        ViewGroup viewGroup = getContainer();
        if (viewGroup != null && udView != null && udView.getView() != null) {
            View view = udView.getView();
            LuaViewUtil.addView(viewGroup, view, pos != null ? pos : -1, null);
        }
        return this;
    }

    /**
     * remove a subview
     *
     * @param subView
     * @return
     */
    public UDViewGroup removeView(UDView subView) {
        ViewGroup viewGroup = getContainer();
        if (viewGroup != null && subView != null && subView.getView() != null) {
            View view = subView.getView();
            LuaViewUtil.removeView(viewGroup, view);//TODO 这里需要排查一下，是否直接移除没有问题？
        }
        return this;
    }

    /**
     * 移除所有view
     *
     * @return
     */
    public UDViewGroup removeAllViews() {
        LuaViewUtil.removeAllViews(getContainer());
        return this;
    }

    /**
     * 提供View的构造环境，注意，callback里面不能有异步操作，否则操作的时序上会混乱
     *
     * @param callback
     * @return
     */
    public UDViewGroup children(LuaFunction callback) {
        if (getView() instanceof ViewGroup) {
            Globals globals = getGlobals();
            if (globals != null) {
                globals.pushContainer(getView());
                LuaUtil.callFunction(callback, this);
                globals.popContainer();
            }
        }
        return this;
    }


    public UDViewGroup setChildNodeViews(ArrayList<UDView> childNodeViews) {
        ViewGroup viewGroup = getContainer();
        if (viewGroup != null && childNodeViews != null) {
            if (viewGroup instanceof ILVViewGroup) {
                ((ILVViewGroup) viewGroup).setChildNodeViews(childNodeViews);
            }
        }

        return this;
    }
}
