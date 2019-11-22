package cn.com.venvy.lua.plugin;

import android.content.Context;
import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.json.JSONObject;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.App;
import cn.com.venvy.Config;
import cn.com.venvy.common.utils.VenvyAPKVersionCodeUtils;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by videojj_pls on 2018/9/4.
 * 网络请求公共参数插件
 */

public class LVCommonParamPlugin {
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


    private static LVCommonParamPlugin.GetCommonParamData mCommonParamData;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("commonParam", mCommonParamData == null ? mCommonParamData = new LVCommonParamPlugin.GetCommonParamData() : mCommonParamData);
    }

    public static String getCommonParamJson() {
        String paramJson = "";
        mCommonParamData = mCommonParamData == null ? mCommonParamData = new LVCommonParamPlugin.GetCommonParamData() : mCommonParamData;
        try {
            Varargs varargs = mCommonParamData.invoke();
            int fixIndex = VenvyLVLibBinder.fixIndex(varargs);
            LuaTable commonParamTable = LuaUtil.getTable(varargs, fixIndex + 1);
            paramJson = new JSONObject(LuaUtil.toMap(commonParamTable)).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramJson;
    }

    private static class GetCommonParamData extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaTable table = new LuaTable();
            Context context = App.getContext();
            if (context == null) {
                return table;
            }
            table.set(LuaValue.valueOf(VERSION), LuaValue.valueOf(VenvyAPKVersionCodeUtils.getVersionName(App.getContext())));
            table.set(LuaValue.valueOf(SDK_VERSION), LuaValue.valueOf(Config.SDK_VERSION));
            table.set(LuaValue.valueOf(OS_VERSION), LuaValue.valueOf(VenvyDeviceUtil.getOsVersion()));
            table.set(LuaValue.valueOf(UD_ID), LuaValue.valueOf(VenvyDeviceUtil.getAndroidID(context)));
            table.set(LuaValue.valueOf(NETWORK), LuaValue.valueOf(VenvyDeviceUtil.getNetWorkType(context)));
            table.set(LuaValue.valueOf(LANGUAGE), LuaValue.valueOf(VenvyDeviceUtil.getLanguage(context)));
            table.set(LuaValue.valueOf(PHONE_MODEL), LuaValue.valueOf(android.os.Build.MODEL));
            table.set(LuaValue.valueOf(PHONE_PROVIDER), LuaValue.valueOf(android.os.Build.BRAND));
            table.set(LuaValue.valueOf(OS_TYPE), LuaValue.valueOf(1));
            table.set(LuaValue.valueOf(ANDROID_ID), LuaValue.valueOf(VenvyDeviceUtil.getAndroidID(context)));
            table.set(LuaValue.valueOf(IMEI), LuaValue.valueOf(VenvyDeviceUtil.getIMEI(context)));
            table.set(LuaValue.valueOf(DEVICE_TYPE), LuaValue.valueOf(1));
            table.set(LuaValue.valueOf(PHONE_WIDTH), LuaValue.valueOf(VenvyUIUtil.getScreenWidth(context)));
            table.set(LuaValue.valueOf(PHONE_HEIGHT), LuaValue.valueOf(VenvyUIUtil.getScreenHeight(context)));
            table.set(LuaValue.valueOf(PPI), LuaValue.valueOf(VenvyUIUtil.getScreenPPI(context)));
            table.set(LuaValue.valueOf(CARRIER), LuaValue.valueOf(VenvyDeviceUtil.getSubscriptionOperatorType(context)));
            String ip = VenvyDeviceUtil.getLocalIPAddress();
            if (!TextUtils.isEmpty(ip)) {
                table.set(LuaValue.valueOf(IP), LuaValue.valueOf(ip));
            }
            String appName = VenvyAPKVersionCodeUtils.getAppName(context);
            if (!TextUtils.isEmpty(appName)) {
                table.set(LuaValue.valueOf(APP_NAME), LuaValue.valueOf(appName));
            }
            String packageName = VenvyAPKVersionCodeUtils.getPackageName(context);
            if (!TextUtils.isEmpty(packageName)) {
                table.set(LuaValue.valueOf(PKG_NAME), LuaValue.valueOf(packageName));
            }
            String macAddress = VenvyDeviceUtil.getMacAddress();
            if (!TextUtils.isEmpty(macAddress)) {
                table.set(LuaValue.valueOf(MAC), LuaValue.valueOf(macAddress));
            }
            String imsi = VenvyDeviceUtil.getIMSI(context);
            if (!TextUtils.isEmpty(imsi)) {
                table.set(LuaValue.valueOf(IMSI), LuaValue.valueOf(imsi));
            }
            return table;
        }
    }
}
