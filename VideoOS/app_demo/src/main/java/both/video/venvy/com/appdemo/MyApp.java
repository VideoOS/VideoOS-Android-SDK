package both.video.venvy.com.appdemo;

import android.app.Application;
import android.content.Context;

import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.videopls.pub.VideoPlus;

import android.support.multidex.MultiDex;

import org.luaj.vm2.ast.Str;

/**
 * Created by lgf on 2017/3/22.
 */

public class MyApp extends Application {
    private final String appKey = "e3095ad4-5927-40eb-b6e5-a43b7f1e966b";
    private final String appSecret = "b28a1f82e6c147d8";

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
        VideoPlus.appCreateSAAS(MyApp.this, appKey, appSecret);
    }
}
