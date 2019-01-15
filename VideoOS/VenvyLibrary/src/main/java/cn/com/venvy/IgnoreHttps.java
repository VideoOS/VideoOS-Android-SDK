package cn.com.venvy;

import android.text.TextUtils;

/**
 * Created by yanjiangbo on 2017/9/3.
 */

public class IgnoreHttps {
    public static boolean sIsIgnoreHttps = false;

    public static String ignore(String string) {
        if (!TextUtils.isEmpty(string)) {
            if (string.startsWith("https") && sIsIgnoreHttps) {
                if (string.contains("videojj.com")) {
                    return string.replaceFirst("https", "http");
                }
            }
        }
        return string;
    }
}
