/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.indicator;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.userdata.indicator.UDCircleViewPagerIndicator;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;


/**
 * method mapper for PagerIndicator
 *
 * @param <U>
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UICircleViewPagerIndicatorMethodMapper<U extends UDCircleViewPagerIndicator> extends UIViewMethodMapper<U> {
    static String TAG = "UICircleViewPagerIndicatorMethodMapper";
    static String[] sMethods = new String[]{
            "unselectedColor",//0
            "selectedColor",//1
            "fillColor",//2
            "pageColor",//3
            "strokeWidth",//4
            "strokeColor",//5
            "radius",//6
            "snap",//7
            "currentPage",//8
            "currentItem"//9
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        switch (code - super.getAllFunctionNames().size()) {
            case 0:
                return pageColor(target, varargs);
            case 1:
                return fillColor(target, varargs);
            case 2:
                return fillColor(target, varargs);
            case 3:
                return pageColor(target, varargs);
            case 4:
                return strokeWidth(target, varargs);
            case 5:
                return strokeColor(target, varargs);
            case 6:
                return radius(target, varargs);
            case 7:
                return snap(target, varargs);
            case 8:
                return currentItem(target, varargs);
            case 9:
                return currentItem(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 设置未选中颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue fillColor(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setFillColor(ColorUtil.parse(LuaUtil.getInt(varargs, 2))) :
                valueOf(ColorUtil.getHexColor(view.getFillColor()));
    }


    /**
     * 设置颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue pageColor(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setPageColor(ColorUtil.parse(LuaUtil.getInt(varargs, 2))) :
                view.setPageColor(ColorUtil.parse(LuaUtil.getInt(varargs, 2)));
    }


    /**
     * 设置线条宽度
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue strokeWidth(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setStrokeWidth(DimenUtil.dpiToPx(varargs.arg(2))) :
                valueOf(DimenUtil.pxToDpi(view.getStrokeWidth()));
    }


    /**
     * 设置线条颜色
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue strokeColor(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setStrokeColor(ColorUtil.parse(LuaUtil.getInt(varargs, 2))) :
                valueOf(ColorUtil.getHexColor(view.getStrokeColor()));
    }


    /**
     * 设置半径
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue radius(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setRadius(DimenUtil.dpiToPx(varargs.arg(2)))
                : valueOf(DimenUtil.pxToDpi(view.getRadius()));
    }


    /**
     * 设置是否移动瞬间过去
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue snap(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setSnap(varargs.optboolean(2, false)) : valueOf(view.isSnap());
    }


    /**
     * 设置当前第几页
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue currentItem(U view, Varargs varargs) {
        return varargs.narg() > 1 ? view.setCurrentItem(varargs.optint(2, -1)) : view;
    }


}