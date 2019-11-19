package cn.com.venvy.common.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Lucas on 2019/11/18.
 */
public class VenvyVibrateUtil {

    //震动milliseconds毫秒
    public static void vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    //以pattern[]方式震动
    public static void vibrate(final Context context, long[] pattern,int repeat){
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern,repeat);
    }
    //取消震动
    public static void virateCancle(final Context context){
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }
}
