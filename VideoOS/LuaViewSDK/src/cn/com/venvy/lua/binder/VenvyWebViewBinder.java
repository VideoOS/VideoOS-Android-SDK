package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.maper.LVWebViewMethodMapper;
import cn.com.venvy.lua.view.VenvyLVWebView;


/**
 * Created by Arthur on 2017/9/4.
 */

public class VenvyWebViewBinder extends BaseFunctionBinder {

    public VenvyWebViewBinder() {
        super("WebView");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return LVWebViewMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                VenvyLVWebView venvyLVWebView = new VenvyLVWebView(globals, metaTable, varargs);
                venvyLVWebView.init(globals.getContext());
                return venvyLVWebView;
            }
        };
    }
}
