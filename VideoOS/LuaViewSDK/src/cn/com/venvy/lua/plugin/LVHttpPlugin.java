package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.http.base.RequestType;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;
import cn.com.venvy.lua.bridge.LVHttpBridge;

import static cn.com.venvy.common.http.base.RequestType.GET;

/**
 * 网络请求插件
 * Created by Arthur on 2017/8/21.
 */
public class LVHttpPlugin {

    public static void install(VenvyLVLibBinder venvyLVLibBinder, LVHttpBridge lvHttpBridge) {
        venvyLVLibBinder.set("get", new GetConnect(lvHttpBridge));
        venvyLVLibBinder.set("post", new PostConnect(lvHttpBridge));
        venvyLVLibBinder.set("delete", new DeleteConnect(lvHttpBridge));
        venvyLVLibBinder.set("put", new PutConnect(lvHttpBridge));
        venvyLVLibBinder.set("abortAll", new CancelAll(lvHttpBridge));
        venvyLVLibBinder.set("abort", new Cancel(lvHttpBridge));
        venvyLVLibBinder.set("upload", new Upload(lvHttpBridge));
    }

    private static class Upload extends VarArgFunction {

        private LVHttpBridge httpBridge;

        Upload(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                String url = LuaUtil.getString(args, fixIndex + 1);
                String filePath = LuaUtil.getString(args, fixIndex + 2);
                LuaFunction callback = LuaUtil.getFunction(args, fixIndex + 3);
                httpBridge.upload(url, filePath, callback);

            }
            return LuaValue.NIL;
        }
    }

    private static class GetConnect extends VarArgFunction {
        private LVHttpBridge httpBridge;

        GetConnect(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return startConnect(args, httpBridge, GET);
        }
    }

    private static class PostConnect extends VarArgFunction {
        private LVHttpBridge httpBridge;

        PostConnect(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return startConnect(args, httpBridge, RequestType.POST);
        }
    }

    private static class DeleteConnect extends VarArgFunction {
        private LVHttpBridge httpBridge;

        DeleteConnect(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return startConnect(args, httpBridge, RequestType.DELETE);
        }
    }

    private static class CancelAll extends VarArgFunction {
        private LVHttpBridge httpBridge;

        CancelAll(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return LuaValue.valueOf(httpBridge != null && httpBridge.abortAll());
        }
    }

    private static class Cancel extends VarArgFunction {
        private LVHttpBridge httpBridge;

        Cancel(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {

            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                Integer requestId = LuaUtil.getInt(args, fixIndex + 1);
                return valueOf(requestId != null && httpBridge != null && httpBridge.abort(requestId));
            }
            return LuaValue.valueOf(false);
        }
    }

    private static class PutConnect extends VarArgFunction {
        private LVHttpBridge httpBridge;

        PutConnect(LVHttpBridge lvHttpBridge) {
            this.httpBridge = lvHttpBridge;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return startConnect(args, httpBridge, RequestType.PUT);
        }
    }

    private static Varargs startConnect(Varargs args, LVHttpBridge lvHttpBridge, RequestType requestType) {
        final int fixIndex = VenvyLVLibBinder.fixIndex(args);
        int requestId = -1;
        if (args.narg() > fixIndex) {
            String url = LuaUtil.getString(args, fixIndex + 1);
            LuaTable table = LuaUtil.getTable(args, fixIndex + 2);
            LuaFunction callback = LuaUtil.getFunction(args, fixIndex + 3);
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
