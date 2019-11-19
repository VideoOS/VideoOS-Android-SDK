package both.video.venvy.com.appdemo.utils;

import android.text.TextUtils;

import both.video.venvy.com.appdemo.MyApp;
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

    public static final String CONFIG_INTERACT_LOCAL_LUAPATH = "interact_local_luaPath";
    public static final String CONFIG_INTERACT_LOCAL_JSONPATH = "interact_local_jsonPath";
    public static final String CONFIG_INTERACT_LOCAL_VIDEOPATH = "interact_local_videoPath";

    public static final String CONFIG_INTERACT_ONLINE_COMMITID = "interact_online_commitId";
    public static final String CONFIG_INTERACT_ONLINE_JSONURL = "interact_online_jsonUrl";
    public static final String CONFIG_INTERACT_ONLINE_VIDEOURL = "interact_online_videoURL";

    public static final String CONFIG_SERVICE_LOCAL_JSONPATH = "service_local_jsonPath";
    public static final String CONFIG_SERVICE_LOCAL_VIDEOPATH = "service_local_videoPath";

    public static final String CONFIG_SERVICE_ONLINE_COMMITID = "service_online_commitId";
    public static final String CONFIG_SERVICE_ONLINE_VIDEOURL = "service_online_videoUrl";

    public static final String DEFAULT_VIDEO_URL = "https://videojj-mobile.oss-cn-beijing.aliyuncs.com/resource/test/SwordArtOnlineAlicization22.mp4";

    public static void putServiceOnLineVideoUrl(String serviceOnLineVideoUrl) {
        if (TextUtils.isEmpty(serviceOnLineVideoUrl)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_ONLINE_VIDEOURL, serviceOnLineVideoUrl);
    }

    public static String getServiceOnLineVideoUrl() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_ONLINE_VIDEOURL, "");
    }

    public static void putServiceOnLineCommitId(String serviceOnLineCommitId) {
        if (TextUtils.isEmpty(serviceOnLineCommitId)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_ONLINE_COMMITID, serviceOnLineCommitId);
    }

    public static String getServiceOnLineCommitId() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_ONLINE_COMMITID, "");
    }

    public static void putServiceLocalVideoPath(String serviceLocalVideoPath) {
        if (TextUtils.isEmpty(serviceLocalVideoPath)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_LOCAL_VIDEOPATH, serviceLocalVideoPath);
    }

    public static String getServiceLocalVideoPath() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_LOCAL_VIDEOPATH, "");
    }

    public static void putServiceLocalJsonPath(String serviceLocalJsonPath) {
        if (TextUtils.isEmpty(serviceLocalJsonPath)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_LOCAL_JSONPATH, serviceLocalJsonPath);
    }

    public static String getServiceLocalJsonPath(){
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_SERVICE_LOCAL_JSONPATH, "");
    }

    public static void putInteractOnLineVideoUrl(String interactOnLineVideoUrl) {
        if (TextUtils.isEmpty(interactOnLineVideoUrl)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_VIDEOURL, interactOnLineVideoUrl);
    }

    public static String getInteractOnLineVideoUrl() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_VIDEOURL, "");
    }

    public static void putInteractOnLineJsonUrl(String interactOnLineJsonUrl) {
        if (TextUtils.isEmpty(interactOnLineJsonUrl)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_JSONURL, interactOnLineJsonUrl);
    }

    public static String getInteractOnLineJsonUrl() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_JSONURL, "");
    }

    public static void putInteractOnLineCommitId(String interactOnLineCommitId) {
        if (TextUtils.isEmpty(interactOnLineCommitId)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_COMMITID, interactOnLineCommitId);
    }

    public static String getInteractOnLineCommitId() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_ONLINE_COMMITID, "");
    }

    public static void putInteractLocalVideoPath(String interactLocalVideoPath) {
        if (TextUtils.isEmpty(interactLocalVideoPath)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_VIDEOPATH, interactLocalVideoPath);
    }

    public static String getInteractLocalVideoPath() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_VIDEOPATH, "");
    }

    public static void putInteractLocalJsonPath(String interactLocalJsonPath) {
        if (TextUtils.isEmpty(interactLocalJsonPath)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_JSONPATH, interactLocalJsonPath);
    }

    public static String getInteractLocalJsonPath() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_JSONPATH, "");
    }

    public static void putInteractLocalLuaPath(String interactLocalLuaPath) {
        if (TextUtils.isEmpty(interactLocalLuaPath)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_LUAPATH, interactLocalLuaPath);
    }

    public static String getInteractLocalLuaPath(){
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_INTERACT_LOCAL_LUAPATH, "");
    }

    public static void putAppKey(String appKey) {
        if (TextUtils.isEmpty(appKey)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPKEY_TAG, appKey);
    }

    public static String getAppKey() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPKEY_TAG, "");
    }

    public static void putAppSecret(String appSecret) {
        if (TextUtils.isEmpty(appSecret)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPSECRET_TAG, appSecret);
    }

    public static String getAppSecret() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_APPSECRET_TAG, "");
    }

    public static String getVideoId() {
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_ID, "");
    }

    public static void putVideoId(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_ID, videoId);
    }

    public static String getVideoName(){
        return VenvyPreferenceHelper.getString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_NAME, DEFAULT_VIDEO_URL);
    }

    public static void putVideoName(String videoName){
        if (TextUtils.isEmpty(videoName)) {
            return;
        }
        VenvyPreferenceHelper.putString(MyApp.getInstance(), CONFIG_FILE_NAME, CONFIG_VIDEO_NAME, videoName);
    }
}
