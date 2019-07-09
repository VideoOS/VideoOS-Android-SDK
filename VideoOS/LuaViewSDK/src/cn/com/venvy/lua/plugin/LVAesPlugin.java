package cn.com.venvy.lua.plugin;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * Aes加解密
 * Created by Arthur on 2017/8/21.
 */

public class LVAesPlugin {

    private static AesEncrypt sAesEncrypt;
    private static AesDecrypt sAesDecrypt;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("aesEncrypt", sAesEncrypt == null ? sAesEncrypt = new AesEncrypt() : sAesEncrypt);
        venvyLVLibBinder.set("aesDecrypt", sAesDecrypt == null ? sAesDecrypt = new AesDecrypt() : sAesDecrypt);
    }

    /**
     * key,iv,content
     * aes加密
     */
    private static class AesEncrypt extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue contextValue = args.arg(fixIndex + 1);  //content
            LuaValue keyValue = args.arg(fixIndex + 2);  //key
            LuaValue ivValue = args.arg(fixIndex + 3);//iv
            String key = VenvyLVLibBinder.luaValueToString(keyValue);
            String iv = VenvyLVLibBinder.luaValueToString(ivValue);
            String content = VenvyLVLibBinder.luaValueToString(contextValue);

            String aes = VenvyAesUtil.encrypt(key, iv, content);
            return LuaValue.valueOf(aes == null ? "" : aes);
        }
    }

    /**
     * key,iv,content
     * aes解密
     */
    private static class AesDecrypt extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue contextValue = args.arg(fixIndex + 1);  //content
            LuaValue keyValue = args.arg(fixIndex + 2);  //key
            LuaValue ivValue = args.arg(fixIndex + 3);//iv
            String key = luaValueToString(keyValue);
            String iv = luaValueToString(ivValue);
            String content = luaValueToString(contextValue);

            String aes = VenvyAesUtil.decrypt(content, key, iv);
            VenvyLog.d("aes result : " + aes);
            return LuaValue.valueOf(aes == null ? "" : aes);
        }
    }
}
