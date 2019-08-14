package cn.com.videopls.pub.track;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.VideoPlusBaseModel;

/***
 * 视联网模式统计
 */

public class ChainTrackModel extends VideoPlusBaseModel {
    private static final String CHAIN_TRACK_URL = Config.HOST_VIDEO_OS
            + "/statistic/collectVisionSwitchTimes";
    private ChainTrackCallback mChainTrackCallback;
    private String mOnOff = "-1";
    private static final String ON_OR_OFF = "onOrOff";

    public ChainTrackModel(Platform platform, String onOff,
                           ChainTrackCallback callback) {
        super(platform);
        this.mChainTrackCallback = callback;
        this.mOnOff = onOff;
    }

    private ChainTrackModel.ChainTrackCallback getChainTrackCallback() {
        return mChainTrackCallback;
    }

    @Override
    public boolean needCheckResponseValid() {
        return false;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (!response.isSuccess()) {
                        ChainTrackCallback callback = getChainTrackCallback();
                        if (callback != null) {
                            callback.trackError(new Exception("track chain data error"));
                        }
                        return;
                    }
                    JSONObject obj = new JSONObject(response.getResult());
                    String resCode = obj.optString("resCode");
                    if (TextUtils.equals(resCode, "00")) {
                        ChainTrackCallback callback = getChainTrackCallback();
                        if (callback != null) {
                            callback.trackComplete();
                        }
                        return;
                    }

                } catch (Exception e) {
                    VenvyLog.e(ChainTrackModel.class.getName(), e);
                    ChainTrackCallback callback = getChainTrackCallback();
                    if (callback != null) {
                        callback.trackError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(ChainTrackModel.class.getName(), e);
                ChainTrackCallback callback = getChainTrackCallback();
                if (callback != null) {
                    callback.trackError(e);
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
        return HttpRequest.post(CHAIN_TRACK_URL, createBody());
    }

    private Map<String, String> createBody() {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        Platform platform = getPlatform();
        if (platform != null) {
            PlatformInfo info = platform.getPlatformInfo();
            if (info != null) {
                String appKey = info.getAppKey();
                if (!TextUtils.isEmpty(appKey)) {
                    bodyParams.put("appKey", appKey);
                }
            }
        }
        if (!TextUtils.isEmpty(mOnOff)) {
            bodyParams.put(ON_OR_OFF, mOnOff);
        }
        HashMap<String, String> dataParams = new HashMap<>();
        dataParams.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()),
                AppSecret.getAppSecret(getPlatform()), new JSONObject(bodyParams).toString()));
        return dataParams;
    }

    public interface ChainTrackCallback {
        void trackComplete();

        void trackError(Throwable t);
    }

    public void destroy() {
        mChainTrackCallback = null;
    }
}
