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
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视频投放信息查询
 */

public class VideoServiceQueryAdsModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_ADS_URL = "/api/queryAllAds";
    private ServiceQueryAdsCallback mQueryAdsCallback;
    private Map<String, String> mParams = new HashMap<>();

    public VideoServiceQueryAdsModel(Platform platform, Map<String, String> params, VideoServiceQueryAdsModel.ServiceQueryAdsCallback callback) {
        super(platform);
        this.mQueryAdsCallback = callback;
        this.mParams = params;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {

            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {

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
        return HttpRequest.post(Config.HOST_VIDEO_OS + SERVICE_QUERYALL_ADS_URL, createBody(mParams));
    }

    private Map<String, String> createBody(Map<String, String> params) {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        if (params != null) {
            paramBody.putAll(params);
        }
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return paramBody;
    }

    public interface ServiceQueryAdsCallback {
        void queryComplete();

        void queryError(Throwable t);
    }
    public void destroy() {
        mQueryAdsCallback = null;
    }
}
