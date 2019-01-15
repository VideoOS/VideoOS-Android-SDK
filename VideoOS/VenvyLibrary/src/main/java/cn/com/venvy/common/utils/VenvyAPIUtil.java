package cn.com.venvy.common.utils;

import android.os.Build;

/**
 * Created by yanjiangbo on 2017/5/11.
 */

public class VenvyAPIUtil {

    public static boolean isSupport(int apiNo) {
        return Build.VERSION.SDK_INT >= apiNo;
    }
}
