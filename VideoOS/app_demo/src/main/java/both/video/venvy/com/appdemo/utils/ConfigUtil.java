package both.video.venvy.com.appdemo.utils;

import android.content.Context;
import android.text.TextUtils;

import both.video.venvy.com.appdemo.MyApp;
import both.video.venvy.com.appdemo.bean.OsConfigureBean;
import both.video.venvy.com.appdemo.bean.SettingsBean;
import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.utils.VenvyPreferenceHelper;

/**
 * Create by bolo on 08/06/2018
 */
public class ConfigUtil {

    public static final String CONFIG_FILE_NAME = "config_name";

    public static final String CONFIG_APPKEY_TAG = "AppKey";
    public static final String CONFIG_APPSECRET_TAG = "AppSecret";
    public static final String CONFIG_VIDEO_ID = "videoId";
    public static final String CONFIG_VIDEO_NAME = "videoName";

    // 正式环境appKey&appSecret (ps : 只有这一对)
    public static final String RELEASE_APP_KEY = "ae83ec93-c3fa-493a-a0f2-ce11765f4616";
    public static final String RELEASE_APP_SECRET = "72a46e7bcdb44efb";

    private static final String TEST_APP_KEY = "eed99d68-6f1d-403a-a9f4-0583cccc20e9";
    private static final String TEST_APP_SECRET = "941f336cf231470b";
    public static final String DEFAULT_APPKEY = TEST_APP_KEY;
    public static final String DEFAULT_APPSECRET = TEST_APP_SECRET;

    public static final String DEFAULT_VIDEO_ID = "d67eba2a3e549b75e42746b79721d0bf";


    public static final String SP_DEMO_CONFIG = "sp_demo_config";
    private static final String SP_DEMO_CONFIG_UID = "u_id";
    private static final String SP_DEMO_CONFIG_ROOMID = "room_id";
    private static final String SP_DEMO_CONFIG_PLATFORMID = "platform_id";
    private static final String SP_DEMO_CONFIG_CATE = "cate";
    private static final String SP_DEMO_CONFIG_APPID = "app_id";
    private static final String SP_DEMO_CONFIG_VIDEOPATH = "video_path";
    public static final String SP_DEMO_CONFIG_STATUS = "status";
    private static final String SP_DEMO_CONFIG_SCREEN_STATUS = "screen_status";
    private static final String SP_DEMO_CONFIG_USER_TYPE = "user_type";
    private static final String SP_DEMO_CONFIG_IS_FULL_SCREEN = "is_full_screen";
    private static final String SP_DEMO_CONFIG_VERTICAL_HEIGHT = "vertical_height";
    private static final String SP_DEMO_CONFIG_IS_USER_APP = "is_user_app";


    public static void saveConfig(Context context, SettingsBean bean) {
        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_SCREEN_STATUS,
                bean.mScreenStatus.getId());

        if (bean.mUserType != null) {
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_USER_TYPE,
                    bean.mUserType == PlatformUserInfo.UserType.Consumer ? 0 : 1);
        }

        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_IS_USER_APP,
                bean.isUserApp);

        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_IS_FULL_SCREEN, bean
                .isFullScreen);


        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_VERTICAL_HEIGHT,
                bean.mVerticalHeight);


        if (!TextUtils.isEmpty(bean.mUid))
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_UID, bean
                    .mUid);

        if (!TextUtils.isEmpty(bean.mRoomId))
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_ROOMID,
                    bean.mRoomId);

        if (!TextUtils.isEmpty(bean.mPlatformId))
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_PLATFORMID,
                    bean.mPlatformId);

        if (!TextUtils.isEmpty(bean.mAppkey))
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_APPID,
                    bean.mAppkey);

        if (!TextUtils.isEmpty(bean.mUrl))
            VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_VIDEOPATH,
                    bean.mUrl);

        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_CATE, bean
                .mCate);

        VenvyPreferenceHelper.put(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_STATUS,
                bean.mStatus.getEnvironmentValue());
    }

    public static SettingsBean getSettingCache(Context context) {
        return getSettingCache(context, null);
    }

    public static SettingsBean getSettingCache(Context context, OsConfigureBean data) {
        SettingsBean bean = new SettingsBean();

        // debutStatus
        int debugStatus;
        if (data != null) {
            debugStatus = data.debugStatus;
        } else {
            debugStatus = VenvyPreferenceHelper.getInt(context, SP_DEMO_CONFIG,
                    SP_DEMO_CONFIG_STATUS);
        }
        DebugStatus.changeEnvironmentStatus(
                DebugStatus.EnvironmentStatus.getStatusByIntType(debugStatus));

        int screenStatus = VenvyPreferenceHelper.getInt(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_SCREEN_STATUS);
        bean.mScreenStatus = ScreenStatus.getStatusById(screenStatus);

        // userType
        int type = VenvyPreferenceHelper.getInt(context, SP_DEMO_CONFIG, SP_DEMO_CONFIG_USER_TYPE);
        bean.mUserType = type == 0 ? PlatformUserInfo.UserType.Consumer : PlatformUserInfo
                .UserType.Anchor;

        bean.isUserApp = VenvyPreferenceHelper.getBoolean(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_IS_USER_APP);

        // isFullScreen
        bean.isFullScreen = VenvyPreferenceHelper.getBoolean(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_IS_FULL_SCREEN);

        // mVerticalHeight
        bean.mVerticalHeight = VenvyPreferenceHelper.getInt(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_VERTICAL_HEIGHT);


        bean.mUid = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_UID, "");
        bean.mRoomId = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_ROOMID, "");
        bean.mPlatformId = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_PLATFORMID, "");
        bean.mCate = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_CATE, "");
        bean.mAppkey = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_APPID, "");
        bean.mUrl = VenvyPreferenceHelper.getString(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_VIDEOPATH, "");
        return bean;
    }

    public static OsConfigureBean getConfig(Context context, OsConfigureBean data) {
        return getConfig(context, data, null);
    }

    public static OsConfigureBean getConfig(Context context, OsConfigureBean data, SettingsBean
            bean) {
        if (data == null) {
            return data;
        }

        data.debugStatus = VenvyPreferenceHelper.getInt(context, SP_DEMO_CONFIG,
                SP_DEMO_CONFIG_STATUS);
        DebugStatus.changeEnvironmentStatus(
                DebugStatus.EnvironmentStatus.getStatusByIntType(data.debugStatus));

        if (bean == null) {
            bean = getSettingCache(context, data);
        }

        if (data.cateList != null && data.cateList.contains(bean.mCate)) {
            data.cateList.remove(bean.mCate);
        }
        data.cateList.add(0, bean.mCate);

        if (data.userIdList != null && data.userIdList.contains(bean.mUid)) {
            data.userIdList.remove(bean.mUid);
        }
        data.userIdList.add(0, bean.mUid);

        if (data.roomIdList != null && data.roomIdList.contains(bean.mRoomId)) {
            data.roomIdList.remove(bean.mRoomId);
        }
        data.roomIdList.add(0, bean.mRoomId);

        if (data.platformIdList != null && data.platformIdList.contains(bean.mPlatformId)) {
            data.platformIdList.remove(bean.mPlatformId);
        }
        data.platformIdList.add(0, bean.mPlatformId);

        return data;
    }


    public static void putAppKey(String appKey) {
        if (TextUtils.isEmpty(appKey)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPKEY_TAG, appKey);
    }

    public static String getAppKey() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPKEY_TAG, DEFAULT_APPKEY);
    }

    public static void putAppSecret(String appSecret) {
        if (TextUtils.isEmpty(appSecret)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPSECRET_TAG, appSecret);
    }

    public static String getAppSecret() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPSECRET_TAG, DEFAULT_APPSECRET);
    }

    public static String getVideoId() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_ID, DEFAULT_VIDEO_ID);
    }

    public static void putVideoId(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_ID, videoId);
    }

    public static String getVideoName(){
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_NAME, "");
    }

    public static void putVideoName(String videoName){
        if (TextUtils.isEmpty(videoName)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_NAME, videoName);
    }
}
