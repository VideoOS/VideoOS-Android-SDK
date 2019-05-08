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
    private final String appKey = "96d4245d-427f-4964-bbe1-3b3b6ccaf7e1";
    private final String appSecret = "ebb53e815fa9458d";

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
