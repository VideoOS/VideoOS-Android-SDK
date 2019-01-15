/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.base;


import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * 零参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseVarArgCreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;
    public Class<? extends LibFunction> libClass;


    public BaseVarArgCreator(Globals globals, LuaValue metatable, Class<? extends LibFunction> libClass) {
        this.globals = globals;
        this.metatable = metatable;
        this.libClass = libClass;
    }

    public Varargs invoke(Varargs args) {
        if (LuaViewConfig.isLibsLazyLoad()) {
            metatable = LuaViewManager.createMetatable(libClass);
        }
        return createUserdata(globals, metatable, args);
    }

    public abstract LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs);
}
