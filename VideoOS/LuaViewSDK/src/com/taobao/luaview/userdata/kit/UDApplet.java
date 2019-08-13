package com.taobao.luaview.userdata.kit;

import android.os.Bundle;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.AndroidUtil;
import com.taobao.luaview.util.DimenUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;
import cn.com.videopls.pub.R;

import static cn.com.venvy.App.getContext;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MSG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY;
import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * Created by Lucas on 2019/8/2.
 */
@LuaViewLib(revisions = {"20190802已对标"})
public class UDApplet extends BaseLuaTable {


    public UDApplet(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        set("appletSize", new AppletSize());// 返回视联网小程序容器size
        set("showRetryPage", new RetryPage());// 显示重试页面
        set("showErrorPage", new ErrorPage());// 显示错误页面
    }

    class AppletSize extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            float width = 222;
            float height = DimenUtil.pxToDpi(AndroidUtil.getScreenHeight(getContext()));
            LuaValue[] luaValue = new LuaValue[]{LuaValue.valueOf(width), LuaValue.valueOf(height)};
            return LuaValue.varargsOf(luaValue);
        }
    }

    class RetryPage extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 1);  //key
            String msg = luaValueToString(target);

            Bundle bundle = new Bundle();
            bundle.putString(CONSTANT_MSG, msg);
            bundle.putBoolean(CONSTANT_NEED_RETRY, true);
            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);


            return LuaValue.valueOf(msg);
        }
    }

    class ErrorPage extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 1);  //key
            String msg = luaValueToString(target);
            Bundle bundle = new Bundle();
            bundle.putString(CONSTANT_MSG, msg);
            bundle.putBoolean(CONSTANT_NEED_RETRY, false);
            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);

            return LuaValue.valueOf(msg);
        }
    }


}
