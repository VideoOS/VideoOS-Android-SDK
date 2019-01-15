package cn.com.venvy.common.utils;

import javax.crypto.*;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;


/**
 * 创建日期:2017-6-9
 * Title:
 * Description：对本文件的详细描述，原则上不能少于50字
 *
 * @author
 * @version 1.0
 *          Remark：认为有必要的其他信息
 * @mender：
 */
public class VenvyRSAUtil {
    public static final String KEY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBlxdQe+B3bCL3+km31ABB23sXUB0A3owEBodW\n" +
            "lPeikgfEw/JfbZXuiKFoIqAbjmzpDvAE4PYAU4wBjE01wRNLg4KLJyorGLkx6I6gHE67mZqLryep\n" +
            "xZdwd8MwzQCsoN3+PAQYUJz54Flc6e14l/LVDyggw/HN/OD9iXC027IVDQIDAQAB";
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String RSA_CHARSET = "UTF-8";
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";//SHA1withRSA MD5withRSA

    /**
     * 功能:RSA公钥加密
     * 作者:
     * 创建日期:2017-6-9
     *
     * @param rawData
     * @param publicKey
     * @return
     */
    public static String encryptByRSA(String rawData, String publicKey) {
        try {
            Cipher encodeCipher = Cipher.getInstance(RSA_ALGORITHM);
            encodeCipher.init(Cipher.ENCRYPT_MODE, string2PublicKey(publicKey));
            int blockSize = encodeCipher.getBlockSize();// 127
            byte[] data = rawData.getBytes(RSA_CHARSET);
            int data_length = data.length;// 明文数据大小
            int outputSize = encodeCipher.getOutputSize(data_length);// 输出缓冲区大小
            // 计算出块的数量
            int blocksSize = (data_length + blockSize - 1) / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data_length - i * blockSize > 0) {
                if (data_length - i * blockSize > blockSize) {
                    encodeCipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    encodeCipher.doFinal(data, i * blockSize, data_length - i * blockSize, raw, i * outputSize);
                }
                i = ++i;
            }
            return VenvyBase64.encode(raw);
        } catch (Exception e) {
            throw new RuntimeException(String.format("公钥对数据[%s]使用字符集[%s]加密失败",
                    rawData, RSA_CHARSET), e);
        }
    }

    /**
     * 功能:私钥RSA解密
     * 作者:
     * 创建日期:2017-6-9
     *
     * @param encodeData
     * @param privateKey
     * @return
     */
    public static String decryptByRSA(String encodeData, String privateKey) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(MAX_DECRYPT_BLOCK);
            /**解密这个地方注意，第一种方式适合服务器之间的加解密，第二种方式指定解密算法，主要是用于和移动端交互的时候用*/
            Cipher decodeCipher = Cipher.getInstance(RSA_ALGORITHM);

            decodeCipher.init(Cipher.DECRYPT_MODE, string2PrivateKey(privateKey));
            int blockSize = decodeCipher.getBlockSize();
            int j = 0;
            byte[] raw = VenvyBase64.decode(encodeData);
            while (raw.length - j * blockSize > 0) {
                bout.write(decodeCipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            byte[] decryptedData = bout.toByteArray();
            return new String(decryptedData, RSA_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(String.format("私钥对数据[%s]使用字符集[%s]解密失败",
                    encodeData, RSA_CHARSET), e);
        }
    }


    /**
     * 功能:使用私钥签名
     * 作者:
     * 创建日期:2017-6-9
     *
     * @param rawData
     * @param privateKey
     * @return
     */
    public static String sign(String rawData, String privateKey) {
        try {
            Signature signSignature = Signature
                    .getInstance(SIGNATURE_ALGORITHM);
            signSignature.initSign(string2PrivateKey(privateKey));
            signSignature.update(rawData.getBytes(RSA_CHARSET));
            return VenvyBase64.encode(signSignature.sign());
        } catch (Exception e) {
            throw new RuntimeException(String.format("使用私钥对数据[%s]进行[%s]签名失败",
                    rawData, RSA_CHARSET), e);
        }
    }


    /**
     * 功能:公钥验签
     * 作者:
     * 创建日期:2017-6-9
     *
     * @param rawData
     * @param sign
     * @param publicKey
     * @return
     */
    public static boolean verify(String rawData, String sign, String publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(string2PublicKey(publicKey));
            signature.update(rawData.getBytes(RSA_CHARSET));
            return signature.verify(VenvyBase64.decode(sign));
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "公钥使用签名串[%s]对数据[%s]进行[%s]验签失败", sign, rawData, RSA_CHARSET), e);
        }
    }

    /**
     * 使用RSA私钥加密数据
     *
     * @param
     * @param
     * @return 加密数据
     */
    public static String encryptByRSA1(String rawData, String privateKey) {
        try {
            Cipher encodeCipher = Cipher.getInstance(RSA_ALGORITHM);
            encodeCipher.init(Cipher.ENCRYPT_MODE, string2PrivateKey(privateKey));
            int blockSize = encodeCipher.getBlockSize();// 127
            byte[] data = rawData.getBytes(RSA_CHARSET);
            int data_length = data.length;// 明文数据大小
            int outputSize = encodeCipher.getOutputSize(data_length);// 输出缓冲区大小
            // 计算出块的数量
            int blocksSize = (data_length + blockSize - 1) / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data_length - i * blockSize > 0) {
                if (data_length - i * blockSize > blockSize) {
                    encodeCipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    encodeCipher.doFinal(data, i * blockSize, data_length - i * blockSize, raw, i * outputSize);
                }
                i = i + 1;
            }
            return VenvyBase64.encode(raw);
        } catch (Exception e) {
            throw new RuntimeException(String.format("私钥对数据[%s]使用字符集[%s]加密失败",
                    rawData, RSA_CHARSET), e);
        }

    }

    /**
     * 用RSA公钥解密
     *
     * @param
     * @param
     */
    public static String decryptByRSA1(String encodeData, String publicKey) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(MAX_DECRYPT_BLOCK);
            Cipher decodeCipher1 = Cipher.getInstance("RSA");

            decodeCipher1.init(Cipher.DECRYPT_MODE, string2PublicKey(publicKey));
            int blockSize = decodeCipher1.getBlockSize();
            int j = 0;
            byte[] raw = VenvyBase64.decode(encodeData);
            while (raw.length - j * blockSize > 0) {
                bout.write(decodeCipher1.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            byte[] decryptedData = bout.toByteArray();
            return new String(decryptedData, RSA_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(String.format("私钥对数据[%s]使用字符集[%s]解密失败",
                    encodeData, RSA_CHARSET), e);
        }
    }


    /**
     * 计算字符串的SHA数字摘要，以byte[]形式返回
     */
    public static byte[] MdigestSHA(String source) {
        //byte[] nullreturn = { 0 };
        try {
            MessageDigest thisMD = MessageDigest.getInstance("SHA");
            byte[] digest = thisMD.digest(source.getBytes("UTF-8"));
            return digest;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 功能:密钥转成字符串
     * 作者: 余建
     * 创建日期:2017-6-9
     *
     * @param key
     * @return
     */
    public String keyToString(Key key) {
        try {
            return VenvyBase64.encode(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(String.format("输出密钥[%s]字符串失败", key.getFormat()), e);
        }
    }


    /**
     * 功能:公钥字符串转公钥
     * 作者: 余建
     * 创建日期:2017-6-9
     *
     * @param publicKeyStr
     * @return
     */
    public static PublicKey string2PublicKey(String publicKeyStr) {
        PublicKey publicKey = null;
        X509EncodedKeySpec bobPubKeySpec = null;
        try {
            bobPubKeySpec = new X509EncodedKeySpec(VenvyBase64.decode(publicKeyStr));
            // RSA对称加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            publicKey = keyFactory.generatePublic(bobPubKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("公钥[%s]加载失败", publicKeyStr), e);
        }
        return publicKey;
    }

    /**
     * 功能: 私钥字符串转私钥
     * 作者: 余建
     * 创建日期:2017-6-9 上午11:55:19
     *
     * @param privateKyeStr
     * @return
     */
    public static PrivateKey string2PrivateKey(String privateKyeStr) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec priPKCS8 = null;
        try {
            priPKCS8 = new PKCS8EncodedKeySpec(VenvyBase64.decode(privateKyeStr));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(priPKCS8);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("私钥[%s]加载失败", privateKyeStr), e);
        }
        return privateKey;
    }

    /**
     * 功能: 根据路径加载公钥
     * 作者: 余建
     * 创建日期:2017-6-9 上午11:03:34
     *
     * @param publicKeyPath
     * @return
     */
    private PublicKey getPublicKeyFromX509(String publicKeyPath) {
        try {
            InputStream fin = getInputStream(publicKeyPath);
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f
                    .generateCertificate(fin);
            return certificate.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(String.format("加载公钥证书路径[%s]失败",
                    publicKeyPath), e);
        }
    }

    private InputStream getInputStream(String keyFilePath) {
        try {
            return new FileInputStream(new File(keyFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("文件[%s]加载失败", keyFilePath), e);
        }
    }

    /**
     * 功能: 加载私钥证书
     * 作者: 余建
     * 创建日期:2017-6-9 上午11:04:06
     *
     * @param privateKeyPath
     * @param password
     * @return
     */
    private PrivateKey getPrivateKey(String privateKeyPath, String password) {
        try {
            InputStream is = getInputStream(privateKeyPath);
            KeyStore store = KeyStore.getInstance("pkcs12");
            char[] passwordChars = password.toCharArray();
            store.load(is, passwordChars);
            Enumeration<String> e = store.aliases();
            if (e.hasMoreElements()) {
                String alias = e.nextElement();
                return (PrivateKey) store.getKey(alias, passwordChars);
            }
            throw new RuntimeException(String.format(
                    "无法加载私钥证书路径[%s]及密码[%s],请核对证书文件及密码", privateKeyPath, password));
        } catch (KeyStoreException | NoSuchAlgorithmException
                | CertificateException | IOException
                | UnrecoverableKeyException e) {
            throw new RuntimeException(String.format("加载私钥证书路径[%s]及密码[%s]失败",
                    privateKeyPath, password), e);
        }
    }

}
