package cn.com.venvy;

import android.content.Context;

/**
 * Created by yanjiangbo on 2018/1/11.
 */

public class App {

    private static Context sAppContext = null;
    // 是否打开开发者模式
    private static boolean isDevMode = false;

    public static Context getContext() {
        return sAppContext;
    }

    public static void setContext(Context context) {
        if (sAppContext == null) {
            sAppContext = context.getApplicationContext();
        }
    }

    public static void setIsDevMode(boolean isDevMode) {
        App.isDevMode = isDevMode;
    }

    public static boolean isIsDevMode() {
        return isDevMode;
    }
}
