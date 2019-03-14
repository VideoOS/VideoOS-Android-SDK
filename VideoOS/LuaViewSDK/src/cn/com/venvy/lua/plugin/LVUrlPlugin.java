package cn.com.venvy.lua.plugin;


import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import cn.com.venvy.common.utils.VenvyBase64;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * Url处理插件
 * Created by Arthur on 2017/8/21.
 */
public class LVUrlPlugin {

    private static UrlEncode sUrlEncode;
    private static UrlDecode sUrlDecode;
    private static Base64Encode sBase64Encode;
    private static Base64Decode sBase64Decode;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("encode", sUrlEncode == null ? sUrlEncode = new UrlEncode() : sUrlEncode);
        venvyLVLibBinder.set("decode", sUrlDecode == null ? sUrlDecode = new UrlDecode() : sUrlDecode);
        venvyLVLibBinder.set("base64Encode", sBase64Encode == null ? sBase64Encode = new Base64Encode() : sBase64Encode);
        venvyLVLibBinder.set("base64Decode", sBase64Decode == null ? sBase64Decode = new Base64Decode() : sBase64Decode);
    }

    private static class UrlEncode extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue contextValue = args.arg(fixIndex + 1);
            String encodeValue = luaValueToString(contextValue);

            try {
                return LuaValue.valueOf(URLEncoder.encode(encodeValue, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return LuaValue.NIL;
            }
        }
    }

    /**
     * (String) encodeType,(String)value
     */
    private static class UrlDecode extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue contextValue = args.arg(fixIndex + 1);
            String encodeValue = luaValueToString(contextValue);

            try {
                return LuaValue.valueOf(URLDecoder.decode(encodeValue, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return LuaValue.NIL;
            }

        }
    }


    /**
     * Url base64
     */
    private static class Base64Encode extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue target = args.arg(fixIndex + 1);  //key
            String result = luaValueToString(target);
            return LuaValue.valueOf(VenvyBase64.encode(result.getBytes()));
        }
    }

    /**
     * Url 反base64
     */
    private static class Base64Decode extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue target = args.arg(fixIndex + 1);  //key
            String source = luaValueToString(target);

            byte[] data = null;
            try {
                data = VenvyBase64.decode(source);
            } catch (Exception e) {
                VenvyLog.e(LVUrlPlugin.class.getName(), e);
            }
            if (data == null) {
                return LuaValue.NIL;
            }
            try {
                return LuaValue.valueOf(new String(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                return LuaValue.NIL;
            }

        }
    }


}
