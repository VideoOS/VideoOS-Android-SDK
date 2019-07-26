package cn.com.videopls.pub;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视频投放信息查询
 */

public class VideoServiceQueryAdsModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_ADS_URL = "/api/queryAllAds";
    private static final String SERVICE_QUERYALL_ADS_URL_MOCK = "https://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/queryAllAds";
    private ServiceQueryAdsCallback mQueryAdsCallback;
    private VideoPlusLuaUpdate mDownLuaUpdate;
    private Map<String, String> mQueryAdsParams;

    public VideoServiceQueryAdsModel(Platform platform, Map<String, String> params, VideoServiceQueryAdsModel.ServiceQueryAdsCallback callback) {
        super(platform);
        this.mQueryAdsCallback = callback;
        this.mQueryAdsParams = params;
    }

    private VideoServiceQueryAdsModel.ServiceQueryAdsCallback getQueryAdsCallback() {
        return mQueryAdsCallback;
    }

    private Map<String, String> getQueryAdsParams() {
        return mQueryAdsParams;
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
                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data error"));
                        }
                        return;
                    }
                    final JSONObject value = new JSONObject(response.getResult());
                    final String queryAdsId = value.optString("id");
                    if (TextUtils.isEmpty(queryAdsId)) {
                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data with id is null"));
                        }
                        return;
                    }
                    final String queryAdsTemplate = value.optString("template");
                    if (TextUtils.isEmpty(queryAdsTemplate)) {
                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data with template is null"));
                        }
                        return;
                    }
                    JSONArray fileListArray = value.optJSONArray("fileList");
                    if (fileListArray == null || fileListArray.length() <= 0) {
                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data with fileList is null"));
                        }
                        return;
                    }
                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new VideoPlusLuaUpdate(getPlatform(), new VideoPlusLuaUpdate.CacheLuaUpdateCallback() {
                            @Override
                            public void updateComplete(boolean isUpdateByNetWork) {
                                ServiceQueryAdsCallback callback = getQueryAdsCallback();
                                if (callback != null) {
                                    Map<String, String> params = getQueryAdsParams();
                                    String adsType = params != null ? params.get(VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
                                    ServiceQueryAdsInfo queryAdsInfo = new ServiceQueryAdsInfo.Builder().setQueryAdsTemplate(queryAdsTemplate).setQueryAdsId(queryAdsId).setQueryAdsType(!TextUtils.isEmpty(adsType) ? Integer.valueOf(adsType) : 0).build();
                                    callback.queryComplete(value.toString(), queryAdsInfo);
                                }
                            }

                            @Override
                            public void updateError(Throwable t) {
                                ServiceQueryAdsCallback callback = getQueryAdsCallback();
                                if (callback != null) {
                                    callback.queryError(new Exception("query ads down lua failed"));
                                }
                            }
                        });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(fileListArray);
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
        return HttpRequest.post(SERVICE_QUERYALL_ADS_URL_MOCK, new HashMap<String, String>());
    }

    private Map<String, String> createBody(Map<String, String> params) {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        Platform platform = getPlatform();
        if (platform != null) {
            PlatformInfo info = platform.getPlatformInfo();
            if (info != null) {
                String videoId = info.getVideoId();
                if (!TextUtils.isEmpty(videoId)) {
                    paramBody.put("videoId", videoId);
                }
            }
        }
        if (params != null) {
            paramBody.putAll(params);
        }
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return paramBody;
    }

    public interface ServiceQueryAdsCallback {
        void queryComplete(String queryAdsData, ServiceQueryAdsInfo queryAdsInfo);

        void queryError(Throwable t);
    }

    public void destroy() {
        mQueryAdsCallback = null;
        if (mQueryAdsParams != null) {
            mQueryAdsParams.clear();
        }
        if (mDownLuaUpdate != null) {
            mDownLuaUpdate.destroy();
        }
    }
}
