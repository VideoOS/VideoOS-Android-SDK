package cn.com.videopls.pub;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视频投放信息查询
 */

public class VideoServiceQueryAdsModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_ADS_URL = Config.HOST_VIDEO_OS
            + "/api/queryAllAds";
    private ServiceQueryAdsCallback mQueryAdsCallback;
    private PreloadLuaUpdate mDownLuaUpdate;
    private Map<String, String> mQueryAdsParams;

    public VideoServiceQueryAdsModel(Platform platform, Map<String, String> params,
                                     VideoServiceQueryAdsModel.ServiceQueryAdsCallback callback) {
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

    private void callbackException(String message) {
        ServiceQueryAdsCallback callback = getQueryAdsCallback();
        if (callback != null) {
            callback.queryError(new Exception(message));
        }
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (!response.isSuccess()) {
                        callbackException("query ads data error");
                        return;
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    if (TextUtils.isEmpty(encryptData)) {
                        callbackException("query ads encryptData is null");
                        return;
                    }
                    final JSONObject decryptObj = new JSONObject(VenvyAesUtil.decrypt(encryptData,
                            AppSecret.getAppSecret(getPlatform()),
                            AppSecret.getAppSecret(getPlatform())));
                    if (decryptObj == null) {
                        callbackException("query ads encryptData is null");
                        return;
                    }
                    final String resCode = decryptObj.optString("resCode");
                    if (!TextUtils.equals(resCode, "00")) {
                        callbackException(decryptObj.optString("resMsg"));
                        return;
                    }
                    final JSONObject launchInfoObj = decryptObj.optJSONObject("launchInfo");
                    if (launchInfoObj == null) {
                        callbackException("query ads launchInfo is null,未查询到有投放数据");
                        return;
                    }

                    final String id = launchInfoObj.optString("id");
                    JSONObject miniAppInfoObj = launchInfoObj.optJSONObject("miniAppInfo");
                    if (TextUtils.isEmpty(id) || miniAppInfoObj == null) {
                        callbackException("id or miniAppInfo is null");
                        return;
                    }
                    final String miniAppId = miniAppInfoObj.optString("miniAppId");
                    final String template = miniAppInfoObj.optString("template");
                    if (TextUtils.isEmpty(miniAppId) || TextUtils.isEmpty(template)) {
                        callbackException("miniAppId or template is null");
                        return;
                    }
                    JSONArray luaListArray = miniAppInfoObj.optJSONArray("luaList");
                    if (luaListArray == null || luaListArray.length() <= 0) {
                        callbackException("luaListArray is null");
                        return;
                    }

                    List<LuaFileInfo> luaFileInfoList = new ArrayList<>();

                    LuaFileInfo luaFileInfo = new LuaFileInfo();
                    luaFileInfo.setMiniAppId(miniAppId);
                    List<LuaFileInfo.LuaListBean> luaList = luaArray2LuaList(luaListArray);

                    if(luaList != null && luaList.size() > 0){
                        luaFileInfo.setLuaList(luaList);
                        luaFileInfoList.add(luaFileInfo);
                    }

                    if (luaFileInfoList.size() <= 0) {
                        callbackException("query ads launchInfo is null");
                        return;
                    }

                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new PreloadLuaUpdate(Platform.STATISTICS_DOWNLOAD_STAGE_REALPLAY, getPlatform(), new
                                PreloadLuaUpdate.CacheLuaUpdateCallback() {
                                    @Override
                                    public void updateComplete(boolean isUpdateByNetWork) {
                                        if (isUpdateByNetWork) {
                                            VideoOSLuaView.destroyLuaScript();
                                        }
                                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                                        if (callback != null) {
                                            Map<String, String> params = getQueryAdsParams();
                                            String adsType = params != null ? params.get
                                                    (VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
                                            ServiceQueryAdsInfo queryAdsInfo =
                                                    new ServiceQueryAdsInfo
                                                            .Builder()
                                                            .setQueryAdsTemplate(template)
                                                            .setQueryAdsId(id)
                                                            .setQueryAdsType(!TextUtils.isEmpty(adsType) ?
                                                                    Integer.valueOf(adsType) : 0).build();
                                            callback.queryComplete(launchInfoObj.toString(),
                                                    queryAdsInfo);
                                        }
                                    }

                                    @Override
                                    public void updateError(Throwable t) {
                                        ServiceQueryAdsCallback callback = getQueryAdsCallback();
                                        if (callback != null) {
                                            callback.queryError(new Exception("query ads down lua" +
                                                    " failed"));
                                        }
                                    }
                                });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(luaFileInfoList);
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
        return HttpRequest.post(SERVICE_QUERYALL_ADS_URL, createBody(mQueryAdsParams));
    }

    private Map<String, String> createBody(Map<String, String> params) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        Platform platform = getPlatform();
        if (platform != null) {
            PlatformInfo info = platform.getPlatformInfo();
            if (info != null) {
                String videoId = info.getVideoId();
                if (!TextUtils.isEmpty(videoId)) {
                    bodyParams.put("videoId", videoId);
                }
            }
        }
        if (params != null) {
            bodyParams.putAll(params);
        }
        HashMap<String, String> dataParams = new HashMap<>();
        dataParams.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()),
                AppSecret.getAppSecret(getPlatform()), new JSONObject(bodyParams).toString()));
        return dataParams;
    }

    public interface ServiceQueryAdsCallback {
        void queryComplete(Object queryAdsData, ServiceQueryAdsInfo queryAdsInfo);

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
