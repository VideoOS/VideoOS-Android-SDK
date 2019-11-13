package com.taobao.luaview.userdata.kit;

import android.os.Bundle;
import android.util.Pair;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.VisionUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_DATA;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MSG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY;
import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * Created by Lucas on 2019/8/2.
 */
@LuaViewLib(revisions = {"20190802已对标"})
public class UDApplet extends BaseLuaTable {


    public UDApplet(Globals globals, LuaValue metatable, Platform platform) {
        super(globals, metatable);
        set("appletSize", new AppletSize());// 返回视联网小程序容器size
        set("showRetryPage", new RetryPage());// 显示重试页面
        set("showErrorPage", new ErrorPage());// 显示错误页面
        set("canGoBack", new CanGoBack(platform));// 是否能够返回上一页
        set("goBack", new GoBack(platform));// 返回上一页
        set("closeView", new CloseView(platform));// 关闭当前容器
    }

    class AppletSize extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            Pair<Float, Float> pair = VisionUtil.getVisionProgramSize();

            LuaValue[] luaValue = new LuaValue[]{LuaValue.valueOf(pair.first), LuaValue.valueOf(pair.second)};
            return LuaValue.varargsOf(luaValue);
        }
    }

    class RetryPage extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 2);  //key
            LuaValue data = args.arg(fixIndex + 3);  //data
            String msg = luaValueToString(target);

            Bundle bundle = new Bundle();
            bundle.putString(CONSTANT_MSG, msg);
            bundle.putBoolean(CONSTANT_NEED_RETRY, true);
            bundle.putString(CONSTANT_DATA, luaValueToString(data));
            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);


            return LuaValue.valueOf(msg);
        }
    }

    class ErrorPage extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 2);  //key
            String msg = luaValueToString(target);
            Bundle bundle = new Bundle();
            bundle.putString(CONSTANT_MSG, msg);
            bundle.putBoolean(CONSTANT_NEED_RETRY, false);
            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);
            return LuaValue.valueOf(msg);
        }
    }

    class CanGoBack extends VarArgFunction {

        private Platform platform;

        public CanGoBack(Platform platform) {
            super();
            this.platform = platform;
        }


        @Override
        public Varargs invoke(Varargs args) {
            if (platform != null && platform.getAppletListener() != null) {
                return LuaValue.valueOf(platform.getAppletListener().canGoBack());
            }
            return LuaValue.valueOf(true);
        }
    }


    class GoBack extends VarArgFunction {

        private Platform platform;

        public GoBack(Platform platform) {
            super();
            this.platform = platform;
        }


        @Override
        public Varargs invoke(Varargs args) {
            if (platform != null && platform.getAppletListener() != null) {
                platform.getAppletListener().goBack();
            }
            return LuaValue.NIL;
        }
    }

    class CloseView extends VarArgFunction {

        private Platform platform;

        public CloseView(Platform platform) {
            super();
            this.platform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (platform != null && platform.getAppletListener() != null) {
                platform.getAppletListener().closeView();
            }
            return LuaValue.NIL;
        }
    }

}
