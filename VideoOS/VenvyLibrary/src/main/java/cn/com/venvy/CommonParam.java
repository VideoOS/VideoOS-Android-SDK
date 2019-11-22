package cn.com.venvy;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.common.utils.VenvyAPKVersionCodeUtils;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by videojj_pls on 2019/8/30.
 */

public class CommonParam {
    private static final String LANGUAGE = "LANGUAGE";//语言
    private static final String SDK_VERSION = "SDK_VERSION";//sdk版本号
    private static final String UD_ID = "UD_ID";//uuid
    private static final String PHONE_MODEL = "PHONE_MODEL";//手机型号
    private static final String NETWORK = "NETWORK";//网络连接类型（0:未知，1:wifi，2:2G，3:3G，4:4G 5:5G）network
    private static final String VERSION = "VERSION";//APP版本（如：9.3.4）使用version
    private static final String OS_VERSION = "OS_VERSION";//系统版本号
    private static final String PHONE_PROVIDER = "PHONE_PROVIDER";//手机提供商
    private static final String IP = "IP";//ip地址
    private static final String OS_TYPE = "OS_TYPE";//0:未知 1:Android 2:iOS 3:Windows Phone
    private static final String MAC = "MAC";
    private static final String ANDROID_ID = "ANDROID_ID";//android_id
    private static final String IMEI = "IMEI";
    private static final String APP_NAME = "APP_NAME";//APP名称(utf-8)，需要UrlEncode
    private static final String PKG_NAME = "PKG_NAME";//APP包名(安卓是应用的PackageName,ios是Bundle ID)
    private static final String CARRIER = "CARRIER";//运营商信息 0:其他，1:移动，2:联通，3:电信
    private static final String PHONE_HEIGHT = "PHONE_HEIGHT";//设备屏幕宽度,物理像素
    private static final String PHONE_WIDTH = "PHONE_WIDTH";//设备屏幕高度,物理像素
    private static final String PPI = "PPI";//设备像素密度,物理像素
    private static final String IMSI = "IMSI";//国际移动客户识别码
    private static final String DEVICE_TYPE = "DEVICE_TYPE";//终端类型(1:移动端 2:PC 3:OTT)

    public static JSONObject getCommonParamJson(String appKey) {
        Map<String, String> paramMap = new HashMap<>();
        Context context = App.getContext();
        paramMap.put(VERSION, VenvyAPKVersionCodeUtils.getVersionName(context));
        paramMap.put(SDK_VERSION, Config.SDK_VERSION);
        paramMap.put(OS_VERSION, VenvyDeviceUtil.getOsVersion());
        paramMap.put(UD_ID, VenvyDeviceUtil.getAndroidID(context));
        paramMap.put(NETWORK, String.valueOf(VenvyDeviceUtil.getNetWorkType(context)));
        paramMap.put(LANGUAGE, VenvyDeviceUtil.getLanguage(context));
        paramMap.put(PHONE_MODEL, android.os.Build.MODEL);
        paramMap.put(PHONE_PROVIDER, android.os.Build.BRAND);
        paramMap.put(OS_TYPE, String.valueOf(1));
        paramMap.put(ANDROID_ID, VenvyDeviceUtil.getAndroidID(context));
        paramMap.put(IMEI, VenvyDeviceUtil.getIMEI(context));
        paramMap.put(DEVICE_TYPE, String.valueOf(1));
        paramMap.put(PHONE_WIDTH, String.valueOf(VenvyUIUtil.getScreenWidth(context)));
        paramMap.put(PHONE_HEIGHT, String.valueOf(VenvyUIUtil.getScreenHeight(context)));
        paramMap.put(PPI, String.valueOf(VenvyUIUtil.getScreenPPI(context)));
        paramMap.put(CARRIER, String.valueOf(VenvyDeviceUtil.getSubscriptionOperatorType(context)));

        String ip = VenvyDeviceUtil.getLocalIPAddress();
        if (!TextUtils.isEmpty(ip)) {
            paramMap.put(IP, ip);
        }
        String appName = VenvyAPKVersionCodeUtils.getAppName(context);
        if (!TextUtils.isEmpty(appName)) {
            paramMap.put(APP_NAME, appName);
        }
        String packageName = VenvyAPKVersionCodeUtils.getPackageName(context);
        if (!TextUtils.isEmpty(packageName)) {
            paramMap.put(PKG_NAME, packageName);
        }
        String macAddress = VenvyDeviceUtil.getMacAddress();
        if (!TextUtils.isEmpty(macAddress)) {
            paramMap.put(MAC, macAddress);
        }
        String imsi = VenvyDeviceUtil.getIMSI(context);
        if (!TextUtils.isEmpty(imsi)) {
            paramMap.put(IMSI, imsi);
        }
        return new JSONObject(paramMap);
    }
}
