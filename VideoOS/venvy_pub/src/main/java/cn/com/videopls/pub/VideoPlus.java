package cn.com.videopls.pub;

import android.content.Context;
import android.text.TextUtils;

import cn.com.venvy.App;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.download.DownloadDbHelper;
import cn.com.venvy.common.router.VenvyRouterManager;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.LuaHelper;


public class VideoPlus {

    private static boolean executed = false;

//    public static void appCreate(final Context context) {
//        App.setContext(context);
//        Config.SDK_VERSION = BuildConfig.SDK_VERSION;
//        Config.REPORT_ABLE = BuildConfig.ReportAble;
//        Config.DEBUG_STATUS = BuildConfig.DebugStatus;
//
//        if (executed) {
//            return;
//        }
//        executed = true;
//        LuaHelper.initLuaConfig();
//        PlatformInfo platformInfo = new PlatformInfo.Builder().builder();
//        Platform platform = new Platform(platformInfo);
//        new VideoPlusLuaUpdateModel(platform, null).startRequest();
//        VenvyRouterManager.getInstance().init(context, DebugStatus.isRelease() ? null : new VenvyRouterManager.RouterInitResult() {
//            @Override
//            public void initSuccess() {
//                VenvyLog.d(VideoPlus.class.getName(), "VenvyRouter init success");
//            }
//
//            @Override
//            public void initFailed() {
//
//            }
//        });
//        new DownloadDbHelper(context).deleteDownloadingInfo();
//    }

    public static void appCreateSAAS(final Context context, String appKey, String appSecret) {
        App.setContext(context);
        Config.SDK_VERSION = BuildConfig.SDK_VERSION;
        Config.REPORT_ABLE = BuildConfig.ReportAble;
        Config.DEBUG_STATUS = BuildConfig.DebugStatus;

        if (executed) {
            return;
        }
        executed = true;
        LuaHelper.initLuaConfig();
        if (!TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(appSecret)) {
            PlatformInfo platformInfo = new PlatformInfo.Builder().setAppKey(appKey).setAppSecret(appSecret).builder();
            Platform platform = new Platform(platformInfo);
            new VideoPlusLuaUpdateModel(platform, null).startRequest();
        }
        VenvyRouterManager.getInstance().init(context, DebugStatus.isRelease() ? null : new VenvyRouterManager.RouterInitResult() {
            @Override
            public void initSuccess() {
                VenvyLog.d(VideoPlus.class.getName(), "VenvyRouter init success");
            }

            @Override
            public void initFailed() {

            }
        });
        new DownloadDbHelper(context).deleteDownloadingInfo();
    }
}