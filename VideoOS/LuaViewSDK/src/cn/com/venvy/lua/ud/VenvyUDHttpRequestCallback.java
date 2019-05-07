package cn.com.venvy.lua.ud;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.http.base.RequestType;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;
import cn.com.venvy.lua.bridge.LVHttpBridge;
import cn.com.venvy.lua.view.VenvyLVHttpRequestCallback;

import static cn.com.venvy.common.http.base.RequestType.GET;
import static cn.com.venvy.common.http.base.RequestType.POST;

/**
 * Created by videojj_pls on 2019/5/7.
 */

public class VenvyUDHttpRequestCallback extends UDView<VenvyLVHttpRequestCallback> {
    private LVHttpBridge httpBridge;

    public VenvyUDHttpRequestCallback(LVHttpBridge httpBridge, VenvyLVHttpRequestCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        this.httpBridge = httpBridge;
    }

    public LuaValue get(Varargs varargs) {
        return startConnect(varargs, httpBridge, GET);
    }

    public LuaValue post(Varargs varargs) {
        return startConnect(varargs, httpBridge, POST);
    }

    public LuaValue delete(Varargs varargs) {

        return startConnect(varargs, httpBridge, RequestType.DELETE);
    }

    public LuaValue put(Varargs varargs) {

        return startConnect(varargs, httpBridge, RequestType.PUT);
    }

    public LuaValue abortAll(Varargs varargs) {
        return LuaValue.valueOf(httpBridge != null && httpBridge.abortAll());
    }

    public LuaValue abort(Varargs varargs) {

        final int fixIndex = VenvyLVLibBinder.fixIndex(varargs);
        if (varargs.narg() > fixIndex) {
            Integer requestId = LuaUtil.getInt(varargs, 2);
            return valueOf(requestId != null && httpBridge != null && httpBridge.abort(requestId));
        }
        return LuaValue.valueOf(false);
    }

    public LuaValue upload(Varargs varargs) {
        final int fixIndex = VenvyLVLibBinder.fixIndex(varargs);
        if (varargs.narg() > fixIndex) {
            String url = LuaUtil.getString(varargs, 2);
            String filePath = LuaUtil.getString(varargs, 3);
            LuaFunction callback = LuaUtil.getFunction(varargs, 4);
            httpBridge.upload(url, filePath, callback);
        }
        return LuaValue.NIL;
    }

    private LuaValue startConnect(Varargs args, LVHttpBridge lvHttpBridge, RequestType requestType) {
        final int fixIndex = VenvyLVLibBinder.fixIndex(args);
        int requestId = -1;
        if (args.narg() > fixIndex) {
            String url = LuaUtil.getString(args, 2);
            LuaTable table = LuaUtil.getTable(args, 3);
            LuaFunction callback = LuaUtil.getFunction(args, 4);
            switch (requestType) {
                case GET:
                    requestId = lvHttpBridge.get(url, table, callback);
                    break;
                case POST:
                    requestId = lvHttpBridge.post(url, table, callback);
                    break;
                case PUT:
                    requestId = lvHttpBridge.put(url, table, callback);
                    break;
                case DELETE:
                    requestId = lvHttpBridge.delete(url, table, callback);
                    break;
            }
        }
        return LuaValue.valueOf(requestId);
    }
}
