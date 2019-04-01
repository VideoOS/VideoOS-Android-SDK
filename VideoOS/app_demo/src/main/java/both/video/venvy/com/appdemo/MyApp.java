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
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.RELEASE);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this,"e3095ad4-5927-40eb-b6e5-a43b7f1e966b");
    }
}
