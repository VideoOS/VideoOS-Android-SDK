package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.lua.maper.VenvyAcrCloudMapper;
import cn.com.venvy.lua.view.VenvyLVAcrClouldCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyAcrCloudBinder extends BaseFunctionBinder {
    private Platform mPlatform;

    public VenvyAcrCloudBinder() {
        super("AcrCloud");
    }

    public void setPlatform(Platform platform) {
        this.mPlatform = platform;
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyAcrCloudMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new VenvyLVAcrClouldCallback(mPlatform,globals, metaTable, varargs);
            }
        };
    }
}
