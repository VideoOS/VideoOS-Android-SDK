package cn.com.venvy.lua.view;

import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.lua.bridge.LVHttpBridge;
import cn.com.venvy.lua.ud.VenvyUDHttpRequestCallback;

/**
 * Created by videojj_pls on 2019/5/7.
 */

public class VenvyLVHttpRequestCallback extends View implements ILVView {
    private VenvyUDHttpRequestCallback mLuaUserdata;

    public VenvyLVHttpRequestCallback(LVHttpBridge httpBridge, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDHttpRequestCallback(httpBridge, this, globals, metaTable, varargs);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
