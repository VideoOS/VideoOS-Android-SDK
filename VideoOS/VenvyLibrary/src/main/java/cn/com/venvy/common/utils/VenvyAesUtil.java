
package cn.com.venvy.common.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密算法
 *
 * @author long
 */
public class VenvyAesUtil {

    // /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/CBC/PKCS5Padding";

    private static final int IV_LENGTH = 16;
    private static final int KEY_LENGTH = 16;
    // /** 创建密钥 **/
    private static SecretKeySpec createKey(String key) {
        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(KEY_LENGTH);
        sb.append(key);
        while (sb.length() < KEY_LENGTH) {
            sb.append("0");
        }
        if (sb.length() > KEY_LENGTH) {
            sb.setLength(KEY_LENGTH);
        }

        data = sb.toString().getBytes();
        return new SecretKeySpec(data, "AES");
    }

    private static IvParameterSpec createIV(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuffer sb = new StringBuffer(IV_LENGTH);
        sb.append(password);
        while (sb.length() < IV_LENGTH) {
            sb.append("0");
        }
        if (sb.length() > IV_LENGTH) {
            sb.setLength(IV_LENGTH);
        }
        data = sb.toString().getBytes();
        return new IvParameterSpec(data);
    }

    // /** 加密字节数据 **/
    public static String encrypt(String password, String iv, byte[] content) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, createIV(iv));
            byte[] result = cipher.doFinal(content);
            return VenvyBase64.encode(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 加密(结果为16进制字符串) **/
    public static String encrypt(String password, String iv, String content) {
        byte[] data = null;
        try {
            data = content.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt(password, iv, data);
    }

    // /** 解密字节数组 **/
    public static byte[] decrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, createIV(iv));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 解密 **/
    public static String decrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            data = VenvyBase64.decode(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, password, iv);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * check asset exists
     *
     * @param context
     * @param assetFilePath
     * @return
     */
    public static boolean exists(final Context context, final String assetFilePath) {
        boolean bAssetOk = false;
        try {
            context.getAssets().open(assetFilePath).close();
            bAssetOk = true;
        } catch (Exception e) {
        }
        return bAssetOk;
    }
}
