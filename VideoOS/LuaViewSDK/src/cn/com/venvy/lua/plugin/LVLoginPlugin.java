package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.json.JSONObject;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.exception.LoginException;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;


/**
 * Created by mac on 17/9/14.
 * 用户系统相关plugin
 */

public class LVLoginPlugin {

    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("getUserInfo", new GetUserInfo(platform));
        venvyLVLibBinder.set("notifyUserLogined", new UserLogined(platform));
        venvyLVLibBinder.set("screenChanged", new ScreenChanged(platform));
        venvyLVLibBinder.set("requireLogin", new RequireLogin(platform));
    }

    /**
     * 判断是否是竖屏
     */
    private static class ScreenChanged extends VarArgFunction {
        private Platform mPlatform;

        ScreenChanged(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                if (mPlatform != null && mPlatform.getPlatformLoginInterface() != null) {
                    Integer value = LuaUtil.getInt(args, fixIndex + 1);
                    String currentUrl = LuaUtil.getString(args, fixIndex + 2);
                    String ssid = LuaUtil.getString(args, fixIndex + 3);
                    IPlatformLoginInterface.ScreenChangedInfo screenChanged = new IPlatformLoginInterface.ScreenChangedInfo();
                    screenChanged.url = currentUrl;
                    screenChanged.ssid = ssid;
                    screenChanged.screenType = value != null ? value : 1;
                    mPlatform.getPlatformLoginInterface().screenChanged(screenChanged);
                }
            }
            return LuaValue.NIL;
        }
    }

    /**
     * 请求登录
     */
    private static class RequireLogin extends VarArgFunction {
        private Platform mPlatform;

        RequireLogin(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                if (mPlatform != null && mPlatform.getPlatformLoginInterface() != null) {
                    final LuaFunction callback = LuaUtil.getFunction(args, fixIndex + 1);
                    mPlatform.getPlatformLoginInterface().login(new IPlatformLoginInterface.LoginCallback() {
                        @Override
                        public void loginSuccess(PlatformUserInfo platformUserInfo) {
                            LuaUtil.callFunction(callback, JsonUtil.toLuaTable(platformUserInfo.toString()));
                        }

                        @Override
                        public void loginError(LoginException loginException) {
                            LuaUtil.callFunction(callback, LuaValue.NIL);
                        }
                    });
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class GetUserInfo extends VarArgFunction {

        private Platform mPlatform;

        GetUserInfo(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (mPlatform != null && mPlatform.getPlatformLoginInterface() != null) {
                PlatformUserInfo platformUserInfo = mPlatform.getPlatformLoginInterface().getLoginUser();
                if (platformUserInfo != null) {
                    return JsonUtil.toLuaTable(platformUserInfo.toString());
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class UserLogined extends VarArgFunction {

        private Platform mPlatform;

        UserLogined(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (mPlatform == null || mPlatform.getPlatformLoginInterface() == null) {
                return LuaValue.NIL;
            }
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                if (table.isnil()) {
                    return LuaValue.NIL;
                }
                Object object = JsonUtil.toJSON(table);
                if (object != null && object instanceof JSONObject) {
                    mPlatform.getPlatformLoginInterface().userLogined(new PlatformUserInfo((JSONObject) object));
                }
            }
            return LuaValue.NIL;
        }
    }
}
