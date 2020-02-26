package cn.com.videopls.pub;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import cn.com.venvy.CacheConstants;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.utils.VenvyAPIUtil;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
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
        VenvyRegisterLibsManager.registerACRCloud(videoOSAdapter.buildACRCloud());
    }

    protected VideoPlusAdapter getVideoPlusAdapter() {
        return mVideoPlusAdapter;
    }

    public Platform getPlatform() {
        if(mPlatform == null){
            this.mPlatform = initPlatform(mVideoPlusAdapter);
        }
        return mPlatform;
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
        platform.setPlatformRecordInterface(videoOSAdapter.buildRecordInterface());
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
                .setExtendDict(provider.getExtendDict())
                .setAppKey(provider.getAppKey()).setAppSecret(provider.getAppSecret());
        return platformInfoBuilder.builder();
    }

    public void startH5Program(final String appletId){
        this.mPlatform = initPlatform(mVideoPlusAdapter);
        new VisionProgramConfigModel(mPlatform, appletId,true, null).startRequest();
    }



    /**
     * 更新最近使用记录
     * @param appId
     */
    public void refreshRecentHistory(String appId){
        if (!VenvyAPIUtil.isSupport(16)) {
            Log.e("VideoOS", "VideoOS 不支持Android4.0以下版本调用");
            return;
        }
        if (mVideoPlusAdapter == null) {
            VenvyLog.e("Video++ View 未设置adapter");
            return;
        }
        this.mPlatform = initPlatform(mVideoPlusAdapter);

        String key;
        if (mPlatform != null && mPlatform.getPlatformInfo() != null && !TextUtils.isEmpty(mPlatform.getPlatformInfo().getIdentity())) {
            key = mPlatform.getPlatformInfo().getIdentity();
        } else {
            key = VenvyDeviceUtil.getAndroidID(mContext);
        }
        CacheConstants.putVisionProgramId(mContext, key, appId); // 将id保存到本地
//        new VideoRecentlyModel(mPlatform,appId).startRequest();
    }



}
