package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDApplet;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.Platform;

/**
 * Created by Lucas on 2019/8/2.
 */
public class VenvyAppletBinder extends BaseFunctionBinder {
    private Platform mPlatform;

    public VenvyAppletBinder() {
        super("Applet");
    }

    public void setPlatform(Platform platform) {
        this.mPlatform = platform;
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new UDApplet(env.checkglobals(), metaTable, mPlatform);
    }
}
