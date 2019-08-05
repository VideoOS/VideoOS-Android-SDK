package cn.com.videopls.pub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.debug.DebugHelper;
import cn.com.venvy.common.interf.ActionType;
import cn.com.venvy.common.interf.EventType;
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
import cn.com.venvy.common.utils.VenvyAPIUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.lua.LuaHelper;
import cn.com.venvy.processor.annotation.VenvyAutoData;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by yanjiangbo on 2017/5/17.
 */

public abstract class VideoPlusController implements VenvyObserver {

    protected VideoPlusView mContentView;

    protected Platform mPlatform;

    protected HashSet<ServiceQueryAdsInfo> mQueryAdsArray = new HashSet<>();

    private Context mContext;
    private VideoPlusAdapter mVideoPlusAdapter;
    private VideoPlusLuaUpdateModel mLuaUpdateModel;
    private VideoPlusBaseModel mQueryAdsModel;
    private static final String MAIN_DEFAULT_ID = "main_default";

    public VideoPlusController(VideoPlusView videoPlusView) {
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
    public void startService(ServiceType serviceType, final HashMap<String, String> params,
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
            public void successful(Object result, final ServiceQueryAdsInfo queryAdsInfo) {
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
                skipParams.put("data", result.toString());
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
    }

    public void reResumeService(ServiceType serviceType) {
        if (serviceType == null) {
            return;
        }
        List<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
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
        List<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
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
        List<ServiceQueryAdsInfo> queryAdsInfoArray = getRunningService(serviceType);
        if (queryAdsInfoArray == null || queryAdsInfoArray.size() <= 0) {
            return;
        }
        for (ServiceQueryAdsInfo queryAdsInfo : queryAdsInfoArray) {
            View tagView = mContentView.findViewWithTag(queryAdsInfo.getQueryAdsId());
            if (tagView != null) {
                mContentView.removeView(tagView);
                queryAdsInfoArray.remove(queryAdsInfo);
            }
        }
    }

    public void stop() {
        unRegisterObservable();
        if (mLuaUpdateModel != null) {
            mLuaUpdateModel.destroy();
        }
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

        //开始访问Lua增量更新接口
        mLuaUpdateModel = new VideoPlusLuaUpdateModel(mPlatform,
                new VideoPlusLuaUpdateModel.LuaUpdateCallback() {
                    @Override
                    public void updateComplete(boolean isUpdateByNetwork) {
                        if (isUpdateByNetwork) {
                            VenvyLog.d(VideoPlusController.class.getName(), "lua 在线更新成功");
                            //如果是在线更新的版本，需要强制更新lua路径地址
                            VideoOSLuaView.destroyLuaScript();
                        } else {
                            VenvyLog.d(VideoPlusController.class.getName(), "lua 校验版本成功");
                        }
                        if (result != null) {
                            result.successful();
                        }
                        registerObservable();
                    }

                    @Override
                    public void updateError(Throwable throwable) {
//                VenvyLog.e(VideoPlusController.class.getName(), "lua 更新失败, : " + throwable
//                .getMessage());
                        if (result != null) {
                            result.failed();
                        }
                        registerObservable();
                    }
                });
        mLuaUpdateModel.startRequest();
    }

    private void startQueryConnect(ServiceType serviceType, Map<String, String> params, final IStartQueryResult result) {
        switch (serviceType) {
            case ServiceTypeVideoMode:
                mQueryAdsModel = new VideoServiceQueryChainModel(mPlatform, params, new VideoServiceQueryChainModel.ServiceQueryChainCallback() {
                    @Override
                    public void queryComplete(Object queryAdsData, ServiceQueryAdsInfo queryAdsInfo) {
                        if (result != null) {
                            result.successful(queryAdsData, queryAdsInfo);
                        }
                    }

                    @Override
                    public void queryError(Throwable throwable) {
                        Log.i("video++","=start=queryError===="+throwable.getMessage());
                        if (result != null) {
                            result.failed(throwable);
                        }
                    }
                });
                break;
            default:
                mQueryAdsModel = new VideoServiceQueryAdsModel(mPlatform, params,
                        new VideoServiceQueryAdsModel.ServiceQueryAdsCallback() {

                            @Override
                            public void queryComplete(Object queryAdsData, ServiceQueryAdsInfo queryAdsInfo) {
                                if (result != null) {
                                    result.successful(queryAdsData, queryAdsInfo);
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

    private List<ServiceQueryAdsInfo> getRunningService(ServiceType serviceType) {
        List<ServiceQueryAdsInfo> queryAdsInfoArray = new ArrayList<>();
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
}
