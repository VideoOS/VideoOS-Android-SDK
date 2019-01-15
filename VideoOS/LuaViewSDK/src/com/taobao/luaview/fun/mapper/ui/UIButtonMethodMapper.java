/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.ui.UDButton;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;


/**
 * Button 接口封装
 *
 * @param <U>
 * @author song
 */
@LuaViewLib(revisions = {"20170306已对标", "跟iOS不一致，iOS继承自View"})
public class UIButtonMethodMapper<U extends UDButton> extends UITextViewMethodMapper<U> {
    static String TAG = "UIButtonMethodMapper";
    static String[] sMethods = new String[]{
            "title",//0
            "titleColor",//1
            "image",//2
            "showsTouchWhenHighlighted"//3
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        switch (code - super.getAllFunctionNames().size()) {
            case 0:
                return text(target, varargs);
            case 1:
                return textColor(target, varargs);
            case 2:
                return image(target, varargs);
            case 3:
                return showsTouchWhenHighlighted(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    //--------------------------------------- API --------------------------------------------------


    /**
     * 设置图片
     *
     * @param view
     * @param varargs
     * @return
     */
    public Varargs image(U view, Varargs varargs) {
        return varargs.narg() > 1 ?
                view.setImage(varargs.optjstring(2, null), varargs.optjstring(3, null)) :
                view.getImage();
    }


    /**
     * 获取按钮点击是否高亮
     *
     * @param view
     * @param varargs
     * @return
     */
    @Deprecated
    public LuaValue showsTouchWhenHighlighted(U view, Varargs varargs) {
        return varargs.narg() > 1 ?
                view.setHighlightColor(0) :
                valueOf(view.getHighlightColor() == 0);
    }

}