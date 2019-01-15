/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.list;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.list.UDBaseListOrRecyclerView;
import com.taobao.luaview.userdata.list.UDBaseListView;
import com.taobao.luaview.userdata.ui.UDViewGroup;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

/**
 * ListView的方法映射
 *
 * @author song
 */
@LuaViewLib
@Deprecated
public abstract class UIBaseListViewMethodMapper<U extends UDViewGroup> extends UIBaseListOrRecyclerViewMethodMapper<U> {
    static String TAG = "UIBaseListViewMethodMapper";
    static String[] sMethods = new String[]{
            "header",//0
            "footer",//1
            "dividerHeight"//2
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        switch (code - super.getAllFunctionNames().size()) {
            case 0:
                return header(varargs);
            case 1:
                return footer(varargs);
            case 2:
                return dividerHeight(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------

    public abstract UDBaseListView getUDBaseListView(Varargs varargs);


    @Override
    public UDBaseListOrRecyclerView getUDBaseListOrRecyclerView(Varargs varargs) {
        return getUDBaseListView(varargs);
    }

    /**
     * 设置TableView的头
     *
     * @param varargs
     * @return
     */
    public LuaValue header(Varargs varargs) {
        return varargs.narg() > 1 ?
                getUDBaseListView(varargs).setHeader(varargs.arg(2)) :
                getUDBaseListView(varargs).getHeader();
    }


    /**
     * 设置TableView的尾
     *
     * @param varargs
     * @return
     */
    public LuaValue footer(Varargs varargs) {
        return varargs.narg() > 1 ?
                getUDBaseListView(varargs).setFooter(varargs.arg(2)) :
                getUDBaseListView(varargs).getFooter();
    }

    /**
     * 分隔线高度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue dividerHeight(U view, Varargs varargs) {
        return varargs.narg() > 1 ?
                setMiniSpacing(view, varargs) :
                getMiniSpacing(view, varargs);
    }
}