package cn.com.videopls.pub;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Set;

import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.router.PostInfo;
import cn.com.venvy.common.router.VenvyRouterManager;
import cn.com.venvy.common.utils.VenvyAPIUtil;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/8/30.
 */
public class VideoPlusH5Controller {

    private Context mContext;
    private VideoPlusAdapter mVideoPlusAdapter;
    private Platform mPlatform;
    private ViewGroup mContentView;
    public VideoPlusH5Controller(Context mContext,VideoWebToolBarView view) {
        this.mContext = mContext;
        mContentView = view;
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

    protected VideoPlusAdapter getVideoPlusAdapter() {
        return mVideoPlusAdapter;
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

    public void startH5Program(final String appletId){
        this.mPlatform = initPlatform(mVideoPlusAdapter);
        new VisionProgramConfigModel(mPlatform, appletId,true, null).startRequest();
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


}
