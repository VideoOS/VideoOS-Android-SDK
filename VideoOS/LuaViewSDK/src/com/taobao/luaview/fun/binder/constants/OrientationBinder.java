package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDOrientation;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Orientation 常量
 *
 * @author song
 * @date 15/8/21
 */
public class OrientationBinder extends BaseFunctionBinder {
    public OrientationBinder() {
        super("Orientation");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new UDOrientation(env.checkglobals(), metaTable);
    }
}
