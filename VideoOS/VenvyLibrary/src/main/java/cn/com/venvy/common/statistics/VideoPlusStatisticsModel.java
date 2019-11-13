package cn.com.venvy.common.statistics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;

/**
 * Created by videopls on 2019/8/22.
 */
public class VideoPlusStatisticsModel extends VideoPlusStatisticsBaseModel {
    private static final String SERVICE_STATISTICS_URL = Config.HOST_VIDEO_OS + "/commonStats";
    private String dataJson;
    private VideoPlusStatisticsCallback videoPlusStatisticsCallback;
    public VideoPlusStatisticsModel(@NonNull Platform platform,String dataJson,VideoPlusStatisticsCallback videoPlusStatisticsCallback) {
        super(platform);
        this.dataJson = dataJson;
        this.videoPlusStatisticsCallback = videoPlusStatisticsCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(SERVICE_STATISTICS_URL,createHead(),createBody());
    }

    private Map<String, String> createHead() {
        HashMap<String, String> headMap = new HashMap<>();
        if(getPlatform() != null && getPlatform().getPlatformInfo() != null && getPlatform().getPlatformInfo().getAppKey() != null){
            headMap.put("appKey", getPlatform().getPlatformInfo().getAppKey());
        }
        return headMap;
    }

    private Map<String, String> createBody() {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("data", VenvyAesUtil.encrypt(getPlatform().getPlatformInfo().getAppSecret(), getPlatform().getPlatformInfo().getAppSecret(), dataJson));
        return bodyMap;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (!response.isSuccess()) {
                    VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
                    if (configCallback != null) {
                        configCallback.updateError(new Exception("get app config error"));
                    }
                }
                VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
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
                VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
                if (configCallback != null) {
                    configCallback.updateError(e);
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

    public interface VideoPlusStatisticsCallback {
        void updateComplete(String result);

        void updateError(Throwable t);
    }

    @Override
    public void destroy() {
        super.destroy();
        videoPlusStatisticsCallback = null;
    }

    private VideoPlusStatisticsCallback getAppConfigCallback() {
        return videoPlusStatisticsCallback;
    }
}
