package both.video.venvy.com.appdemo.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import both.video.venvy.com.appdemo.UrlContent;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;

public class AppConfigModel extends IConfigModel {
    private AppConfigCallback mAppConfigCallback;

    public AppConfigModel(AppConfigCallback appConfigCallback) {
        super();
        this.mAppConfigCallback = appConfigCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.get(UrlContent.URL_APP_INFO);
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (!response.isSuccess()) {
                    AppConfigCallback configCallback = getAppConfigCallback();
                    if (configCallback != null) {
                        configCallback.updateError(new Exception("get app config error"));
                    }
                }
                AppConfigCallback configCallback = getAppConfigCallback();
                if (configCallback == null) {
                    return;
                }
                String result = response.getResult();
                if (!TextUtils.isEmpty(result)) {
                    configCallback.updateComplete(result);
                } else {
                    configCallback.updateError(new NullPointerException("get app config NullPointerException"));
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(AppConfigModel.class.getName(), "获取用户配置信息失败 " + (e != null ? e.getMessage() : ""));
                AppConfigCallback configCallback = getAppConfigCallback();
                if (configCallback != null) {
                    configCallback.updateError(e);
                }
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int i) {

            }
        };
    }

    public interface AppConfigCallback {
        void updateComplete(String result);

        void updateError(Throwable t);
    }

    @Override
    public void destroy() {
        super.destroy();
        mAppConfigCallback = null;
    }

    private AppConfigCallback getAppConfigCallback() {
        return mAppConfigCallback;
    }
}
