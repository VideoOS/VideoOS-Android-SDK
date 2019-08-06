package com.taobao.luaview.userdata.kit;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.AndroidUtil;
import com.taobao.luaview.util.DimenUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Created by Lucas on 2019/8/2.
 */
@LuaViewLib(revisions = {"20190802已对标"})
public class UDApplet extends BaseLuaTable {


    public UDApplet(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        set("appletSize", new AppletSize());// 返回视联网小程序容器size
    }

    class AppletSize extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            float width = 222;
            float height = DimenUtil.pxToDpi(AndroidUtil.getScreenHeight(getContext()));
            LuaValue[] luaValue = new LuaValue[]{LuaValue.valueOf(width), LuaValue.valueOf(height)};
            return LuaValue.varargsOf(luaValue);
        }
    }


}
