package com.taobao.luaview.userdata.kit;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.util.VisionUtil;

import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.HashMap;

import cn.com.venvy.CacheConstants;
import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.WidgetInfo;
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
        set("appletSize", new AppletSize(platform));// 返回视联网小程序容器size
        set("showRetryPage", new RetryPage());// 显示重试页面
        set("showErrorPage", new ErrorPage());// 显示错误页面
        set("canGoBack", new CanGoBack(platform));// 是否能够返回上一页
        set("goBack", new GoBack(platform));// 返回上一页
        set("closeView", new CloseView(platform));// 关闭当前容器
        set("setStorageData", new SetStorageData(platform));// 存储本地数据
        set("getStorageData", new GetStorageData(platform));// 获取本地储存的数据
        set("openAds", new OpenAds(platform));// 获取本地储存的数据
        set("openApplet", new OpenApplet());// 打开新的容器
    }

    class AppletSize extends VarArgFunction {
        private Platform platform;

        public AppletSize(Platform platform) {
            this.platform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            Pair<Float, Float> pair = VisionUtil.getVisionProgramSize(platform.isNvgShow());

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


    class SetStorageData extends VarArgFunction {
        private Platform platform;

        public SetStorageData(Platform platform) {
            super();
            this.platform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue key = args.arg(fixIndex + 2);  // key
            LuaValue value = args.arg(fixIndex + 3);  // value
            LuaValue fileName = args.arg(fixIndex + 4);  // fileName
            CacheConstants.putCacheByFileName(platform.getContentViewGroup().getContext(), luaValueToString(fileName),
                    luaValueToString(key), luaValueToString(value));
            return LuaValue.NIL;
        }

    }

    class GetStorageData extends VarArgFunction {

        private Platform platform;

        public GetStorageData(Platform platform) {
            super();
            this.platform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue key = args.arg(fixIndex + 2);  // key
            LuaValue fileName = args.arg(fixIndex + 3);  // file sp 文件名
            String fileNameStr = luaValueToString(fileName);
            if (TextUtils.isEmpty(fileNameStr)) {
                //  没有传文件名，默认查询当前开发者ID对应的缓存数据
                String data = CacheConstants.getCacheByDeveloperId(platform.getContentViewGroup().getContext(), luaValueToString(key));
                return LuaValue.valueOf(data);
            } else {
                String jsonStr = CacheConstants.getVisionProgramId(platform.getContentViewGroup().getContext(), fileNameStr);
                return LuaValue.valueOf(jsonStr);
            }
        }
    }

    class OpenAds extends VarArgFunction {
        private Platform platform;

        public OpenAds(Platform platform) {
            super();
            this.platform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                final LuaTable table = LuaUtil.getTable(args, fixIndex + 2);
                try {
                    String str = JsonUtil.toString(table);
                    JSONObject jsonObject = new JSONObject(str);
//                    VenvyLog.d("openAds : " + jsonObject.toString());
                    if (jsonObject.has("targetType")) {
                        String targetType = jsonObject.optString("targetType");
                        JSONObject linkData = jsonObject.optJSONObject("linkData");
                        String downAPI = jsonObject.optString("downloadApkUrl");
                        String deepLink = linkData.optString("deepLink");
                        // targetType  1 落地页 2 deepLink 3 下载
                        if (targetType.equalsIgnoreCase("3")) {
                            JSONObject downloadTrackLink = jsonObject.optJSONObject("downloadTrackLink");
                            Bundle trackData = new Bundle();
                            trackData.putString(VenvyObservableTarget.Constant.CONSTANT_DOWNLOAD_API, downAPI);
                            trackData.putStringArray("isTrackLinks", JsonUtil.toStringArray(downloadTrackLink.optJSONArray("isTrackLinks")));
                            trackData.putStringArray("dsTrackLinks", JsonUtil.toStringArray(downloadTrackLink.optJSONArray("dsTrackLinks")));
                            trackData.putStringArray("dfTrackLinks", JsonUtil.toStringArray(downloadTrackLink.optJSONArray("dfTrackLinks")));
                            trackData.putStringArray("instTrackLinks", JsonUtil.toStringArray(downloadTrackLink.optJSONArray("instTrackLinks")));
                            trackData.putString("launchPlanId",jsonObject.optString("launchPlanId"));
                            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_DOWNLOAD_TASK, trackData);
                        } else {
                            // 走Native:widgetNotify()  逻辑
                            WidgetInfo.Builder builder = new WidgetInfo.Builder()
                                    .setWidgetActionType(WidgetInfo.WidgetActionType.ACTION_OPEN_URL)
                                    .setUrl("");
                            if (targetType.equalsIgnoreCase("1")) {
                                builder.setLinkUrl(downAPI);
                            } else if (targetType.equalsIgnoreCase("2")) {
                                builder.setDeepLink(deepLink);
                            }
                            WidgetInfo widgetInfo = builder.build();
                            if (platform.getWidgetClickListener() != null) {
                                platform.getWidgetClickListener().onClick(widgetInfo);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return LuaValue.NIL;
        }


    }

    class OpenApplet extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                final LuaTable luaTable = LuaUtil.getTable(args, fixIndex + 2);
                HashMap<String, String> map = LuaUtil.toMap(luaTable);
                if (map != null && map.size() > 0) {
                    String appletId = map.get("appletId");
                    String screenType = map.get("screenType");
                    String appType = map.get("appType");
                    String data = map.get("data");
                    String level = map.get("level");
                    // 发起一个视联网小程序
                    Bundle bundle = new Bundle();
                    bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, appletId);
                    bundle.putString(VenvyObservableTarget.KEY_ORIENTATION_TYPE, screenType);
                    bundle.putString(VenvyObservableTarget.Constant.CONSTANT_LEVEL, level);
                    bundle.putString(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE, appType);
                    if (!TextUtils.isEmpty(data)) {
                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_DATA, data);
                    }
                    ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, bundle);

                }

            }

            return LuaValue.NIL;
        }
    }

}
