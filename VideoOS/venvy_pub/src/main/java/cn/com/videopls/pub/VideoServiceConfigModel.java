package cn.com.videopls.pub;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videojj_pls on 2019/7/12.
 * 获取视联网配置信息
 */

public class VideoServiceConfigModel extends VideoPlusBaseModel {

    private static final String SERVICE_CONFIG_URL = "/api/service/config";
    private ServiceConfigCallback mServiceConfigCallback;

    public VideoServiceConfigModel(Platform platform, VideoServiceConfigModel.ServiceConfigCallback callback) {
        super(platform);
        mServiceConfigCallback = callback;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                //TODO 业务逻辑处理
                VideoServiceConfigModel.ServiceConfigCallback callback = getServiceConfigCallback();
                if (callback != null) {
                    callback.updateComplete();
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoServiceConfigModel.class.getName(), "视联网Config接口访问失败 " + (e != null ? e.getMessage() : ""));
                VideoServiceConfigModel.ServiceConfigCallback callback = getServiceConfigCallback();
                if (callback != null) {
                    callback.updateError(e);
                }
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(Config.HOST_VIDEO_OS + SERVICE_CONFIG_URL, createBody());
    }

    public interface ServiceConfigCallback {
        void updateComplete();

        void updateError(Throwable t);
    }

    private VideoServiceConfigModel.ServiceConfigCallback getServiceConfigCallback() {
        return mServiceConfigCallback;
    }

    private Map<String, String> createBody() {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return paramBody;
    }

    public void destroy() {
        mServiceConfigCallback = null;
    }
}
