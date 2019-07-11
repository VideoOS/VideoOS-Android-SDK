package both.video.venvy.com.appdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import both.video.venvy.com.appdemo.utils.ConfigUtil;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.videopls.pub.VideoPlus;

/**
 * Created by lgf on 2017/3/22.
 */

public class MyApp extends Application {

    private static MyApp appContext;

    public static MyApp getInstance() {
        return appContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        ConfigUtil.putAppKey("93db5ef3-7fbc-485a-97b0-fc9f4e7209f5");
        ConfigUtil.putAppSecret("74f251d40a49468a");
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this, ConfigUtil.getAppKey(), ConfigUtil.getAppSecret());
    }
}
