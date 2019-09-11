package cn.com.venvy.lua.view;

import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.lua.ud.VenvyUDNotificationCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyLVNotificationCallback extends View implements ILVView {

    private VenvyUDNotificationCallback mLuaUserdata;

    public VenvyLVNotificationCallback(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDNotificationCallback(this, globals, metaTable, varargs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
