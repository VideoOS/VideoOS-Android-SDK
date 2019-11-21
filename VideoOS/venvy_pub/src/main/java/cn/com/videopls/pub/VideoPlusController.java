package cn.com.videopls.pub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.cache.AppCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.VideoCopyLuaAssetsHelper;
import cn.com.venvy.common.debug.DebugHelper;
import cn.com.venvy.common.interf.ActionType;
import cn.com.venvy.common.interf.EventType;
import cn.com.venvy.common.interf.IAppletListener;
import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.router.PostInfo;
import cn.com.venvy.common.router.VenvyRouterManager;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.utils.VenvyAPIUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.lua.LuaHelper;
import cn.com.venvy.processor.annotation.VenvyAutoData;
import cn.com.videopls.pub.exception.DownloadException;

import static cn.com.venvy.common.interf.ServiceType.ServiceTypeVideoMode_TAG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_DATA;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MINI_APP_INFO;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MSG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_TYPE;

/**
 * Created by yanjiangbo on 2017/5/17.
 */

public abstract class VideoPlusController implements VenvyObserver {

    protected VideoProgramView mContentView;

    protected Platform mPlatform;

    protected IAppletListener mAppletListener;

    protected HashSet<ServiceQueryAdsInfo> mQueryAdsArray = new HashSet<>();

    private Context mContext;
    private VideoPlusAdapter mVideoPlusAdapter;
    private VideoPlusBaseModel mQueryAdsModel;
    private static final String MAIN_DEFAULT_ID = "main_default";

    public VideoPlusController(VideoProgramView videoPlusView) {
        mContext = videoPlusView.getContext();
        this.mContentView = videoPlusView;
        initDebugView(videoPlusView);
    }

    public void setAdapter(VideoPlusAdapter videoOSAdapter) {
        this.mVideoPlusAdapter = videoOSAdapter;
        VenvyRegisterLibsManager.registerConnectLib(videoOSAdapter.buildConnectProvider());
        VenvyRegisterLibsManager.registerImageLoaderLib(videoOSAdapter.buildImageLoader());
        VenvyRegisterLibsManager.registerImageSizeLib(videoOSAdapter.buildImageSize());
        VenvyRegisterLibsManager.registerWebViewLib(videoOSAdapter.buildWebView());
        VenvyRegisterLibsManager.registerImageViewLib(videoOSAdapter.buildImageView());
        VenvyRegisterLibsManager.registerSvgaImageView(videoOSAdapter.buildSvgaImageView());
        VenvyRegisterLibsManager.registerSocketConnect(videoOSAdapter.buildSocketConnect());
    }

    public void setAppletListener(IAppletListener appletListener) {
        this.mAppletListener = appletListener;
    }

    public void start() {
        if (!VenvyAPIUtil.isSupport(16)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("Video++ View 未设置adapter");
            return;
        }
        if (mContentView != null) {
            mContentView.setVisibility(View.VISIBLE);
        }
        this.mPlatform = initPlatform(mVideoPlusAdapter);
        startConnect(new IStartResult() {
            @Override
            public void successful() {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(VenvySchemeUtil.SCHEME_LUA_VIEW)
                        .path(VenvySchemeUtil.PATH_LUA_VIEW)
                        .appendQueryParameter("template", "main.lua")
                        .appendQueryParameter("id", MAIN_DEFAULT_ID);
                navigation(builder.build(), null, null);
            }

            @Override
            public void failed() {
                VenvyLog.e(VideoPlusController.class.getName(), "VideoOS start error");

            }
        });
    }

    /***
     * 开启Service
     * @param serviceType
     * @param params
     * @param callback
     */
    public void startService(final ServiceType serviceType, final HashMap<String, String> params,
                             final IServiceCallback callback) {
        if (!VenvyAPIUtil.isSupport(16)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("Video++ View 未设置adapter");
            return;
        }
        if (params == null || serviceType == null) {
            Log.e("Video++", "startService api 调用参数为空");
            return;
        }
        params.put(VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE, String.valueOf(serviceType.getId()));
        startQueryConnect(serviceType, params, new IStartQueryResult() {
            @Override
            public void successful(Object result, String miniAppInfo, final ServiceQueryAdsInfo queryAdsInfo) {
                if (queryAdsInfo == null) {
                    if (callback != null) {
                        callback.onFailToCompleteForService(new Exception("error query ads params" +
                                " is null"));
                    }
                    return;
                }
                mQueryAdsArray.add(queryAdsInfo);
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(VenvySchemeUtil.SCHEME_LUA_VIEW)
                        .path(VenvySchemeUtil.PATH_LUA_VIEW)
                        .appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_TEMPLATE,
                                queryAdsInfo.getQueryAdsTemplate())
                        .appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_ID,
                                queryAdsInfo.getQueryAdsId());


                HashMap<String, String> skipParams = new HashMap<>();
                // json data
                skipParams.put(CONSTANT_DATA, result.toString());

                // 视联网模式 启动模式（气泡、标签）.
                if (serviceType == ServiceType.ServiceTypeVideoMode_POP) {
                    skipParams.put(CONSTANT_VIDEO_MODE_TYPE, "1");
                } else if (serviceType == ServiceType.ServiceTypeVideoMode_TAG) {
                    skipParams.put(CONSTANT_VIDEO_MODE_TYPE, "0");
                }
                // miniAppInfo
                if (!TextUtils.isEmpty(miniAppInfo)) {
                    skipParams.put(CONSTANT_MINI_APP_INFO, miniAppInfo);
                }

                navigation(builder.build(), skipParams, new IRouterCallback() {
                    @Override
                    public void arrived() {
                        if (callback != null) {
                            callback.onCompleteForService();
                        }
                    }

                    @Override
                    public void lost() {
                        if (callback != null) {
                            callback.onFailToCompleteForService(new Exception("start startService" +
                                    " failed"));
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable throwable) {
                if (callback != null) {
                    callback.onFailToCompleteForService(throwable);
                }
            }
        });
        serviceTypeVideoModeTrack(serviceType, String.valueOf(1));
    }

    public void reResumeService(ServiceType serviceType) {
        if (serviceType == null) {
            return;
        }
        ArrayList<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
        if (queryAdsInfoArray == null || queryAdsInfoArray.size() <= 0) {
            return;
        }
        for (ServiceQueryAdsInfo queryAdsInfo : queryAdsInfoArray) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(VenvySchemeUtil.SCHEME_LUA_VIEW)
                    .path(VenvySchemeUtil.PATH_LUA_VIEW).appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_EVENT, eventService(serviceType, EventType.EventTypeAction, ActionType.EventTypeResume))
                    .appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_ID,
                            queryAdsInfo.getQueryAdsId());
            navigation(builder.build(), null, null);
        }
    }

    public void pauseService(ServiceType serviceType) {
        if (serviceType == null) {
            return;
        }
        ArrayList<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
        if (queryAdsInfoArray == null || queryAdsInfoArray.size() <= 0) {
            return;
        }
        for (ServiceQueryAdsInfo queryAdsInfo : queryAdsInfoArray) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(VenvySchemeUtil.SCHEME_LUA_VIEW)
                    .path(VenvySchemeUtil.PATH_LUA_VIEW).appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_EVENT, eventService(serviceType, EventType.EventTypeAction, ActionType.EventTypePause)).appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_ID, queryAdsInfo.getQueryAdsId());
            navigation(builder.build(), null, null);
        }
    }

    public void stopService(ServiceType serviceType) {
        serviceTypeVideoModeTrack(serviceType, String.valueOf(0));
        ArrayList<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
        if (queryAdsInfoArray == null || queryAdsInfoArray.size() <= 0) {
            return;
        }
        for (ServiceQueryAdsInfo queryAdsInfo : queryAdsInfoArray) {
            View tagView = mContentView.findViewWithTag(queryAdsInfo.getQueryAdsId());
            if (tagView != null) {
                mContentView.removeView(tagView);
                mQueryAdsArray.remove(queryAdsInfo);
            }
        }
    }

    public void stop() {
        unRegisterObservable();
        if (mQueryAdsModel != null) {
            mQueryAdsModel.destroy();
        }
        if (mQueryAdsArray != null) {
            mQueryAdsArray.clear();
        }
        if (mContentView != null) {
            mContentView.removeAllViews();
            mContentView.setVisibility(View.GONE);
        }
    }

    void destroy() {
        stop();
        AppCache.clear();
        LuaHelper.destroy();
        if (mPlatform != null) {
            mPlatform.onDestroy();
        }
        mPlatform = null;
    }


    @Override
    public void notifyChanged(VenvyObservable venvyObservable, String s, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (TextUtils.equals(s, VenvyObservableTarget.TAG_DATA_SET_CHANGED)) {
            Provider changedProvider = bundle.getParcelable("provider");
            if (changedProvider == null) {
                VenvyLog.e("provider can't be null,please check!");
                return;
            }
            notifyProviderChanged(changedProvider);
        } else if (TextUtils.equals(s, VenvyObservableTarget.TAG_SCREEN_CHANGED)) {
            ScreenStatus status = (ScreenStatus) bundle.getSerializable("screen_changed");
            if (mPlatform != null && mPlatform.getPlatformInfo() != null) {
                mPlatform.getPlatformInfo().updateDirection(status);
            }
        }
    }

    protected VideoPlusAdapter getVideoPlusAdapter() {
        return mVideoPlusAdapter;
    }


    protected Context getContext() {
        return mContext;
    }

    protected Platform initPlatform(VideoPlusAdapter videoPlusAdapter) {
        Platform platform = new Platform(initPlatformInfo(videoPlusAdapter.createProvider()));
        platform = updatePlatformListener(platform, getVideoPlusAdapter());
        return platform;
    }

    protected Platform updatePlatformListener(Platform platform, VideoPlusAdapter videoOSAdapter) {
        platform.setWidgetShowListener(videoOSAdapter.buildWidgetShowListener());
        platform.setWidgetClickListener(videoOSAdapter.buildWidgetClickListener());
        platform.setWidgetCloseListener(videoOSAdapter.buildWidgetCloseListener());
        platform.setMediaControlListener(videoOSAdapter.buildMediaController());
        platform.setPlatformLoginInterface(videoOSAdapter.buildLoginInterface());
        platform.setTagKeyListener(videoOSAdapter.buildOttKeyListener());
        platform.setWedgeListener(videoOSAdapter.buildWedgeListener());
        if (mAppletListener != null) {
            platform.setAppletListener(mAppletListener);
        }
        platform.setWidgetPrepareShowListener(videoOSAdapter.buildWidgetPrepareShowListener());
        platform.setContentViewGroup(mContentView);
        return platform;
    }

    protected PlatformInfo initPlatformInfo(Provider provider) {
        if (provider == null) {
            return null;
        }
        PlatformInfo.Builder platformInfoBuilder = new PlatformInfo.Builder()
                .setVideoId(provider.getVideoPath())
                .setThirdPlatform(provider.getPlatformId())
                .setVideoWidth(provider.getHorVideoWidth())
                .setVideoHeight(provider.getHorVideoHeight())
                .setVerVideoWidth(provider.getVerVideoWidth())
                .setVerVideoHeight(provider.getVerVideoHeight())
                .setIdentity(provider.getCustomUDID())
                .setCustomerPackageName(provider.getPackageName())
                .setInitDirection(provider.getDirection())
                .setVideoType(provider.getVideoType())
                .setVideoCategory(provider.getVideoCategory())
                .setExtendJSONString(provider.getExtendJSONString())
                .setAppKey(provider.getAppKey()).setAppSecret(provider.getAppSecret());
        return platformInfoBuilder.builder();
    }


    protected void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        if (!VenvyAPIUtil.isSupport(14)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mContentView == null) {
            VenvyLog.e("VideoOS", "Video++ View 不能为null");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("VideoOS", "Video++ View 未设置adapter");
            return;
        }

        if (mPlatform == null) {
            mPlatform = initPlatform(mVideoPlusAdapter);
        }

        PostInfo postInfo = VenvyRouterManager.getInstance().setUri(uri)
                .withTargetPlatform("platform", mPlatform)
                .withTargetViewParent(mContentView);
        Set<String> uriQueryParameterNames = uri.getQueryParameterNames();
        HashMap<String, String> targetDataMap = null;
        if (uriQueryParameterNames != null && uriQueryParameterNames.size() > 0) {
            for (String key : uriQueryParameterNames) {
                String value = uri.getQueryParameter(key);
                if (!TextUtils.isEmpty(value)) {
                    if (targetDataMap == null) {
                        targetDataMap = new HashMap<>();
                    }
                    targetDataMap.put(key, value);
                }
            }
        }
        if (params != null) {
            String value = params.get("data");
            if (!TextUtils.isEmpty(value)) {
                if (targetDataMap == null) {
                    targetDataMap = new HashMap<>();
                }
                targetDataMap.put("data", value);
            }
        }
        if (targetDataMap != null) {
            postInfo.withSerializable("data", targetDataMap);
        }
        postInfo.navigation(mContentView.getContext(), callback);
    }

    protected void registerObservable() {
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_DATA_SET_CHANGED, this);
    }

    protected void unRegisterObservable() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_DATA_SET_CHANGED, this);
    }

    protected void notifyProviderChanged(Provider changedProvider) {
        if (mPlatform != null) {
            mPlatform.updatePlatformInfo(initPlatformInfo(changedProvider));
        }
    }


    private void initDebugView(ViewGroup viewGroup) {
        DebugHelper.addDebugLayout(viewGroup);
    }

    private void startConnect(final IStartResult result) {
        //Copy本地Lua逻辑处理
        VideoCopyLuaAssetsHelper.getInstance().start(new VideoCopyLuaAssetsHelper.LuaCopyCallback() {
            @Override
            public void copyComplete() {
                if (result != null) {
                    result.successful();
                }
                registerObservable();
            }

            @Override
            public void copyError(Throwable throwable) {
                VenvyLog.e(VideoPlusController.class.getName(), "copy lua 失败, : " + throwable
                        .getMessage());
                if (result != null) {
                    result.failed();
                }
                registerObservable();
            }
        });
    }

    private void startQueryConnect(ServiceType serviceType, final Map<String, String> params, final IStartQueryResult result) {
        switch (serviceType) {
            case ServiceTypeVideoMode_TAG:
            case ServiceTypeVideoMode_POP:
                // 视联网模式
                mQueryAdsModel = new VideoServiceQueryChainModel(mPlatform, params, serviceType == ServiceTypeVideoMode_TAG, new VideoServiceQueryChainModel.ServiceQueryChainCallback() {
                    @Override
                    public void queryComplete(Object queryAdsData, String miniAppInfo, ServiceQueryAdsInfo queryAdsInfo) {
                        if (result != null) {
                            result.successful(queryAdsData, miniAppInfo, queryAdsInfo);
                        }
                    }

                    @Override
                    public void queryError(Throwable throwable) {
                        if (result != null) {
                            result.failed(throwable);
                        }
                    }
                });
                break;
            case ServiceTypeVideoTools:

                // 视联网小工具
                mQueryAdsModel = new VideoServiceToolsModel(mPlatform, params, new VideoServiceToolsModel.VisionProgramToolsCallback() {
                    @Override
                    public void downComplete(String entranceLua, String miniAppInfo) {
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme(VenvySchemeUtil.SCHEME_LUA_VIEW)
                                .path(VenvySchemeUtil.PATH_LUA_VIEW)
                                .appendQueryParameter(VenvySchemeUtil.QUERY_PARAMETER_TEMPLATE,
                                        entranceLua);


                        JSONObject paramsJson = new JSONObject();

                        try {
                            paramsJson.put("data", new JSONObject(params.get("data")));
                            paramsJson.put("miniAppInfo", new JSONObject(miniAppInfo));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        HashMap<String, String> finalParams = new HashMap<>();
                        finalParams.put("data", paramsJson.toString());
                        navigation(builder.build(), finalParams, new IRouterCallback() {
                            @Override
                            public void arrived() {
//                                if (callback != null) {
//                                    callback.onCompleteForService();
//                                }
                            }

                            @Override
                            public void lost() {
                                VenvyLog.e("视联网小工具启动失败");
                            }
                        });
                    }

                    @Override
                    public void downError(Throwable t) {
                        if (result != null) {
                            result.failed(t);
                        }
                    }
                });
                break;
            default:
                // 前后暂停贴广告
                if (mPlatform == null) {
                    mPlatform = initPlatform(mVideoPlusAdapter);
                }
                mQueryAdsModel = new VideoServiceQueryAdsModel(mPlatform, params,
                        new VideoServiceQueryAdsModel.ServiceQueryAdsCallback() {

                            @Override
                            public void queryComplete(Object queryAdsData, ServiceQueryAdsInfo queryAdsInfo) {
                                if (result != null) {
                                    result.successful(queryAdsData, "", queryAdsInfo);
                                }
                            }

                            @Override
                            public void queryError(Throwable throwable) {
                                if (result != null) {
                                    result.failed(throwable);
                                }
                            }
                        });
                break;
        }
        mQueryAdsModel.startRequest();
    }

    protected void closeInfoView() {
        if (mContentView == null) {
            return;
        }
        int childCount = mContentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mContentView.getChildAt(i);
            if (childView != null) {
                int priority = getViewPriority(childView);
                if (priority == 2) {
                    mContentView.removeView(childView);
                }
            }
        }
    }


    private int getViewPriority(View view) {
        try {
            if (view != null) {
                Field field = view.getClass().getDeclaredField("priority");
                if (field != null && field.getAnnotation(VenvyAutoData.class) != null) {
                    field.setAccessible(true);
                    Object targetPriority = field.get(view);
                    if (targetPriority != null && targetPriority instanceof String) {
                        return Integer.valueOf((String) targetPriority);
                    }
                }
            }
        } catch (Exception e) {
            //忽略此处异常
        }
        return 0;
    }

    /**
     * 移除最上层的childView
     */
    protected void removeTopChild() {
        if (mContentView == null) {
            return;
        }
        int childCount = mContentView.getChildCount();
        if (childCount > 0) {
            mContentView.removeViewAt(childCount - 1);
        }
    }


    private ArrayList<ServiceQueryAdsInfo> getRunningService(ServiceType serviceType) {
        ArrayList<ServiceQueryAdsInfo> queryAdsInfoArray = new ArrayList<>();
        for (ServiceQueryAdsInfo queryAdsInfo : mQueryAdsArray) {
            if (queryAdsInfo.getQueryAdsType() == serviceType.getId()) {
                queryAdsInfoArray.add(queryAdsInfo);
            }
        }
        return queryAdsInfoArray;
    }

    private String eventService(ServiceType serviceType, EventType eventType,
                                ActionType actionType) {
        HashMap<String, String> eventParams = new HashMap<>();
        eventParams.put(VenvySchemeUtil.QUERY_PARAMETER_EVENT_TYPE,
                String.valueOf(eventType.getId()));
        eventParams.put(VenvySchemeUtil.QUERY_PARAMETER_ACTION_TYPE,
                String.valueOf(actionType.getId()));
        return new JSONObject(eventParams).toString();
    }

    /***
     *
     * @param serviceType
     * @param onOrOff 0:关闭 1:开启
     */
    private void serviceTypeVideoModeTrack(ServiceType serviceType, String onOrOff) {
        if (serviceType != ServiceType.ServiceTypeVideoMode_POP && serviceType != ServiceTypeVideoMode_TAG) {
            return;
        }
        VenvyStatisticsManager.getInstance().submitVisualSwitchStatisticsInfo(onOrOff);
    }

    public void startVisionProgram(final String appletId, final String data, final int type, final boolean isH5Type, final IRouterCallback callback) {
        if (!VenvyAPIUtil.isSupport(16)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("Video++ View 未设置adapter");
            return;
        }
        if (mContentView != null) {
            mContentView.setVisibility(View.VISIBLE);
        }
        this.mPlatform = initPlatform(mVideoPlusAdapter);
        VisionProgramConfigModel model = new VisionProgramConfigModel(mPlatform, appletId, isH5Type, new VisionProgramConfigModel.VisionProgramConfigCallback() {

            @Override
            public void downComplete(final String entranceLua, boolean isUpdateByNet, boolean nvgShow) {
                VenvyLog.d("vision program downComplete : " + isUpdateByNet + "   - " + entranceLua);
                mPlatform.setNvgShow(false);
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        String luaId = entranceLua;
                        if (entranceLua.contains(".")) {
                            luaId = entranceLua.split("\\.")[0];
                        }
                        //LuaView://applets?appletId=xxxx&template=xxxx.lua&id=xxxx&(priority=x)
                        Uri uri = Uri.parse("LuaView://applets?appletId=" + appletId + "&template=" + entranceLua + "&id=" + luaId);
                        HashMap<String, String> params = new HashMap<>();
                        params.put("data", data);
                        mContentView.navigation(uri, params, callback);
                    }
                });

            }

            @Override
            public void downError(Throwable t) {
                VenvyLog.e("getMiniAppConf downError");
                //网络不通，请求不到小程序内容 | 网络请求错误，为底层通讯错误如,404,500等
                Bundle bundle = new Bundle();
                if (t instanceof DownloadException) {
                    bundle.putString(CONSTANT_MSG,
                            getContext().getString(
                                    VenvyResourceUtil.getStringId(getContext(), "loadMiniAppError")));
                } else {
                    bundle.putString(CONSTANT_MSG,
                            getContext().getString(
                                    VenvyResourceUtil.getStringId(getContext(), "networkBusy")));
                }

                bundle.putBoolean(CONSTANT_NEED_RETRY, true);
                bundle.putString(CONSTANT_DATA, data);


                ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);
            }
        });
        model.startRequest();
    }

    /**
     * 更新最近使用记录
     *
     * @param appId
     */
    public void refreshRecentHistory(String appId) {
        if (!VenvyAPIUtil.isSupport(16)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("Video++ View 未设置adapter");
            return;
        }
        this.mPlatform = initPlatform(mVideoPlusAdapter);
        new VideoRecentlyModel(mPlatform, appId).startRequest();
    }
}
