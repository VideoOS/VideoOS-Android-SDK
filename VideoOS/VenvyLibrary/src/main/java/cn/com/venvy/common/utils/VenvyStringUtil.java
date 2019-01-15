package cn.com.venvy.common.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 判断是否是网站
 * @author John
 *
 */
public class VenvyStringUtil {

    public static final String EMPTY = "";

    /**
     * 校验是否是手机号
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|17[0-9])\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 校验是否是邮箱
     *
     * @param
     * @return
     */
    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /***
     *
     * @param num 当前
     * @param total 总数
     * @param scale 保留几位
     * @return
     */
    public static String accuracy(double num, double total, int scale) {
        if (total == 0) {
            total = 1;
        }
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //可以设置精确几位小数  
        df.setMaximumFractionDigits(scale);
        //模式 例如四舍五入  
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return df.format(accuracy_num) + "%";
    }


    /**
     * 把字符转转化为md5加密
     *
     * @param source
     * @return
     */
    public static String convertMD5(String source) {
        byte[] buf = source.getBytes();
        StringBuilder md5Str = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(buf);
            byte[] tmp = md5.digest();
            for (byte b : tmp) {
                md5Str.append(Integer.toHexString(b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        return md5Str.toString();
    }

    // 比较时间的先后
    public static boolean greater(Date d1, Date d2) {
        // 如果compareTo返回0，表示两个日期相等，返回小于0的值，表示d1在d2之前，大于0表示d1在d2之后

        return d1.compareTo(d2) > 0;
    }

    // 切割过长的红包标题
    public static String spliteWalletTitle(String title) {

        if (title.contains("\n")) return title;
        StringBuilder stringBuilder = new StringBuilder();
        int length = title.length();
        if (length > 4) {
            length /= 2;
            stringBuilder.append(title.substring(0, length))
                    .append("\n")
                    .append(title.substring(length));
        } else {
            return title;
        }

        return stringBuilder.toString();
    }

    public static String httpMapToString(Map<String, String> params) {
        if (params == null)
            return null;
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            VenvyLog.e("JSON error : ", e);
        }
        return jsonObject.toString();
    }

    public static String httpMapArrayToString(Map<String, String[]> params) {
        if (params == null)
            return null;
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue()[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            VenvyLog.e("JSON error : ", e);
        }
        return jsonObject.toString();
    }

    public static String getDecodeJSONStr(String s) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Map<String, String[]> getParamsMap(String queryString) {
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        if (queryString != null && queryString.length() > 0) {
            int ampersandIndex, lastAmpersandIndex = 0;
            String subStr, param, value;
            String[] paramPair, values, newValues;
            do {
                ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                if (ampersandIndex > 0) {
                    subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                    lastAmpersandIndex = ampersandIndex;
                } else {
                    subStr = queryString.substring(lastAmpersandIndex);
                }
                paramPair = subStr.split("=");
                param = paramPair[0];
                value = paramPair.length == 1 ? "" : paramPair[1];
                value = URLDecoder.decode(value);
                if (paramsMap.containsKey(param)) {
                    values = paramsMap.get(param);
                    int len = values.length;
                    newValues = new String[len + 1];
                    System.arraycopy(values, 0, newValues, 0, len);
                    newValues[len] = value;
                } else {
                    newValues = new String[]{value};
                }
                paramsMap.put(param, newValues);
            } while (ampersandIndex > 0);
        }
        return paramsMap;
    }

    //使用String的split 方法
    public static String[] convertStrToArray(String str) {
        //拆分字符为"," ,然后把结果交给数组strArray
        return str.split(",");
    }

    /**
     * md5
     *
     * @param s
     * @return
     */
    public static byte[] md5(byte[] s) {
        return encrypt(s, "MD5");
    }


    /**
     * md5
     *
     * @param s
     * @return
     */
    public static String md5Hex(String s) {
        return toHexString(encrypt(s, "MD5").getBytes());
    }

    /**
     * encrypt method
     *
     * @param s
     * @param method encrypt type
     * @return
     */
    private static String encrypt(String s, String method) {
        try {
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(s.getBytes());
            return new String(digest.digest());
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    /**
     * encrypt method
     *
     * @param s
     * @param method encrypt type
     * @return
     */
    private static byte[] encrypt(byte[] s, String method) {
        try {
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(s);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }


    /**
     * to hex string
     *
     * @param keyData
     * @return
     */
    private static String toHexString(byte[] keyData) {
        if (keyData == null) {
            return null;
        }
        int expectedStringLen = keyData.length * 2;
        StringBuilder sb = new StringBuilder(expectedStringLen);
        for (int i = 0; i < keyData.length; i++) {
            String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
            if (hexStr.length() == 1) {
                hexStr = "0" + hexStr;
            }
            sb.append(hexStr);
        }
        return sb.toString();

    }
}
