package cn.com.videopls.pub;

import android.os.Bundle;
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
import cn.com.venvy.PreloadZipUpdate;
import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyMD5Util;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by videojj_pls on 2019/7/22.
 * 视联网模式投放信息查询
 */

public class VideoServiceQueryChainModel extends VideoPlusBaseModel {
    private static final String SERVICE_QUERYALL_CHAIN_URL_MOCK = Config.HOST_VIDEO_OS
            + "/vision/v2/getLabelConf";
    private static final String LUA_ZIP = "/lua/os/chain.zip";
    private ServiceQueryChainCallback mQueryChainCallback;
    private PreloadLuaUpdate mDownLuaUpdate;
    private PreloadZipUpdate mDownZipUpdate;
    private Map<String, String> mQueryAdsParams;
    private boolean isTagMode; // 是否是气泡模式

    public VideoServiceQueryChainModel(Platform platform, Map<String, String> params, boolean isTagMode,
                                       VideoServiceQueryChainModel.ServiceQueryChainCallback callback) {
        super(platform);
        this.isTagMode = isTagMode;
        this.mQueryChainCallback = callback;
        this.mQueryAdsParams = params;
    }

    private VideoServiceQueryChainModel.ServiceQueryChainCallback getQueryChainCallback() {
        return mQueryChainCallback;
    }

    private Map<String, String> getQueryChainParams() {
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
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query ads data error"));
                        }
                        return;
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    String decrypt = VenvyAesUtil.decrypt(encryptData,
                            AppSecret.getAppSecret(getPlatform()),
                            AppSecret.getAppSecret(getPlatform()));
                    final String queryAdsId = VenvyMD5Util.MD5(decrypt);
                    final JSONObject obj = new JSONObject(decrypt);
                    String resCode = obj.optString("resCode");
                    if (TextUtils.equals(resCode, "01")) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            String resMsg = obj.optString("resMsg");
                            if (!TextUtils.isEmpty(resMsg)) {
                                callback.queryError(new Exception(resMsg));
                            } else {
                                callback.queryError(new Exception("query chain data is error"));
                            }
                        }
                        return;
                    }
                    final JSONObject videoModeMiniAppInfoObj = obj.optJSONObject("videoModeMiniAppInfo");
                    final JSONObject desktopMiniAppInfoObj = obj.optJSONObject("desktopMiniAppInfo");
                    if (videoModeMiniAppInfoObj == null || desktopMiniAppInfoObj == null) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query appInfo is error."));
                        }
                        return;
                    }
                    final JSONArray dataJsonArray = obj.optJSONArray("jsonList");

                    final String videoModeMiniAppId = videoModeMiniAppInfoObj.optString("miniAppId");
                    final String videoModeTemplate = videoModeMiniAppInfoObj.optString("template");
                    JSONArray videoModeLuaArray = videoModeMiniAppInfoObj.optJSONArray("luaList");

                    final String desktopMiniAppId = desktopMiniAppInfoObj.optString("miniAppId");
                    final String desktopTemplate = desktopMiniAppInfoObj.optString("template");
                    JSONArray desktopModeLuaArray = desktopMiniAppInfoObj.optJSONArray("luaList");

                    //条件判断
                    if (TextUtils.isEmpty(videoModeMiniAppId) || TextUtils.isEmpty(videoModeTemplate) || videoModeLuaArray == null || videoModeLuaArray.length() <= 0 ||
                            TextUtils.isEmpty(desktopMiniAppId) || TextUtils.isEmpty(desktopTemplate) || desktopModeLuaArray == null || desktopModeLuaArray.length() <= 0) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query data is null."));
                        }
                        return;
                    }

                    List<LuaFileInfo> luaFileInfoList = new ArrayList<>();

                    //videoModeLuaArray json --> bean
                    LuaFileInfo videoModeLuaFileInfo = new LuaFileInfo();
                    videoModeLuaFileInfo.setMiniAppId(videoModeMiniAppId);
                    List<LuaFileInfo.LuaListBean> videoModeLuaList = luaArray2LuaList(videoModeLuaArray);

                    if (videoModeLuaList != null && videoModeLuaList.size() > 0) {
                        videoModeLuaFileInfo.setLuaList(videoModeLuaList);
                        luaFileInfoList.add(videoModeLuaFileInfo);
                    }

                    //desktopModeLuaArray json --> bean
                    LuaFileInfo desktopLuaFileInfo = new LuaFileInfo();
                    desktopLuaFileInfo.setMiniAppId(desktopMiniAppId);
                    List<LuaFileInfo.LuaListBean> desktopLuaList = luaArray2LuaList(desktopModeLuaArray);

                    if (desktopLuaList != null && desktopLuaList.size() > 0) {
                        desktopLuaFileInfo.setLuaList(desktopLuaList);
                        luaFileInfoList.add(desktopLuaFileInfo);
                    }

                    if (luaFileInfoList.size() <= 0) {
                        ServiceQueryChainCallback callback = getQueryChainCallback();
                        if (callback != null) {
                            callback.queryError(new Exception("query data is null."));
                        }
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
                                        //  load desktop lua
                                        loadDesktopProgram(desktopTemplate, desktopMiniAppInfoObj.toString(), obj.toString());

                                        if (dataJsonArray != null && dataJsonArray.length() > 0) {
                                            mDownZipUpdate.startDownloadZipFile(dataJsonArray);
                                        }
                                    }

                                    @Override
                                    public void updateError(Throwable t) {
                                        ServiceQueryChainCallback callback = getQueryChainCallback();
                                        if (callback != null) {
                                            callback.queryError(new Exception("chain ads down lua" +
                                                    " failed"));
                                        }
                                    }
                                });
                    }
                    if (mDownZipUpdate == null) {
                        mDownZipUpdate = new PreloadZipUpdate(Platform.STATISTICS_DOWNLOAD_STAGE_REALPLAY, getPlatform(), new PreloadZipUpdate.CacheZipUpdateCallback() {
                            @Override
                            public void updateComplete(JSONArray zipJsonDataArray) {
                                Map<String, String> params = getQueryChainParams();
                                String adsType = params != null ? params.get
                                        (VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
                                ServiceQueryAdsInfo queryAdsInfo =
                                        new ServiceQueryAdsInfo
                                                .Builder()
                                                .setQueryAdsTemplate(videoModeTemplate)
                                                .setQueryAdsId(queryAdsId)
                                                .setQueryAdsType(!TextUtils.isEmpty(adsType) ?
                                                        Integer.valueOf(adsType) : 0).build();
                                ServiceQueryChainCallback callback = getQueryChainCallback();
                                if (callback != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        if (zipJsonDataArray != null && zipJsonDataArray.length() > 0) {
                                            jsonObject.put("data", zipJsonDataArray);
                                        }
                                        callback.queryComplete(jsonObject, videoModeMiniAppInfoObj.toString(),
                                                queryAdsInfo);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void updateError(Throwable t) {
                                ServiceQueryChainCallback callback = getQueryChainCallback();
                                if (callback != null) {
                                    callback.queryError(new Exception("chain ads down lua" +
                                            " failed"));
                                }
                            }
                        });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(luaFileInfoList);
                } catch (Exception e) {
                    VenvyLog.e(VideoServiceQueryChainModel.class.getName(), e);
                    ServiceQueryChainCallback callback = getQueryChainCallback();
                    if (callback != null) {
                        callback.queryError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoServiceQueryChainModel.class.getName(), e);
                ServiceQueryChainCallback callback = getQueryChainCallback();
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
        return HttpRequest.post(SERVICE_QUERYALL_CHAIN_URL_MOCK, createBody(mQueryAdsParams));
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


    private void loadDesktopProgram(String luaName, String miniAppInfo, String originData) {
        Bundle bundle = new Bundle();
        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_LUA_NAME, luaName);
        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_MINI_APP_INFO, miniAppInfo);
        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_TYPE, isTagMode ? "0" : "1");
        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_DATA, originData);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_LAUNCH_DESKTOP_PROGRAM, bundle);
    }


    public interface ServiceQueryChainCallback {
        void queryComplete(Object queryAdsData, String miniAppInfo, ServiceQueryAdsInfo queryAdsInfo);

        void queryError(Throwable t);
    }

    public void destroy() {
        VenvyAsyncTaskUtil.cancel(LUA_ZIP);
        if (mDownZipUpdate != null) {
            mDownZipUpdate.destroy();
        }
        if (mDownLuaUpdate != null) {
            mDownLuaUpdate.destroy();
        }
        if (mQueryAdsParams != null) {
            mQueryAdsParams.clear();
        }
        mQueryChainCallback = null;
    }
}
