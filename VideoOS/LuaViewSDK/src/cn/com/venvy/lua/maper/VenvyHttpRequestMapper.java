package cn.com.venvy.lua.maper;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDHttpRequestCallback;

/**
 * Created by videojj_pls on 2019/5/7.
 */
@LuaViewLib(revisions = {"20190507已对标"})
public class VenvyHttpRequestMapper<U extends VenvyUDHttpRequestCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyHttpRequestMapper";
    private static final String[] sMethods = new String[]{
            "get",
            "post",
            "delete",
            "put",
            "abortAll",
            "abort",
            "upload"
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
                return get(target, varargs);
            case 1:
                return post(target, varargs);
            case 2:
                return delete(target, varargs);
            case 3:
                return put(target, varargs);
            case 4:
                return abortAll(target, varargs);
            case 5:
                return abort(target, varargs);
            case 6:
                return upload(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue get(U target, Varargs varargs) {
        return target.get(varargs);
    }

    public LuaValue post(U target, Varargs varargs) {
        return target.post(varargs);
    }

    public LuaValue delete(U target, Varargs varargs) {
       return target.delete(varargs);
    }

    public LuaValue put(U target, Varargs varargs) {
       return target.put(varargs);
    }

    public LuaValue abortAll(U target, Varargs varargs) {
       return target.abortAll(varargs);
    }

    public LuaValue abort(U target, Varargs varargs) {
       return target.abort(varargs);
    }

    public LuaValue upload(U target, Varargs varargs) {
       return target.upload(varargs);
    }

}
