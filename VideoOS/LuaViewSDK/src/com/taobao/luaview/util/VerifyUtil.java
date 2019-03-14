/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import cn.com.venvy.common.utils.VenvyIOUtils;

/**
 * 加解密验证
 *
 * @author song
 * @date 15/11/10
 */
public class VerifyUtil {
    static String TAG = "VerifyUtil";
    static String CACHE_PUBLIC_KEY = AppCache.CACHE_PUBLIC_KEY;

    public static String SIGNATURE_ALGORITHM = "SHA1WithRSA";// "MD5withRSA";
    static String DER_CERT_509 = "X.509";

    /**
     * 验证rsa
     *
     * @param content
     * @param sign
     * @return
     */
    public static boolean rsa(Context context, byte[] content, byte[] sign) {
        try {
            byte[] publicKeyFileData = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH);
            if (publicKeyFileData == null) {
                InputStream inputStream;
                inputStream = context.getAssets().open(Constants.PUBLIC_KEY_PATH);
                publicKeyFileData = VenvyIOUtils.toBytes(inputStream);
                AppCache.getCache(TAG).put(Constants.PUBLIC_KEY_PATH, publicKeyFileData);
            }
            return rsa(content, publicKeyFileData, sign);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证rsa
     *
     * @param content
     * @param publicKey
     * @param sign
     * @return
     */
    public static boolean rsa(byte[] content, byte[] publicKey, byte[] sign) {
        InputStream inputStream = null;
        try {
            PublicKey pk = AppCache.getCache(CACHE_PUBLIC_KEY).get(Constants.PUBLIC_KEY_PATH_PK);//get public key
            if (pk == null) {
                inputStream = new ByteArrayInputStream(publicKey);
                pk = CertificateFactory.getInstance(DER_CERT_509).generateCertificate(inputStream).getPublicKey();
                AppCache.getCache(CACHE_PUBLIC_KEY).put(Constants.PUBLIC_KEY_PATH_PK, pk);//cache public key
            }
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(pk);
            sig.update(content);
            return sig.verify(sign);

        } catch (Exception e) {
        } finally {
            VenvyIOUtils.close(inputStream);
        }
        return false;
    }


}
