package cn.com.venvy.lua.plugin;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyRSAUtil;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by videojj_pls on 2018/8/30.
 * RSA加解密
 */

public class LVRSAPlugin {
    private static LVRSAPlugin.RSAEncrypt sRsaEncrypt;
    private static LVRSAPlugin.RSADecrypt sRsaDecrypt;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("rsaEncryptStringWithPublicKey", sRsaEncrypt == null ? sRsaEncrypt = new LVRSAPlugin.RSAEncrypt() : sRsaEncrypt);
        venvyLVLibBinder.set("rsaDecryptStringWithPublicKey", sRsaDecrypt == null ? sRsaDecrypt = new LVRSAPlugin.RSADecrypt() : sRsaDecrypt);
    }

    /**
     * data key
     * RSA加密
     */
    private static class RSAEncrypt extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue dataValue = args.arg(fixIndex + 1);  //data
            LuaValue publicKeyValue = args.arg(fixIndex + 2);  //publicKey
            String data = VenvyLVLibBinder.luaValueToString(dataValue);
            String publicKey = VenvyLVLibBinder.luaValueToString(publicKeyValue);

            String rsa = VenvyRSAUtil.encryptByRSA(data, publicKey);
            return LuaValue.valueOf(rsa == null ? "" : rsa);
        }
    }

    /**
     * data key
     * RSA解密
     */
    private static class RSADecrypt extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue dataValue = args.arg(fixIndex + 1);  //data
            LuaValue publicKeyValue = args.arg(fixIndex + 2);  //publicKey
            String data = VenvyLVLibBinder.luaValueToString(dataValue);
            String publicKey = VenvyLVLibBinder.luaValueToString(publicKeyValue);

            String rsa = VenvyRSAUtil.decryptByRSA1(data, publicKey);
            return LuaValue.valueOf(rsa == null ? "" : rsa);
        }
    }
}
