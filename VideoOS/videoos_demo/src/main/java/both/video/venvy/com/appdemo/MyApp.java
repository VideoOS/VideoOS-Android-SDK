package both.video.venvy.com.appdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.taobao.luaview.util.ToastUtil;

import both.video.venvy.com.appdemo.utils.ConfigUtil;
import cn.com.venvy.Config;
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
        ToastUtil.showToast(this,"init app");
        ConfigUtil.putAppKey("4bd773ed-ad39-4dc5-87b1-c9e35ceeee8f");
        ConfigUtil.putAppSecret("da1b1ff3a8e749e7");


        ConfigUtil.putVideoName("https://static.videojj.com/dev/Video/lundaonile.mp4");
        ConfigUtil.putVideoId("66ecd57c83e300167d17306298210bd5");
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.RELEASE);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this, ConfigUtil.getAppKey(), ConfigUtil.getAppSecret());
    }
}
