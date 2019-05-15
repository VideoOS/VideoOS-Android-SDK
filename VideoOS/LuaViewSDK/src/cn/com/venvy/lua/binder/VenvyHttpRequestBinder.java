package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.bridge.LVHttpBridge;
import cn.com.venvy.lua.maper.VenvyHttpRequestMapper;
import cn.com.venvy.lua.view.VenvyLVHttpRequestCallback;

/**
 * Created by videojj_pls on 2019/5/7.
 */
public class VenvyHttpRequestBinder extends BaseFunctionBinder {
    private LVHttpBridge httpBridge;

    public VenvyHttpRequestBinder() {
        super("HttpRequest");
    }

    public void setLVHttpBridge(LVHttpBridge lvHttpBridge) {
        this.httpBridge = lvHttpBridge;
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyHttpRequestMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new VenvyLVHttpRequestCallback(httpBridge, globals, metaTable, varargs);
            }
        };
    }
}
