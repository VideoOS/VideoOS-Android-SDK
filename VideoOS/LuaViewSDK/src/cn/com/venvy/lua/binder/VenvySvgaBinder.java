package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.maper.VenvyUISvgaImageViewMapper;
import cn.com.venvy.lua.view.VenvyLVSvgeImageView;

/**
 * Created by mac on 18/3/28.
 */

public class VenvySvgaBinder extends BaseFunctionBinder {

    public VenvySvgaBinder() {
        super("SVGAView");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyUISvgaImageViewMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {

                return new VenvyLVSvgeImageView(globals, metaTable, varargs);
            }
        };
    }
}
