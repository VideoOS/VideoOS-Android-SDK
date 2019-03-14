package cn.com.venvy.lua.plugin;

import android.net.Uri;
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
import cn.com.venvy.common.router.PostInfo;
import cn.com.venvy.common.router.VenvyRouterManager;
import cn.com.venvy.common.utils.VenvyBase64;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * lua路由插件
 * Created by Arthur on 2017/8/21.
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
                info.withTargetViewParent(mPlatform.getContentViewGroup()).withTargetPlatform("platform", mPlatform).navigation();
                return LuaValue.TRUE;
            }
            return LuaValue.FALSE;
        }
    }
}
