package cn.com.venvy.lua;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.global.LuaViewConfig;

import cn.com.venvy.Platform;
import cn.com.venvy.lua.binder.UIGradientViewBinder;
import cn.com.venvy.lua.binder.VenvyActivityLifeCycleBinder;
import cn.com.venvy.lua.binder.VenvyAppletBinder;
import cn.com.venvy.lua.binder.VenvyHttpRequestBinder;
import cn.com.venvy.lua.binder.VenvyKeyboardBinder;
import cn.com.venvy.lua.binder.VenvyMediaLifeCycleBinder;
import cn.com.venvy.lua.binder.VenvyMediaViewBinder;
import cn.com.venvy.lua.binder.VenvyMqttBinder;
import cn.com.venvy.lua.binder.VenvyNativeBinder;
import cn.com.venvy.lua.binder.VenvyNotificationBinder;
import cn.com.venvy.lua.binder.VenvySvgaBinder;
import cn.com.venvy.lua.binder.VenvyWebViewBinder;
import cn.com.venvy.lua.bridge.LVHttpBridge;
import cn.com.venvy.lua.bridge.LVImageScannerBridge;
import cn.com.venvy.lua.provider.ImageProviderImpl;

/**
 * Created by Arthur on 2017/9/5.
 */

public class LuaHelper {

    private static UIGradientViewBinder uiGradientViewBinder;
    private static VenvyMediaLifeCycleBinder venvyMediaLifeCycleBinder;
    private static VenvyMqttBinder venvyMqttBinder;
    private static VenvyWebViewBinder venvyWebViewBinder;
    private static VenvyActivityLifeCycleBinder venvyActivityLifeCycleBinder;
    private static VenvyKeyboardBinder venvyKeyboardBinder;
    private static VenvyNativeBinder venvyNativeBinder;
    private static VenvyAppletBinder venvyAppletBinder;
    private static VenvySvgaBinder venvySvgaBinder;
    private static LVHttpBridge lvHttpBridge;
    private static VenvyMediaViewBinder mediaViewBinder;
    private static VenvyHttpRequestBinder httpRequestBinder;
    private static VenvyNotificationBinder notificationBinder;

    public static void initLuaConfig() {
        LuaView.registerImageProvider(ImageProviderImpl.class);
        LuaViewConfig.setLibsLazyLoad(true);
        //是否使用非反射方式API调用（默认为true)
        LuaViewConfig.setUseNoReflection(true);
        LuaViewConfig.setCachePrototype(true);
    }

    /**
     * 异步创建LuaView
     */
    public static void createLuaViewAsync(Context context, final Platform platform, final ViewGroup rootViewGroup, @Nullable LuaView.CreatedCallback createdCallback) {

        final LuaView.CreatedCallback clientCallback = createdCallback;
        VenvyLuaView.createAsync(context, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                registerNativeLibs(luaView, platform, rootViewGroup);
                if (clientCallback != null) {
                    clientCallback.onCreated(luaView);
                }
            }
        });
    }

    private static void registerNativeLibs(@NonNull LuaView luaView, Platform platform, ViewGroup viewGroup) {

        luaView.register("NativeScanner", new LVImageScannerBridge(luaView.getContext()));
        luaView.registerLibs(getUiGradientViewBinder(),
                getMediaLifeCycleBinder(platform),
                getMqttBinder(),
                getAppletBinder(platform),
                getSvgaBinder(),
                getWebViewBinder(platform),
                getActivityLifeCycleBinder(),
                getKeyboardBinder(),
                getMediaViewBinder(),
                getNotificationBinder(),
                getNativeBinder(platform, viewGroup), getHttpRequestBinder(platform));
        luaView.setUseStandardSyntax(true);//是否使用标准语法
    }

    private static VenvyMediaViewBinder getMediaViewBinder() {
        return mediaViewBinder == null ? mediaViewBinder = new VenvyMediaViewBinder() : mediaViewBinder;
    }

    private static VenvyHttpRequestBinder getHttpRequestBinder(Platform platform) {
        VenvyHttpRequestBinder target = httpRequestBinder == null ? httpRequestBinder = new VenvyHttpRequestBinder() : httpRequestBinder;
        target.setLVHttpBridge(getLvHttpBridge(platform));
        return target;
    }

    private static VenvyNotificationBinder getNotificationBinder() {
        return notificationBinder == null ? notificationBinder = new VenvyNotificationBinder() : notificationBinder;
    }

    private static UIGradientViewBinder getUiGradientViewBinder() {
        return uiGradientViewBinder == null ? uiGradientViewBinder = new UIGradientViewBinder() : uiGradientViewBinder;
    }

    private static VenvyActivityLifeCycleBinder getActivityLifeCycleBinder() {
        return venvyActivityLifeCycleBinder == null ? venvyActivityLifeCycleBinder = new VenvyActivityLifeCycleBinder() : venvyActivityLifeCycleBinder;
    }

    private static VenvyKeyboardBinder getKeyboardBinder() {
        return venvyKeyboardBinder == null ? venvyKeyboardBinder = new VenvyKeyboardBinder() : venvyKeyboardBinder;
    }

    private static VenvyMediaLifeCycleBinder getMediaLifeCycleBinder(Platform platform) {
        VenvyMediaLifeCycleBinder target = venvyMediaLifeCycleBinder == null ? venvyMediaLifeCycleBinder = new VenvyMediaLifeCycleBinder() : venvyMediaLifeCycleBinder;
        target.setPlatform(platform);
        return target;
    }

    private static VenvyMqttBinder getMqttBinder() {
        return venvyMqttBinder == null ? venvyMqttBinder = new VenvyMqttBinder() : venvyMqttBinder;
    }

    private static VenvyAppletBinder getAppletBinder(Platform platform) {
        VenvyAppletBinder appletBinder = venvyAppletBinder == null ? venvyAppletBinder = new VenvyAppletBinder() : venvyAppletBinder;
        appletBinder.setPlatform(platform);
        return appletBinder;
    }

    private static VenvyNativeBinder getNativeBinder(Platform platform, ViewGroup viewGroup) {
        VenvyNativeBinder target = venvyNativeBinder == null ? venvyNativeBinder = new VenvyNativeBinder() : venvyNativeBinder;
        target.setPlatform(platform);
        target.setRootView(viewGroup);
        target.setHttpBridge(getLvHttpBridge(platform));
        return target;
    }

    private static VenvyWebViewBinder getWebViewBinder(Platform platform) {
        VenvyWebViewBinder target = venvyWebViewBinder == null ? venvyWebViewBinder = new VenvyWebViewBinder() : venvyWebViewBinder;
        target.setPlatform(platform);
        return target;
    }

    private static VenvySvgaBinder getSvgaBinder() {
        return venvySvgaBinder == null ? venvySvgaBinder = new VenvySvgaBinder() : venvySvgaBinder;
    }

    private static LVHttpBridge getLvHttpBridge(Platform platform) {
        return lvHttpBridge = new LVHttpBridge(platform);
    }

    public static void destroy() {
        uiGradientViewBinder = null;
        venvyWebViewBinder = null;
        venvyMediaLifeCycleBinder = null;
        venvyMqttBinder = null;
        venvyAppletBinder = null;
        venvyActivityLifeCycleBinder = null;
        venvyKeyboardBinder = null;
        venvyNativeBinder = null;
        venvySvgaBinder = null;
        if (lvHttpBridge != null) {
            lvHttpBridge.abortAll();
        }
        lvHttpBridge = null;
        mediaViewBinder = null;
        httpRequestBinder = null;
        notificationBinder = null;
    }
}
