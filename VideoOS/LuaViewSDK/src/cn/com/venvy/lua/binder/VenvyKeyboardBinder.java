package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.maper.VenvyKeyboardMapper;
import cn.com.venvy.lua.view.VenvyLVKeyboardCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyKeyboardBinder extends BaseFunctionBinder {

    public VenvyKeyboardBinder() {
        super("Keyboard");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyKeyboardMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new VenvyLVKeyboardCallback(globals, metaTable, varargs);
            }
        };
    }
}
