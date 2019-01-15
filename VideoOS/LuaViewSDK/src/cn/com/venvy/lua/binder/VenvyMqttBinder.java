package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.maper.VenvyMqttMapper;
import cn.com.venvy.lua.view.VenvyLVMqttCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyMqttBinder extends BaseFunctionBinder {

    public VenvyMqttBinder() {
        super("Mqtt");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyMqttMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new VenvyLVMqttCallback(globals, metaTable, varargs);
            }
        };
    }
}
