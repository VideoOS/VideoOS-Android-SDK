package cn.com.venvy.common.webview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.com.venvy.AppSecret;
import cn.com.venvy.CommonParam;
import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.common.exception.LoginException;
import cn.com.venvy.common.interf.ICallJsFunction;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyBase64;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyMD5Util;
import cn.com.venvy.common.utils.VenvyPreferenceHelper;
import cn.com.venvy.common.utils.VenvyUIUtil;

import static cn.com.venvy.common.observer.VenvyObservableTarget.TAG_JS_BRIDGE_OBSERVER;

/**
 * Created by mac on 17/12/26.
 */

public class JsBridge implements VenvyObserver {
    protected IPlatformLoginInterface mPlatformLoginInterface;
    private Map<String, List<String>> jsMap = new HashMap<>();
    private ICallJsFunction mCallJsFunction;
    protected Context mContext;
    protected String ssid = System.currentTimeMillis() + "";
    private WebViewCloseListener mWebViewCloseListener;
    //是否禁止打开支付宝app true：禁止；false：打开支付宝
    public boolean payDisabled;
    protected IVenvyWebView mVenvyWebView;
    private Platform mPlatform;
    //    private JsParamsInfo mParamsInfo;
    public String mJsData;
    public String mJsTitle;
    //开发者id
    public String mDeveloperUserId;

    public JsBridge(Context context, @NonNull IVenvyWebView webView, Platform platform) {
        this.mVenvyWebView = webView;
        mContext = context;
        mPlatform = platform;
    }

    public void setWebViewCloseListener(WebViewCloseListener webViewCloseListener) {
        mWebViewCloseListener = webViewCloseListener;
    }

    public void setJsData(String jsData) {
        this.mJsData = jsData;
    }

    public void setJsTitle(String jsTitle) {
        this.mJsTitle = jsTitle;
    }

    public void setCallJsFunction(ICallJsFunction jsFunction) {
        mCallJsFunction = jsFunction;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setDeveloperUserId(String developerUserId) {
        this.mDeveloperUserId = developerUserId;
    }

    public void setPlatformLoginInterface(IPlatformLoginInterface platformLoginInterface) {
        mPlatformLoginInterface = platformLoginInterface;
    }

    @JavascriptInterface
    public void commonData(String jsParams) {
        int screenHeight = VenvyUIUtil.getScreenHeight(mContext);
        int screenWidth = VenvyUIUtil.getScreenWidth(mContext);
        float height = VenvyUIUtil.px2dip(mContext, Math.min(screenWidth, screenHeight));
        float width = height / 375.0f * 230;
        JSONObject obj = new JSONObject();
        JSONObject objSize = new JSONObject();
        JSONObject xyObj = new JSONObject();
        try {
            objSize.put("width", width);
            objSize.put("height", height);
            obj.put("common", CommonParam.getCommonParamJson(mPlatform.getPlatformInfo().getAppKey()));
            obj.put("size", objSize);

            xyObj.put("x", mVenvyWebView.getWebViewX());
            xyObj.put("y", mVenvyWebView.getWebViewY());
            obj.put("origin", xyObj);
            obj.put("secret", mPlatform.getPlatformInfo().getAppSecret());
            obj.put("videoInfo", getVideoInfo());
        } catch (Exception e) {

        }
        callJsFunction(obj.toString(), jsParams);
    }

    @JavascriptInterface
    public void openUrl(String jsParams) {
        try {
            JSONObject obj = new JSONObject(jsParams);
            JSONObject msgObj = obj.optJSONObject("msg");
            String linkUrl = msgObj.optString("linkUrl");
            String deepLink = msgObj.optString("deepLink");
            String selfLink = msgObj.optString("selfLink");
            String actionString = "";
            if (!TextUtils.isEmpty(linkUrl)) {
                actionString = linkUrl;
            } else if (!TextUtils.isEmpty(deepLink)) {
                actionString = deepLink;
            } else if (!TextUtils.isEmpty(selfLink)) {
                actionString = selfLink;
                ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLEAR_ALL_VISION_PROGRAM);
            }
            String adID = VenvyMD5Util.MD5(actionString);
            WidgetInfo.WidgetActionType widgetActionType = WidgetInfo.WidgetActionType.findTypeById(1);
            final WidgetInfo widgetInfo = new WidgetInfo.Builder()
                    .setAdId(adID)
                    .setWidgetActionType(widgetActionType)
                    .setUrl(actionString)
                    .setWidgetName(TextUtils.isEmpty(mJsTitle) ? "" : mJsTitle)
                    .setDeepLink(deepLink)
                    .setLinkUrl(linkUrl)
                    .setSelfLink(selfLink)
                    .build();
            if (mPlatform.getWidgetClickListener() != null) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlatform.getWidgetClickListener().onClick(widgetInfo);
                    }
                });
            }
            JSONObject jsonObject = new JSONObject();
            if (TextUtils.isEmpty(deepLink)) {
                jsonObject.put("canOpen", 0);
            } else {
                boolean canOpen = isPayInstall(Uri.parse(deepLink));
                if (canOpen) {
                    jsonObject.put("canOpen", 1);
                } else {
                    jsonObject.put("canOpen", 2);
                }
            }
            callJsFunction(jsonObject.toString(), jsParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getInitData(String jsParams) {
        if (TextUtils.isEmpty(mJsData)) {
            mJsData = "{}";
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("data", new JSONObject(mJsData));
        } catch (Exception e) {

        }
        callJsFunction(obj.toString(), jsParams);
    }

    @JavascriptInterface
    public void showErrorPage(String jsParams) {
        if (TextUtils.isEmpty(jsParams)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsParams);
            mVenvyWebView.showErrorPage(obj.optJSONObject("msg").toString());
        } catch (Exception e) {
            e.printStackTrace();
            mVenvyWebView.showErrorPage("");
        }
    }

    @JavascriptInterface
    public void updateNaviTitle(String jsParams) {
        if (TextUtils.isEmpty(jsParams)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsParams);
            mVenvyWebView.updateNaviTitle(obj.optJSONObject("msg").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openApplet(String jsParams) {
        if (TextUtils.isEmpty(jsParams)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(jsParams);
            mVenvyWebView.openApplet(obj.optJSONObject("msg").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void networkEncrypt(String jsParams) {
        if (mPlatform == null) {
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            JSONObject jsParamsObj = new JSONObject(jsParams);
            obj.put("encryptData", VenvyAesUtil.encrypt(AppSecret.getAppSecret(mPlatform), AppSecret.getAppSecret(mPlatform), jsParamsObj.optJSONObject("msg").optString("data")));

        } catch (Exception e) {
            e.printStackTrace();
        }
        callJsFunction(obj.toString(), jsParams);
    }

    @JavascriptInterface
    public void networkDecrypt(String jsParams) {
        if (mPlatform == null) {
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            JSONObject jsParamsObj = new JSONObject(jsParams);
            obj.put("decryptData", VenvyBase64.encode(VenvyAesUtil.decrypt(jsParamsObj.optJSONObject("msg").optString("data"), AppSecret.getAppSecret(mPlatform), AppSecret.getAppSecret(mPlatform)).getBytes()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        callJsFunction(obj.toString(), jsParams);
    }

    @JavascriptInterface
    public void getIdentity(String jsParams) {
        JSONObject jsonObject = new JSONObject();
        try {
            String identity = getIdentity();
            jsonObject.put("identity", identity);
            jsonObject.put("ssid", identity + ssid);
            jsonObject.put("sdkVersion", "1");
            JSONObject screen = new JSONObject();
            screen.put("screen", VenvyUIUtil.isScreenOriatationPortrait(mContext) ? "5" : "0");

            jsonObject.put("ext", screen);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callJsFunction(jsonObject.toString(), jsParams);

    }

    @JavascriptInterface
    public void getUserInfo(String jsParams) {
        String userJson = "{}";
        if (mPlatformLoginInterface != null) {
            PlatformUserInfo userInfo = mPlatformLoginInterface.getLoginUser();
            if (userInfo != null) {
                userJson = userInfo.toString();
            }
        }
        VenvyLog.i("-getuserInfo----" + userJson + "params==" + jsParams);
        callJsFunction(userJson, jsParams);
    }

    @JavascriptInterface
    public void setUserInfo(String jsParams) {
        VenvyLog.i("---userInfo--" + jsParams);


        try {
            JSONObject json = new JSONObject(jsParams);
            JSONObject user = json.optJSONObject("msg");
            PlatformUserInfo platformUserInfo = new PlatformUserInfo();
            platformUserInfo.setPhoneNum(user.optString("phone"));
            platformUserInfo.setUserToken(user.optString("token"));
            platformUserInfo.setUserName(user.optString("userName"));
            platformUserInfo.setNickName(user.optString("nickName"));
            platformUserInfo.setUid(user.optString("uid"));
            if (mPlatformLoginInterface != null) {
                mPlatformLoginInterface.userLogined(platformUserInfo);
            }
            //todo
        } catch (JSONException e) {

        }
    }

    /***
     * 写入本地数据
     * @param jsonParams
     */
    @JavascriptInterface
    public void setStorageData(String jsonParams) {

        try {
            JSONObject json = new JSONObject(jsonParams);
            JSONObject msgObj = json.optJSONObject("msg");
            String key = msgObj.optString("key");
            String value = msgObj.optString("value");
            VenvyPreferenceHelper.putString(mContext, TextUtils.isEmpty(mDeveloperUserId) ? "" : mDeveloperUserId, key, value);

        } catch (JSONException e) {

        }
    }

    /***
     * 读取本地数据
     * @param jsParams
     */
    @JavascriptInterface
    public void getStorageData(final String jsParams) {
        try {
            final JSONObject jsonObj = new JSONObject(jsParams);
            JSONObject msgObj = jsonObj.optJSONObject("msg");
            final String key = msgObj.optString("key");
            if (jsonObj.has("callback")) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        callJsFunction(VenvyPreferenceHelper.getString(mContext, mDeveloperUserId, key, ""), jsParams);
                    }
                });

            }

        } catch (JSONException e) {

        }
    }

    @JavascriptInterface
    public void requireLogin(final String jsParams) {
        VenvyLog.i("---请求登录--");
        if (mPlatformLoginInterface != null) {
            mPlatformLoginInterface.login(new IPlatformLoginInterface.LoginCallback() {
                @Override
                public void loginSuccess(PlatformUserInfo platformUserInfo) {
                    String userJson = platformUserInfo.toString();
                    VenvyLog.i("---登录成功--" + userJson);
                    callJsFunction(userJson, jsParams);
                }

                @Override
                public void loginError(LoginException loginException) {

                }
            });
        }

    }

    @Deprecated
    @JavascriptInterface
    public void detectPaymentMethod(String jsParams) {
        VenvyLog.i("支付宝＝＝" + jsParams);
        try {
            JSONObject json = new JSONObject(jsParams);
            JSONObject msg = json.optJSONObject("msg");
            JSONObject data = msg.optJSONObject("data");
            boolean result = goPay(data.optString("url"));
            String jsData = result ? "true" : "fasle";
            callJsFunction(jsData, jsParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @JavascriptInterface
    public void setConfig(String jsParams) {
        try {
            JSONObject json = new JSONObject(jsParams);
            JSONObject payStatus = json.optJSONObject("msg");
            payDisabled = payStatus.optBoolean("payDisabled");
            //todo
        } catch (JSONException e) {

        }
    }

    public boolean goPay(String url) {
        if (payDisabled) {
            return false;
        }
        Uri uri = Uri.parse(url);
        boolean result = false;

        if (uri != null) {
            if ((TextUtils.equals(uri.getScheme(), "alipays") || TextUtils.equals(uri.getScheme(), "weixin"))
                    && isPayInstall(uri)) {
                result = startApp(uri);
            }
        }

        return result;
    }

    private boolean isPayInstall(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(mContext.getPackageManager());
        return componentName != null;

    }


    private boolean startApp(Uri uri) {
        try {
            Intent intent = new Intent();
            intent.setData(uri);
            intent.setAction(Intent.ACTION_VIEW);
            mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            VenvyLog.i("打开支付包出错");
            e.printStackTrace();
            return false;
        }

    }


    @JavascriptInterface
    public void getCartData(final String jsParams) {
        new Thread() {
            @Override
            public void run() {
//                Looper.prepare();
//                String result = GoodFileCache.getAllData(mContext);
//                VenvyLog.i("--商品缓存--" + result);
//                callJsFunction(result, jsParams);
            }
        }.start();

    }


    @JavascriptInterface
    public void isScreenRotation(String jsParams) {
        VenvyLog.i("-- isScreenRotation--" + jsParams);
        boolean isProtrait = VenvyUIUtil.isScreenOriatationPortrait(mContext);
        String result = isProtrait ? "true" : "fasle";
        callJsFunction(result, jsParams);
    }


    protected void callJsFunction(final String data, String jsParams) {
        try {
            VenvyLog.i("js回调＝＝＝" + jsParams + " data == " + data);
            final JSONObject jsonObj = new JSONObject(jsParams);
            if (jsonObj.has("callback")) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        String callback = jsonObj.optString("callback");
                        mVenvyWebView.callJsFunction(callback, data);
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * h5消失的时候调用此方法
     */
    public void destroy() {
        ObservableManager.getDefaultObserable().removeObserver(TAG_JS_BRIDGE_OBSERVER, this);
    }


    private void notifyChanage(String data) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
            String type = jsonObject.optString("type");

            if (mCallJsFunction == null) {
                return;
            }
            if (jsMap.containsKey(type)) {

                String msg = jsonObject.optString("msg");
                List<String> functions = jsMap.get(type);
                for (int i = 0; i < functions.size(); i++) {
                    String jsFunction = functions.get(i);
                    mCallJsFunction.callJsFunction(jsFunction, data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void addObserver(String type, String jsFunction) {
        if (jsMap.containsKey(type)) {
            List<String> temp = jsMap.get(type);
            if (!temp.contains(jsFunction)) {
                temp.add(jsFunction);
            }
        } else {
            List<String> newList = new ArrayList<>();
            newList.add(jsFunction);
            jsMap.put(type, newList);
        }
    }


    @JavascriptInterface
    public void removeObserver(String type, String jsFunction) {
        if (jsMap.containsKey(type)) {
            List<String> temp = jsMap.get(type);
            if (temp.contains(jsFunction)) {
                temp.remove(jsFunction);
            }
        }
    }

    /**
     * {"msg":1}
     * msg:1  关闭横评webview、2 关闭竖屏webview 3、关闭activity
     * <p>
     * h5通知关闭webView
     */
    @JavascriptInterface
    public void closeView(final String jsParams) {
        VenvyLog.i("--androidToJs-close--" + jsParams);

        VenvyUIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject msgJson = new JSONObject(jsParams);
                    int msg = msgJson.optInt("msg");
                    switch (msg) {
                        case 1:
                            VenvyLog.i("---关闭横屏webview--");
                            if (mWebViewCloseListener != null) {
                                mWebViewCloseListener.onClose(WebViewCloseListener.CloseType.WEBVIEW);
                            }
                            break;
                        case 2:
                            VenvyLog.i("----关闭竖屏webview----");
                            if (mWebViewCloseListener != null) {
                                mWebViewCloseListener.onClose(WebViewCloseListener.CloseType.WEBVIEW);
                            }
                            break;
                        case 3:
                            VenvyLog.i("---关闭activity(我的订单)---");
                            if (mContext instanceof Activity) {
                                Activity activity = (Activity) mContext;
                                if (activity.isFinishing()) {
                                    return;
                                }
                                activity.finish();
                            }
                            break;
                    }
                } catch (Exception e) {
                    VenvyLog.i("--关闭webview jsParams不是标准json-" + jsParams);
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 切换横竖屏(用户在横屏下点击 立即购买、购物车结算、登录后 通知Native切到竖屏)
     */
    @JavascriptInterface
    public void screenChange(String jsParams) {
        try {
            JSONObject json = new JSONObject(jsParams);
            final String currentUrl = json.optString("msg");
            final IPlatformLoginInterface platformLoginInterface = mPlatformLoginInterface;
            if (platformLoginInterface != null) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mWebViewCloseListener != null) {
                            mWebViewCloseListener.onClose(WebViewCloseListener.CloseType.MALL);
                        }
                        IPlatformLoginInterface.ScreenChangedInfo screenChangedInfo = new IPlatformLoginInterface.ScreenChangedInfo();
                        screenChangedInfo.url = currentUrl;
                        screenChangedInfo.ssid = ssid;
                        mPlatformLoginInterface.screenChanged(screenChangedInfo);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        VenvyLog.i(" --screenChange--" + jsParams);
    }


    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        String data = bundle.getString("msgInfo");
        notifyChanage(data);
    }

    public interface WebViewCloseListener {
        enum CloseType {
            WEBVIEW, MALL
        }

        /**
         * h5关闭webview
         */
        void onClose(CloseType actionType);
    }

    private String getIdentity() {
        if (mPlatformLoginInterface != null && mPlatformLoginInterface.getLoginUser() != null) {
            String customUDID = mPlatformLoginInterface.getLoginUser().getCustomerDeviceId();
            return customUDID != null ? customUDID : "";
        } else {
            if (mPlatform != null && mPlatform.getPlatformInfo() != null && TextUtils.isEmpty(mPlatform.getPlatformInfo().getIdentity())) {
                return mPlatform.getPlatformInfo().getIdentity();
            }
            UUID uuid = VenvyDeviceUtil.getDeviceUuid(mContext);
            if (uuid != null) {
                return uuid.toString();
            }
        }
        return "";
    }

    private JSONObject getVideoInfo() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (mPlatform != null) {
                jsonObject.put("videoID", mPlatform.getPlatformInfo().getVideoId());
                IMediaControlListener mediaControlListener = mPlatform.getMediaControlListener();
                if (mediaControlListener != null) {
                    jsonObject.put("title", TextUtils.isEmpty(mediaControlListener.getVideoTitle()) ? "" : mediaControlListener.getVideoTitle());
                    jsonObject.put("episode", TextUtils.isEmpty(mediaControlListener.getVideoEpisode()) ? "" : mediaControlListener.getVideoEpisode());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
