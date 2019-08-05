package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDApplet;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Created by Lucas on 2019/8/2.
 */
public class AppletBinder extends BaseFunctionBinder {
    public AppletBinder() {
        super("Applet");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new UDApplet(env.checkglobals(), metaTable);
    }
}
