package cn.com.venvy;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.common.utils.VenvyAPKVersionCodeUtils;
import cn.com.venvy.common.utils.VenvyDeviceUtil;

/**
 * Created by videojj_pls on 2019/8/30.
 */

public class CommonParam {
    private static final String VERSION = "VERSION";//版本号
    private static final String SDK_VERSION = "SDK_VERSION";//sdk版本号
    private static final String USER_AGENT = "USER_AGENT";//代理
    private static final String OS_VERSION = "OS_VERSION";//系统版本号
    private static final String UD_ID = "UD_ID";//uuid
        private static final String APP_KEY="APP_KEY";//app的key
    private static final String IP = "IP";//ip地址
    private static final String NETWORK = "NETWORK";//网络
    //    private static final String PLATFORM_ID="";//平台id
//    private static final String CYTRON_VERSION="";//业务系统版本号
    private static final String LANGUAGE = "LANGUAGE";//语言
    //    private static final String BU="OS";//业务id
//    private static final String ENCODING="";//编码
//    private static final String PLATFORM_TOKEN="";//平台token
    private static final String PHONE_MODEL = "PHONE_MODEL";//手机型号
    private static final String PHONE_PROVIDER = "PHONE_PROVIDER";//手机提供商

    public static JSONObject getCommonParamJson(String appKey) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(VERSION, VenvyAPKVersionCodeUtils.getVersionName(App.getContext()));
        paramMap.put(SDK_VERSION, Config.SDK_VERSION);
        paramMap.put(USER_AGENT, VenvyDeviceUtil.getUserAgent(App.getContext()));
        paramMap.put(OS_VERSION, VenvyDeviceUtil.getOsVersion());
        paramMap.put(UD_ID, VenvyDeviceUtil.getAndroidID(App.getContext()));
        String ip = VenvyDeviceUtil.getLocalIPAddress();
        if (!TextUtils.isEmpty(ip)) {
            paramMap.put(IP, ip);
        }
        paramMap.put(NETWORK, VenvyDeviceUtil.getNetWorkName(App.getContext()));
        paramMap.put(LANGUAGE, VenvyDeviceUtil.getLanguage(App.getContext()));
        paramMap.put(APP_KEY,appKey);
        paramMap.put(PHONE_MODEL, android.os.Build.MODEL);
        paramMap.put(PHONE_PROVIDER, android.os.Build.BRAND);
        return new JSONObject(paramMap);
    }
}
