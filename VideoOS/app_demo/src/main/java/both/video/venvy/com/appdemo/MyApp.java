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
    private final String appKey = "921ac192-a38b-4b09-905d-e0c352ec624a";
    private final String appSecret = "6cc1f566c36e4165";

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
