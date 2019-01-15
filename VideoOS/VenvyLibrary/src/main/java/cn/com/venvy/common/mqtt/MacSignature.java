package cn.com.venvy.common.mqtt;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.com.venvy.common.utils.VenvyRandomUtils;

//MQTT
public class MacSignature {

    private static final String MQTT_SECRET = "PHVGC1GIQpwDQnxEbvGeBbG27seJlw";
    public static final String MQTT_CUSTOMER = "CID_sub_Android@@@";
    /**
     * @param text      要签名的文本
     * @param secretKey 阿里云ONS secretKey
     * @return 加密后的字符串
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static String macSignature(String text, String secretKey)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Charset charset = Charset.forName("UTF-8");
        String algorithm = "HmacSHA1";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey.getBytes(charset), algorithm));
        byte[] bytes = mac.doFinal(text.getBytes(charset));
        return new String(Base64.encode(bytes, Base64.NO_WRAP), charset);
    }

    /**
     * 发送方签名方法
     *
     * @param clientId  Mqtt ClientId
     * @param secretKey 阿里云ONS secretKey
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String publishSignature(String clientId, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {
        return macSignature(clientId, secretKey);
    }

    /**
     * 订阅方签名方法
     *
     * @param topics    要订阅的Topic集合
     * @param clientId  Mqtt ClientId
     * @param secretKey 阿里云ONS secretKey
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String subSignature(List<String> topics, String clientId,
                                      String secretKey) throws NoSuchAlgorithmException,
            InvalidKeyException {
        Collections.sort(topics); // 以字典顺序排序
        String topicText = "";
        for (String topic : topics) {
            topicText += topic + "\n";
        }
        String text = topicText + clientId;
        return macSignature(text, secretKey);
    }

    /**
     * 订阅方签名方法
     *
     * @param topic     要订阅的Topic
     * @param clientId  Mqtt ClientId
     * @param secretKey 阿里云ONS secretKey
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String subSignature(String topic, String clientId,
                                      String secretKey) throws NoSuchAlgorithmException,
            InvalidKeyException {
        List<String> topics = new ArrayList<String>();
        topics.add(topic);
        return subSignature(topics, clientId, secretKey);
    }

    /***
     * 获取密码
     *
     * @return
     */
    public static char[] subPassWord(String mClientId) {
        try {
            return MacSignature.macSignature(mClientId.split("@@@")[0],
                    MQTT_SECRET).toCharArray();
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    /***
     * 获取clientId
     *
     * @return
     */
    public static String subClientId() {
        String utcTime = String.valueOf(System.currentTimeMillis());
        String identity = utcTime + VenvyRandomUtils.getRandomNumbersAndLetters(8);// 区分用户唯一标示
        return MQTT_CUSTOMER + identity;
    }
}

