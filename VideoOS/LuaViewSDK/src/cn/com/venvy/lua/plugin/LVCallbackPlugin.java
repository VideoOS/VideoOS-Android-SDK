package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Map;

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
        venvyLVLibBinder.set("widgetNotify", new WidgetTableDelegate(platform));
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

    private static class WidgetTableDelegate extends VarArgFunction {
        private Platform mPlatform;

        WidgetTableDelegate(Platform platform) {
            super();
            this.mPlatform = platform;
        }

        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
            Map<String, String> map = LuaUtil.toMap(table);
            if (map == null || map.size() <= 0) {
                return LuaValue.NIL;
            }
            String eventType = map.get("eventType");
            String adID = map.get("adID");
            String adName = map.get("adName");
            String actionType = map.get("actionType");
            String actionString = map.get("actionString");
            String linkUrl = map.get("linkUrl");
            String deepLink = map.get("deepLink");
            String selfLink = map.get("selfLink");
            WidgetInfo.WidgetActionType widgetActionType = WidgetInfo.WidgetActionType.findTypeById(!TextUtils.isEmpty(actionType) ? Integer.valueOf(actionType) : 0);
            if (TextUtils.isEmpty(eventType)) {
                return LuaValue.NIL;
            }
            if (mPlatform == null) {
                return LuaValue.NIL;
            }

            WidgetInfo widgetInfo = new WidgetInfo.Builder()
                    .setAdId(adID)
                    .setWidgetActionType(widgetActionType)
                    .setUrl(actionString)
                    .setWidgetName(adName)
                    .setDeepLink(deepLink)
                    .setLinkUrl(linkUrl)
                    .setSelfLink(selfLink)
                    .build();
            switch (Integer.valueOf(eventType)) {
                case 1:
                    if (mPlatform.getPrepareShowListener() != null && adID != null) {
                        mPlatform.getPrepareShowListener().prepareShow(widgetInfo);
                    }
                    break;

                case 2:
                    if (mPlatform.getWidgetShowListener() != null && adID != null) {
                        mPlatform.getWidgetShowListener().onShow(widgetInfo);
                    }

                    break;

                case 3:
                    if (mPlatform.getWidgetClickListener() != null && adID != null) {
                        mPlatform.getWidgetClickListener().onClick(widgetInfo);
                    }
                    break;

                case 4:
                    if (mPlatform.getWidgetCloseListener() != null && adID != null) {
                        mPlatform.getWidgetCloseListener().onClose(widgetInfo);
                    }
                    break;
                case 5:
                    if (mPlatform.getWedgeListener() != null && adID != null) {
                        mPlatform.getWedgeListener().goBack();
                    }
                    break;
            }
            return LuaValue.NIL;
        }
    }
}
