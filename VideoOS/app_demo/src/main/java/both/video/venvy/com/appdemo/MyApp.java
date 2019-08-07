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
        ConfigUtil.putAppKey("73d5a8f8-3682-4080-ad7c-996c4e19fc1e");
        ConfigUtil.putAppSecret("c276b70aba84491a");
        ConfigUtil.putVideoName("http://ai.videojj.com/576ca415ca438532011b3c70/%e4%b8%83%e6%9c%88%e4%b8%8e%e5%ae%89%e7%94%9f02.mp4");
        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
        VenvyLog.needLog = true;
        VideoPlus.appCreateSAAS(MyApp.this, ConfigUtil.getAppKey(), ConfigUtil.getAppSecret());
    }
}
