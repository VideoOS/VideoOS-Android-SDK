/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.mapper.ui;

import com.taobao.luaview.fun.base.BaseMethodMapper;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * 属性赋值操作
 *
 * @author song
 */
public class NewIndexFunction extends BaseMethodMapper {

    LuaValue metatable;

    public NewIndexFunction(LuaValue metatable) {
        this.metatable = metatable;
    }

    @Override
    public Varargs invoke(Varargs args) {
        LuaValue func = metatable.get(args.arg(2));
        if (func.isfunction()) {//函数调用
            func.invoke(varargsOf(args.arg(1), args.arg(3)));
        }
        return NONE;
    }

}
