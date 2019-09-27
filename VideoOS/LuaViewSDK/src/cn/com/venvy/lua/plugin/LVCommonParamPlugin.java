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
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by videojj_pls on 2018/9/4.
 * 网络请求公共参数插件
 */

public class LVCommonParamPlugin {
    private static final String VERSION = "VERSION";//版本号
    private static final String SDK_VERSION = "SDK_VERSION";//sdk版本号
    private static final String USER_AGENT = "USER_AGENT";//代理
    private static final String OS_VERSION = "OS_VERSION";//系统版本号
    private static final String UD_ID = "UD_ID";//uuid
    //    private static final String APP_KEY="";//app的key
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
    private static final String ANDROID_ID = "ANDROID_ID";
    private static final String IMEI = "IMEI";
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
            table.set(LuaValue.valueOf(USER_AGENT), LuaValue.valueOf(VenvyDeviceUtil.getUserAgent(context)));
            table.set(LuaValue.valueOf(OS_VERSION), LuaValue.valueOf(VenvyDeviceUtil.getOsVersion()));
            table.set(LuaValue.valueOf(UD_ID), LuaValue.valueOf(VenvyDeviceUtil.getAndroidID(context)));
            String ip = VenvyDeviceUtil.getLocalIPAddress();
            if (!TextUtils.isEmpty(ip)) {
                table.set(LuaValue.valueOf(IP), LuaValue.valueOf(ip));
            }
            table.set(LuaValue.valueOf(NETWORK), LuaValue.valueOf(VenvyDeviceUtil.getNetWorkName(context)));
            table.set(LuaValue.valueOf(LANGUAGE), LuaValue.valueOf(VenvyDeviceUtil.getLanguage(context)));
            table.set(LuaValue.valueOf(PHONE_MODEL), LuaValue.valueOf(android.os.Build.MODEL));
            table.set(LuaValue.valueOf(PHONE_PROVIDER), LuaValue.valueOf(android.os.Build.BRAND));
            table.set(LuaValue.valueOf(ANDROID_ID), LuaValue.valueOf(VenvyDeviceUtil.getAndroidID(App.getContext())));
            table.set(LuaValue.valueOf(IMEI), LuaValue.valueOf(VenvyDeviceUtil.getIMEI(App.getContext())));
            return table;
        }
    }
}
