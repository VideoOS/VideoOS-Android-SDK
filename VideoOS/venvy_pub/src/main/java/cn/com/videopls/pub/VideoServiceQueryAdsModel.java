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
 * Created by videojj_pls on 2019/7/22.
 * 视频投放信息查询
 */

public class VideoServiceQueryAdsModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_ADS_URL = "/api/queryAllAds";
    private static final String SERVICE_QUERYALL_ADS_URL_MOCK = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/queryAllAds#!method=POST&queryParameters=%5B%5D&body=&headers=%5B%5D";
    private ServiceQueryAdsCallback mQueryAdsCallback;
    private Map<String, String> mParams = new HashMap<>();

    public VideoServiceQueryAdsModel(Platform platform, Map<String, String> params, VideoServiceQueryAdsModel.ServiceQueryAdsCallback callback) {
        super(platform);
        this.mQueryAdsCallback = callback;
        this.mParams = params;
    }

    private VideoServiceQueryAdsModel.ServiceQueryAdsCallback getQueryAdsCallback() {
        return mQueryAdsCallback;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (!response.isSuccess()) {
                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data error"));
                            return;
                        }
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    //TODO 先不做判断
                    ServiceQueryAdsCallback callback = getQueryAdsCallback();
                    if (callback != null) {
                        callback.queryComplete(value.toString());
                    }
                } catch (Exception e) {
                    VenvyLog.e(VideoServiceQueryAdsModel.class.getName(), e);
                    ServiceQueryAdsCallback callback = getQueryAdsCallback();
                    if (callback != null) {
                        callback.queryError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoServiceQueryAdsModel.class.getName(), e);
                ServiceQueryAdsCallback callback = getQueryAdsCallback();
                if (callback != null) {
                    callback.queryError(e);
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
        return HttpRequest.post(SERVICE_QUERYALL_ADS_URL_MOCK, createBody(mParams));
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
        void queryComplete(String queryAdsData);

        void queryError(Throwable t);
    }

    public void destroy() {
        mQueryAdsCallback = null;
    }
}
