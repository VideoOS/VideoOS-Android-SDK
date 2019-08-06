package both.video.venvy.com.appdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.taobao.luaview.util.ToastUtil;

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
        ToastUtil.showToast(this,"init app");
        ConfigUtil.putAppKey("66c9bafa-abd0-4aa9-8066-3deaf9dc7f71");
        ConfigUtil.putAppSecret("bcfee675bca844bd");
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this, ConfigUtil.getAppKey(), ConfigUtil.getAppSecret());
    }
}
