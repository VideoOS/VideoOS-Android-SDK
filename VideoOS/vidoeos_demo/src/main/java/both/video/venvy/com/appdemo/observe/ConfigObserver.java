package both.video.venvy.com.appdemo.observe;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import both.video.venvy.com.appdemo.IConfigView;
import both.video.venvy.com.appdemo.bean.ConfigData;
import both.video.venvy.com.appdemo.http.AppConfigModel;
import cn.com.venvy.common.utils.VenvyUIUtil;

public class ConfigObserver<V extends IConfigView> extends IConfigObserver<V> {
    public ConfigObserver(V iView) {
        super(iView);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getModeView().showLoading();
        if (mAppConfigModel == null) {
            mAppConfigModel = new AppConfigModel(new AppConfigModel.AppConfigCallback() {
                @Override
                public void updateComplete(final String result) {
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            getModeView().hideLoading();
                            ConfigData configData = JSON.parseObject(result, ConfigData.class);
                            if (configData == null) {
                                return;
                            }
                            ConfigData.ConfigInfo configInfo = configData.getData();
                            if (configInfo == null) {
                                return;
                            }
                            getModeView().doNetConfigInfo(configInfo.getApps());
                        }
                    });

                }

                @Override
                public void updateError(Throwable t) {
                    getModeView().hideLoading();
                }
            });
        }
        mAppConfigModel.startRequest();
    }

    @Override
    public void checkConfigData(String AppKey, String AppSecret) {
        super.checkConfigData(AppKey, AppSecret);
        if (TextUtils.isEmpty(AppKey)) {
            getModeView().onError("请检查你输入的AppKey");
            return;
        }
        if (TextUtils.isEmpty(AppSecret)) {
            getModeView().onError("请检查你输入的AppSecret");
            return;
        }
        getModeView().doUpdate(AppKey, AppSecret);
    }
}
