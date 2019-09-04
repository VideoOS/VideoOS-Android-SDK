package cn.com.venvy.lua.plugin;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;
import java.util.Set;

import cn.com.venvy.Platform;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.router.PostInfo;
import cn.com.venvy.common.router.VenvyRouterManager;
import cn.com.venvy.common.utils.VenvyBase64;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua路由插件
 * Created by Arthur on 2017/8/21.
 * <p>
 * * A类小程序   L uaView://defaultLuaView?template=xxx.lua&id=xxx
 * * 跳转B类小程序     LuaView://applets?appletId=xxxx&type=x&appType=x(type: 1横屏,2竖屏,appType: 1 lua,2 H5)
 * *
 * * B类小程序容器内部跳转   LuaView://applets?appletId=xxxx&template=xxxx.lua&id=xxxx&(priority=x)
 */

public class LVEventPlugin {


    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("sendAction", new SendAction(platform));
    }

    /**
     * 路由器功能，有Native代码统一管理所有的Action逻辑
     */
    private static class SendAction extends VarArgFunction {

        private Platform mPlatform;

        SendAction(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (mPlatform == null || mPlatform.getContentViewGroup() == null) {
                return LuaValue.FALSE;
            }
            if (args.narg() > fixIndex) {
                final String urlString = LuaUtil.getString(args, fixIndex + 1);
                final LuaTable table = LuaUtil.getTable(args, fixIndex + 2);
                if (TextUtils.isEmpty(urlString)) {
                    return LuaValue.FALSE;
                }
                Uri uri = Uri.parse(new String(VenvyBase64.decode(urlString)));
                PostInfo info = VenvyRouterManager.getInstance().setUri(uri);
                Set<String> params = uri.getQueryParameterNames();
                HashMap<String, String> map = new HashMap<>();
                if (table != null) {
                    map.put("data", JsonUtil.toString(table));
                }
                if (params != null && params.size() > 0) {
                    for (String key : params) {
                        String value = uri.getQueryParameter(key);
                        if (!TextUtils.isEmpty(value)) {
                            map.put(key, value);
                        }
                    }
                }
                if (map.size() > 0) {
                    info.withSerializable("data", map);
                }
                String protocolHost = uri.getHost();
                if (protocolHost.equalsIgnoreCase("defaultLuaView")) {
                    // A类容器内部跳转
                    info.withTargetViewParent(mPlatform.getContentViewGroup()).withTargetPlatform("platform", mPlatform).navigation();
                } else if (protocolHost.equalsIgnoreCase("applets")) {
                    String type = info.getBundle().getString("type");
                    String appType = info.getBundle().getString("appType");
                    if(TextUtils.isEmpty(appType)){
                        VenvyLog.d("appType is null");
                        // appType为空默认指定为lua
                        appType = String.valueOf(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE_LUA);
                    }
                    VenvyLog.d("type is "+type+" ， appType is "+appType);
                    if(TextUtils.isEmpty(type)){
                        // B类小程序内部跳转
                        info.withTargetViewParent(mPlatform.getContentViewGroup()).withTargetPlatform("platform", mPlatform).navigation();

//                        Bundle bundle = new Bundle();
//                        bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, info.getBundle().getString("appletId"));
//                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_TEMPLATE, info.getBundle().getString("template"));
//                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_ID, info.getBundle().getString("id"));
//                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_DATA, JsonUtil.toString(table));
                        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM, null);
                    }else{
                        // 发起一个视联网小程序
                        Bundle bundle = new Bundle();
                        bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, info.getBundle().getString("appletId"));
                        bundle.putString(VenvyObservableTarget.KEY_ORIENTATION_TYPE, type);
                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE, appType);
                        if (table != null) {
                            bundle.putString(VenvyObservableTarget.Constant.CONSTANT_DATA, JsonUtil.toString(table));
                        }
                        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, bundle);
                    }


                }
                return LuaValue.TRUE;
            }
            return LuaValue.FALSE;
        }
    }
}
