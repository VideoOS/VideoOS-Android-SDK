package both.video.venvy.com.appdemo.observe;

import both.video.venvy.com.appdemo.IConfigView;
import both.video.venvy.com.appdemo.http.AppConfigModel;

public class IConfigObserver<V extends IConfigView> extends IObserver<V> {
    protected AppConfigModel mAppConfigModel;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public IConfigObserver(V iView) {
        super(iView);
    }

    public void checkConfigData(String AppKey, String AppSecret) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAppConfigModel != null) {
            mAppConfigModel.destroy();
            mAppConfigModel = null;
        }
    }
}
