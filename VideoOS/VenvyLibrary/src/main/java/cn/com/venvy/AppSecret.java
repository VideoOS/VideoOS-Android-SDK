package cn.com.venvy;

import android.text.TextUtils;

/**
 * Created by videojj_pls on 2019/3/4.
 */

public class AppSecret {
    private static final String AES_KEY = "inekcndsaqwertyi";

    public static String getAppSecret(Platform platform) {
        return platform == null ? AES_KEY : TextUtils.isEmpty(platform.getPlatformInfo().getAppSecret()) ? AES_KEY : platform.getPlatformInfo().getAppSecret();
    }
}
