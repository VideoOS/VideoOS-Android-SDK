package cn.com.venvy.lua.plugin;

import android.os.Build;
import android.text.TextUtils;

import com.taobao.luaview.util.AndroidUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.App;
import cn.com.venvy.Platform;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * 与设备有关的插件
 * Created by Arthur on 2017/8/21.
 */
public class LVDevicePlugin {

    private static Portrait sPortrait;
    private static DeviceBuildVersion sDeviceBuildVersion;
    private static DeviceTitleBarHeight sDeviceTitleBarHeight;
    private static IsTitleBarShow sIsTitleBarShow;

    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("isPortraitScreen", sPortrait == null ? sPortrait = new Portrait() : sPortrait);
        venvyLVLibBinder.set("deviceBuildVersion", sDeviceBuildVersion == null ? sDeviceBuildVersion = new DeviceBuildVersion() : sDeviceBuildVersion);
        venvyLVLibBinder.set("titleBarHeight", sDeviceTitleBarHeight == null ? sDeviceTitleBarHeight = new DeviceTitleBarHeight() : sDeviceTitleBarHeight);
        venvyLVLibBinder.set("isTitleBarShow", sIsTitleBarShow == null ? sIsTitleBarShow = new IsTitleBarShow() : sIsTitleBarShow);
        venvyLVLibBinder.set("packageName", new PackageName(platform));
        venvyLVLibBinder.set("getIdentity", new UDID(platform));
        venvyLVLibBinder.set("screenScale", new ScreenScale());

    }

    private static class UDID extends VarArgFunction {
        private Platform mPlatform;

        UDID(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (mPlatform != null && mPlatform.getPlatformInfo() != null && !TextUtils.isEmpty(mPlatform.getPlatformInfo().getIdentity())) {
                return LuaValue.valueOf(mPlatform.getPlatformInfo().getIdentity());
            }
            return LuaValue.valueOf(VenvyDeviceUtil.getAndroidID(App.getContext()));
        }
    }

    private static class ScreenScale extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(AndroidUtil.getDensity(App.getContext()) + 0.5f);
        }
    }

    /**
     * 判断是否是竖屏
     */
    private static class Portrait extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return LuaValue.valueOf(VenvyUIUtil.isScreenOriatationPortrait(App.getContext()));
        }
    }

    /**
     * 获取包名
     */
    private static class PackageName extends VarArgFunction {

        private Platform mPlatform;

        PackageName(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (mPlatform != null && mPlatform.getPlatformInfo() != null) {
                if (mPlatform.getPlatformInfo().getCustomerPackageName() != null) {
                    return LuaValue.valueOf(mPlatform.getPlatformInfo().getCustomerPackageName());
                }
            }
            return LuaValue.valueOf(VenvyDeviceUtil.getPackageName(App.getContext()));
        }
    }


    /**
     * 获取Android版本号
     */
    private static class DeviceBuildVersion extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            return valueOf(Build.VERSION.SDK_INT);
        }
    }


    /**
     * 获取titlebar高度
     */
    private static class DeviceTitleBarHeight extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(VenvyUIUtil.px2dip(App.getContext(), VenvyUIUtil.getNavigationBarHeight(App.getContext())));
        }
    }

    /**
     * 获取titlebar是否存在
     */
    private static class IsTitleBarShow extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            return valueOf(VenvyUIUtil.isNavigationBarShow(App.getContext()));
        }
    }
}
