package both.video.venvy.com.appdemo;

import android.app.Application;
import android.content.Context;

import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.videopls.pub.VideoPlus;

import android.support.multidex.MultiDex;

/**
 * Created by lgf on 2017/3/22.
 */

public class MyApp extends Application {
    private final String appKey = "484cd61e-2bc6-410f-aefd-87131f3a26e2";
    private final String appSecret = "862a1f51722d4333";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this, appKey, appSecret);
    }
}
