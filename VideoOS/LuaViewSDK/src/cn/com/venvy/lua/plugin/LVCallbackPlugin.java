package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.WidgetInfo;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * 回调插件
 * Created by Arthur on 2017/8/21.
 */

public class LVCallbackPlugin {


    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("widgetEvent", new WidgetDelegate(platform));
    }

    private static class WidgetDelegate extends VarArgFunction {

        private Platform mPlatform;

        WidgetDelegate(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            Integer type = LuaUtil.getInt(args, fixIndex + 1);//事件类型
            LuaValue adId = args.arg(fixIndex + 2);//广告ID
            LuaValue adName = args.arg(fixIndex + 3);//广告类型名
            LuaValue actionType = args.arg(fixIndex + 4);//处理类型
            WidgetInfo.WidgetActionType mWidgetActionType = WidgetInfo.WidgetActionType.findTypeById(actionType != null ? actionType.optint(0) : 0);
            LuaValue url = args.arg(fixIndex + 5);//外链Url
            if (type == null) {
                return LuaValue.NIL;
            }
            if (mPlatform == null) {
                return LuaValue.NIL;
            }
            String mAdId = adId != null ? adId.optjstring(null) : null;
            String mAdName = adName != null ? adName.optjstring(null) : null;
            String mUrl = url != null ? url.optjstring(null) : null;
            WidgetInfo widgetInfo = new WidgetInfo.Builder()
                    .setAdId(mAdId).setWidgetActionType(mWidgetActionType)
                    .setUrl(mUrl).setWidgetName(mAdName)
                    .build();
            switch (type) {
                case 1:
                    if (mPlatform.getPrepareShowListener() != null && mAdId != null) {
                        mPlatform.getPrepareShowListener().prepareShow(widgetInfo);
                    }
                    break;

                case 2:
                    if (mPlatform.getWidgetShowListener() != null && mAdId != null) {
                        mPlatform.getWidgetShowListener().onShow(widgetInfo);
                    }

                    break;

                case 3:
                    if (mPlatform.getWidgetClickListener() != null && mAdId != null) {
                        mPlatform.getWidgetClickListener().onClick(widgetInfo);
                    }
                    break;

                case 4:
                    if (mPlatform.getWidgetCloseListener() != null && mAdId != null) {
                        mPlatform.getWidgetCloseListener().onClose(widgetInfo);
                    }
                    break;
                case 5:
                    if (mPlatform.getWedgeListener() != null && mAdId != null) {
                        mPlatform.getWedgeListener().goBack();
                    }
                    break;
            }

            return LuaValue.NIL;
        }
    }
}
