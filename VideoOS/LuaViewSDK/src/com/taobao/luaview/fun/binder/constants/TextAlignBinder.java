/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDTextAlign;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

public class TextAlignBinder extends BaseFunctionBinder {

    public TextAlignBinder() {
        super("TextAlign");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env,  LuaValue metaTable) {
        return new UDTextAlign(env.checkglobals(), metaTable);
    }
}
