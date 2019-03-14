package cn.com.venvy.lua.maper;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDKeyboardCallback;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class VenvyKeyboardMapper<U extends VenvyUDKeyboardCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyKeyboardMapper";
    private static final String[] sMethods = new String[]{
            "keyboardCallback",
            "hideKeyboard"
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return keyboardCallback(target, varargs);
            case 1:
                return hideKeyboard(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue keyboardCallback(U target, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        if (callback != null && callback.isfunction()) {
            return target.setKeyboardCallback(callback);
        }
        return LuaValue.NIL;
    }

    public LuaValue hideKeyboard(U target, Varargs varargs) {
        View view = target.getView();
        if (view == null) {
            return this;
        }
        Context context = view.getContext();
        if (context instanceof Activity) {
            View v = ((Activity) context).getCurrentFocus();
            if (v != null) {
                IBinder token = v.getWindowToken();
                if (token != null) {
                    InputMethodManager im = (InputMethodManager) context.getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return this;
    }
}
