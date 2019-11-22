package cn.com.venvy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.UUID;

import static android.os.Environment.MEDIA_MOUNTED;


/**
 * 计算宽高工具类
 *
 * @author John
 */
public class VenvyDeviceUtil {

    private static final String TAG = "VenvyDeviceUtil";
    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static final String DEVICE_PREFERENCE_NAME = "venvy-device";
    private static String[] platforms = {"http://pv.sohu.com/cityjson", "http://pv.sohu.com/cityjson?ie=utf-8", "http://ip.taobao.com/service/getIpInfo.php?ip=myip"};

    public static String getLanguage(Context context) {
        return context.getResources()
                .getConfiguration().locale.getCountry();
    }

    public static String getUserAgent(Context context) {
        String model = null;
        try {
            model = URLEncoder.encode(android.os.Build.MODEL, "UTF-8");
        } catch (Exception e) {
            //忽略此处异常
        }
        return "android/"
                + (model != null ? (model + "/") : "")
                + android.os.Build.VERSION.SDK + "/"
                + android.os.Build.VERSION.RELEASE + "/"
                + (getPackageName(context) == null ? "" : getPackageName(context));
    }

    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {

        }
        return null;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /***
     * 获取系统唯一标识码
     * @return
     */
    public static String getClient() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取手机IMEI号，需要<uses-permission
     * android:name="android.permission.READ_PHONE_STATE" />
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String IMEI = "";
        if (context == null) {
            return IMEI;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = telephonyManager.getDeviceId();
            //md5加密
//            IMEI = VenvyStringUtil.convertMD5(IMEI);
        } catch (Exception e) {
            IMEI = "000000000000000";
        }
        return IMEI;
    }

    /***
     * 获取IMSI信息
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
//        TelephonyManager tm = (TelephonyManager) context
//                .getSystemService(Context.TELEPHONY_SERVICE);//
//        return tm.getSubscriberId();
        return "";
    }

    /**
     * 获取androidID
     *
     * @return
     */
    public static String getAndroidID(Context context) {
        String androidID = "";
        if (context == null) {
            return androidID;
        }
        try {
            androidID = Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
            //md5加密
//            androidID = VenvyStringUtil.convertMD5(androidID);
        } catch (Exception e) {
            androidID = "0000000000000000";
        }
        return androidID;
    }

    /***
     * 获取用户唯一标示
     * @param mContext
     * @return
     */
    public static String getClientId(Context mContext) {
        String clientId = VenvyPreferenceHelper.getString(mContext, DEVICE_PREFERENCE_NAME,
                "VideoJj-Live-clientID", null);
        if (!TextUtils.isEmpty(clientId))
            return clientId;
        else {
            clientId = String.valueOf(System.currentTimeMillis())
                    + VenvyRandomUtils.getRandomNumbersAndLetters(8);
            VenvyPreferenceHelper.putString(mContext, DEVICE_PREFERENCE_NAME,
                    "VideoJj-Live-clientID", clientId);
            return clientId;
        }
    }

    /**
     * Returns a unique UUID for the current android device. As with all UUIDs, this unique ID is
     * "very highly likely" to be unique across all Android devices. Much more so than ANDROID_ID
     * is. The UUID is generated by using ANDROID_ID as the base key if appropriate, falling back on
     * TelephonyManager.getDeviceID() if ANDROID_ID is known to be incorrect, and finally falling
     * back on a random UUID that's persisted to SharedPreferences if getDeviceID() does not return
     * a usable value. In some rare circumstances, this ID may change. In particular, if the device
     * is factory reset a new device ID may be generated. In addition, if a user upgrades their
     * phone from certain buggy implementations of Android 2.2 to a newer, non-buggy version of
     * Android, the device ID may change. Or, if a user uninstalls your app on a device that has
     * neither a proper Android ID nor a Device ID, this ID may change on reinstallation. Note that
     * if the code falls back on using TelephonyManager.getDeviceId(), the resulting ID will NOT
     * change after a factory reset. Something to be aware of. Works around a bug in Android 2.2 for
     * many devices when using ANDROID_ID directly.
     *
     * @return a UUID that may be used to uniquely identify your device for most purposes.
     */
    public static
    @Nullable
    UUID getDeviceUuid(Context context) {
        UUID uuid;
        synchronized (VenvyStringUtil.class) {
            final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
            final String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                uuid = UUID.fromString(id);
            } else {
                final String androidId = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                try {
                    if (!TextUtils.isEmpty(androidId) && !TextUtils.equals(androidId, "9774d56d682e549c")) {
                        uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                    } else {
                        final String deviceId = ((TelephonyManager) context
                                .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId
                                .getBytes("utf8")) : UUID.randomUUID();
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply();
                    }
                    return uuid;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return uuid;
    }


    /**
     * 获取IP地址
     */
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            VenvyLog.e(TAG, ex);
        }
        return null;
    }

    /***
     * 获取外网IP地址
     * @param index
     * @return
     */
    public static String getOutNetIP(int index) {
        if (index < platforms.length) {
            BufferedReader buff = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(platforms[index]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);//读取超时
                urlConnection.setConnectTimeout(5000);//连接超时
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                    InputStream is = urlConnection.getInputStream();
                    buff = new BufferedReader(new InputStreamReader(is, "UTF-8"));//注意编码，会出现乱码
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        builder.append(line);
                    }

                    buff.close();//内部会关闭 InputStream
                    urlConnection.disconnect();
                    if (index == 0 || index == 1) {
                        //截取字符串
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);//包含[satrtIndex,endIndex)
                        JSONObject jo = new JSONObject(json);
                        String ip = jo.optString("cip");

                        return ip;
                    } else if (index == 2) {
                        JSONObject jo = new JSONObject(builder.toString());
                        return jo.optJSONObject("data").optString("ip");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return getLocalIPAddress();
        }
        return getOutNetIP(++index);
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        // 当前所连接的网络可用
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            VenvyLog.e(VenvyDeviceUtil.TAG, e);
            return true;
        }
    }

    public static String getNetWorkName(Context context) {
        String strNetworkType = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                VenvyLog.i("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName
                                .equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase
                                ("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    public static int getNetWorkType(Context context) {
        int strNetworkType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = 1;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                VenvyLog.i("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    case TelephonyManager.NETWORK_TYPE_GSM:  // api<25: replace by 16
                        strNetworkType = 2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:   // api< 9: replace by 12
                    case TelephonyManager.NETWORK_TYPE_EHRPD:    // api<11: replace by 14
                    case TelephonyManager.NETWORK_TYPE_HSPAP:    // api<13: replace by 15
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA: // api<25: replace by 17
                        strNetworkType = 3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:      // api<11: replace by 13
                    case TelephonyManager.NETWORK_TYPE_IWLAN:    // api<25: replace by 18
                        strNetworkType = 4;
                        break;
                    case TelephonyManager.NETWORK_TYPE_NR:// api<29: replace by 20
                        strNetworkType = 5;
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 获取设备拨号运营商  运营商信息 0:其他，1:移动，2:联通，3:电信
     *
     * @return ["中国电信CTCC":3]["中国联通CUCC:2]["中国移动CMCC":1]["other":0]["无sim卡":-1]
     */
    public static int getSubscriptionOperatorType(Context context) {
        int opeType = 0;
        // No sim
        if (!hasSim(context)) {
            return opeType;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        // 中国联通
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = 2;
            // 中国移动
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = 1;
            // 中国电信
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = 3;
        } else {
            opeType = 0;
        }
        return opeType;
    }

    /***
     * 获取mac地址
     * @return
     */
    public static String getMacAddress() {
        String address = null;
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    address = mac;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static boolean existSDcard() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        return MEDIA_MOUNTED.equals(externalStorageState);
    }

    /**
     * 是否支持sim卡
     *
     * @param context
     * @return
     */
    public static boolean isSupportSimCard(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }


    /**
     * 获取屏幕物理尺寸
     *
     * @return
     */
    public static double getScreenDimension(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);

        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);

        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(Math.sqrt(x + y)));
    }

    /**
     * 检查手机是否有sim卡
     */
    public static boolean hasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (TextUtils.isEmpty(operator)) {
            return false;
        }
        return true;
    }
}
