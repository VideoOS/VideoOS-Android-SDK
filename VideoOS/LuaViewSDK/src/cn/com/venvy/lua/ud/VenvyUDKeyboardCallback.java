package cn.com.venvy.lua.ud;

import android.os.Bundle;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.lua.view.VenvyLVKeyboardCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyUDKeyboardCallback extends UDView<VenvyLVKeyboardCallback> {
    LuaValue mCallback;

    public VenvyUDKeyboardCallback(VenvyLVKeyboardCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public VenvyUDKeyboardCallback setKeyboardCallback(LuaValue callbacks) {
        if (callbacks != null) {
            mCallback = callbacks;
        }
        return this;
    }

    public void handleKeyboardBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        int height = bundle.getInt("height");
        int orientation = bundle.getInt("orientation");
        LuaUtil.callFunction(mCallback, LuaValue.valueOf(DimenUtil.pxToDpi(height)), LuaValue.valueOf(orientation));
    }
}
