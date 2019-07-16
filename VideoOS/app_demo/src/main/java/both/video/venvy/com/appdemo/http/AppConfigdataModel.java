package both.video.venvy.com.appdemo.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.UrlContent;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

public class AppConfigdataModel extends IConfigModel {
    private AppConfigCallback mAppConfigCallback;
    private String videoId;
    public AppConfigdataModel(AppConfigCallback appConfigCallback) {
        super();
        this.videoId = videoId;
        this.mAppConfigCallback = appConfigCallback;
    }

    public AppConfigdataModel(String  videoId, AppConfigCallback appConfigCallback) {
        super();
        this.videoId = videoId;
        this.mAppConfigCallback = appConfigCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(UrlContent.URL_APP_QUERY_LAUNCH_INFO,createHead(),createBody());
    }

    private Map<String, String> createHead() {
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("appKey", ConfigUtil.getAppKey());
        return headMap;
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
                VenvyLog.e(AppConfigdataModel.class.getName(), "获取用户配置信息失败 " + (e != null ? e.getMessage() : ""));
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

    private Map<String,String> createBody(){
        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("bu_id", "videoos");
        bodyMap.put("device_type","2");
        bodyMap.put("videoId", videoId);
        bodyMap.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        bodyMap.put("data", VenvyAesUtil.encrypt(ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret(), new JSONObject(bodyMap).toString()));
        return bodyMap;
    }
}
