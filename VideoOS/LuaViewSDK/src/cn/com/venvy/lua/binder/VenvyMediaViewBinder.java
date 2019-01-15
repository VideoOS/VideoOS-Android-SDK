package cn.com.venvy.lua.binder;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

import cn.com.venvy.lua.maper.VenvyMediaPlayerMethodMapper;
import cn.com.venvy.lua.view.VenvyMediaPlayerView;

/**
 * Created by lgf on 2018/1/19.
 */

public class VenvyMediaViewBinder extends BaseFunctionBinder {
    public VenvyMediaViewBinder() {
        super("MediaPlayer");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyMediaPlayerMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new VenvyMediaPlayerView(globals, metaTable, varargs);
            }
        };
    }
}
