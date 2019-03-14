/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;


import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDCustomPanel;

import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * 自定义面板
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标", "增加调用Lua方法"})
public class UICustomPanelMethodMapper<U extends UDCustomPanel> extends UIViewGroupMethodMapper<U> {

    static String TAG = "UICustomPanelMethodMapper";
    static String[] sMethods = new String[]{
            "nativeView",//0
            "getNativeView"//1
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        switch (code - super.getAllFunctionNames().size()) {
            case 0:
                return target.getNativeView();
            case 1:
                return target.getNativeView();
        }
        return super.invoke(code, target, varargs);
    }

}