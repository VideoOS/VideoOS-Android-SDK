package cn.com.venvy;

import android.content.Context;

/**
 * Created by yanjiangbo on 2018/1/11.
 */

public class App {

    private static Context sAppContext = null;

    public static Context getContext() {
        return sAppContext;
    }

    public static void setContext(Context context) {
        if (sAppContext == null) {
            sAppContext = context.getApplicationContext();
        }
    }
}
