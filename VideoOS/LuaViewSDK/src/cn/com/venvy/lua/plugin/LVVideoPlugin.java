package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.VideoPlayerSize;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

public class LVVideoPlugin {

    private static SdkVersion sSdkVersion;
    private static IsDebug sIsDebug;
    private static ChangeEnvironment sChangeEnvironment;

    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("sdkVersion", sSdkVersion == null ? sSdkVersion = new SdkVersion() : sSdkVersion);
        venvyLVLibBinder.set("isDebug", sIsDebug == null ? sIsDebug = new IsDebug() : sIsDebug);
        venvyLVLibBinder.set("setDebug", sChangeEnvironment == null ? sChangeEnvironment = new ChangeEnvironment() : sChangeEnvironment);
        venvyLVLibBinder.set("getVideoSize", new VideoSize(platform));
        venvyLVLibBinder.set("currentDirection", new CurrentScreenDirection(platform));
        venvyLVLibBinder.set("isFullScreen", new IsFullScreen(platform));
        venvyLVLibBinder.set("appKey", new AppKey(platform));
        venvyLVLibBinder.set("appSecret", new AppSecret(platform));
        venvyLVLibBinder.set("nativeVideoID", new GetVideoID(platform));
        venvyLVLibBinder.set("platformID", new GetPlatformId(platform));
        venvyLVLibBinder.set("getVideoCategory", new GetCategory(platform));
        venvyLVLibBinder.set("getConfigExtendJSONString", new GetExtendJSONString(platform));
        venvyLVLibBinder.set("osType", new OsType(platform));

    }


    private static class ChangeEnvironment extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            Integer type = LuaUtil.getInt(args, fixIndex + 1);
            if (type != null) {
                DebugStatus.EnvironmentStatus status = DebugStatus.EnvironmentStatus.getStatusByIntType(type);
                DebugStatus.changeEnvironmentStatus(status);
            }
            return LuaValue.TRUE;
        }
    }

    private static class GetExtendJSONString extends VarArgFunction {
        private Platform mPlatform;

        GetExtendJSONString(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getPlatformInfo().getExtendJSONString() != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getExtendJSONString()) : LuaValue.NIL;
        }
    }

    private static class GetCategory extends VarArgFunction {
        private Platform mPlatform;

        GetCategory(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getPlatformInfo().getVideoCategory() != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getVideoCategory()) : LuaValue.NIL;
        }
    }


    private static class GetPlatformId extends VarArgFunction {
        private Platform mPlatform;

        GetPlatformId(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getPlatformInfo().getThirdPlatformId() != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getThirdPlatformId()) : LuaValue.NIL;
        }
    }

    private static class GetVideoID extends VarArgFunction {
        private Platform mPlatform;

        GetVideoID(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getPlatformInfo().getVideoId() != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getVideoId()) : LuaValue.NIL;
        }
    }

    private static class IsFullScreen extends VarArgFunction {
        private Platform mPlatform;

        IsFullScreen(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            int value = mPlatform != null ? mPlatform.getPlatformInfo().getInitDirection().getId() : ScreenStatus.SMALL_VERTICAL.getId();

            boolean b = value == ScreenStatus.FULL_VERTICAL.getId();
            VenvyLog.i("IsFullScreen b= " + b);
            return LuaValue.valueOf(b);
        }
    }

    private static class CurrentScreenDirection extends VarArgFunction {
        private Platform mPlatform;

        CurrentScreenDirection(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getInitDirection().getId()) : LuaValue.valueOf(ScreenStatus.SMALL_VERTICAL.getId());
        }
    }


    /**
     * 获取AppKey
     */
    private static class AppKey extends VarArgFunction {
        private Platform mPlatform;

        AppKey(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null ? LuaValue.valueOf(mPlatform.getPlatformInfo().getAppKey()) : LuaValue.NIL;
        }
    }

    /**
     * 获取AppSecret
     */
    private static class AppSecret extends VarArgFunction {
        private Platform mPlatform;

        AppSecret(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getPlatformInfo() != null && !TextUtils.isEmpty(mPlatform.getPlatformInfo().getAppSecret()) ? LuaValue.valueOf(mPlatform.getPlatformInfo().getAppSecret()) : LuaValue.valueOf(cn.com.venvy.AppSecret.getAppSecret(mPlatform));
        }
    }

    /**
     * 获取sdk版本号
     */
    private static class SdkVersion extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(Config.SDK_VERSION != null ? Config.SDK_VERSION : "");
        }
    }


    private static class VideoSize extends VarArgFunction {
        private Platform mPlatform;

        VideoSize(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {

            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            Integer type = LuaUtil.getInt(args, fixIndex + 1);

            if (mPlatform != null) {
                float width = 0.0f;
                float height = 0.0f;
                float marginTop = 0.0f;

                if (type != null) {
                    switch (type) {
                        case 0: // 竖屏小屏
                            if (mPlatform.getMediaControlListener() != null) {
                                VideoPlayerSize videoPlayerSize = mPlatform.getMediaControlListener().getVideoSize();
                                if (videoPlayerSize != null && videoPlayerSize.mVerVideoHeight > 0) {
                                    width = DimenUtil.pxToDpi(videoPlayerSize.mVerVideoWidth);
                                    height = DimenUtil.pxToDpi(videoPlayerSize.mVerVideoHeight);
                                    marginTop = DimenUtil.pxToDpi(videoPlayerSize.mPortraitSmallScreenOriginY);
                                    break;
                                }
                            }
                            width = DimenUtil.pxToDpi(mPlatform.getPlatformInfo().getVerVideoWidth());
                            height = DimenUtil.pxToDpi(mPlatform.getPlatformInfo().getVerVideoHeight());
                            break;

                        case 1: // 竖屏全屏
                            if (mPlatform.getMediaControlListener() != null) {
                                VideoPlayerSize videoPlayerSize = mPlatform.getMediaControlListener().getVideoSize();
                                if (videoPlayerSize != null && videoPlayerSize.mVerVideoHeight > 0) {
                                    width = DimenUtil.pxToDpi(videoPlayerSize.mHorVideoWidth);
                                    height = DimenUtil.pxToDpi(videoPlayerSize.mHorVideoHeight);
                                    marginTop = DimenUtil.pxToDpi(videoPlayerSize.mPortraitSmallScreenOriginY);
                                    break;
                                }
                            }
                            width = DimenUtil.pxToDpi(Math.min(mPlatform.getPlatformInfo().getVideoWidth(), mPlatform.getPlatformInfo().getVideoHeight()));
                            height = DimenUtil.pxToDpi(Math.max(mPlatform.getPlatformInfo().getVideoWidth(), mPlatform.getPlatformInfo().getVideoHeight()));
                            break;

                        default: //横屏全屏
                            if (mPlatform.getMediaControlListener() != null) {
                                VideoPlayerSize videoPlayerSize = mPlatform.getMediaControlListener().getVideoSize();
                                if (videoPlayerSize != null && videoPlayerSize.mVerVideoHeight > 0) {
                                    width = DimenUtil.pxToDpi(videoPlayerSize.mHorVideoWidth);
                                    height = DimenUtil.pxToDpi(videoPlayerSize.mHorVideoHeight);
                                    marginTop = DimenUtil.pxToDpi(videoPlayerSize.mPortraitSmallScreenOriginY);
                                    break;
                                }
                            }
                            width = DimenUtil.pxToDpi(mPlatform.getPlatformInfo().getVideoWidth());
                            height = DimenUtil.pxToDpi(mPlatform.getPlatformInfo().getVideoHeight());
                            break;
                    }
                }
                LuaValue[] luaValue = new LuaValue[]{LuaValue.valueOf(width), LuaValue.valueOf(height), LuaValue.valueOf(marginTop)};
                return LuaValue.varargsOf(luaValue);
            } else {
                return LuaValue.NIL;
            }

        }
    }

    /**
     * 判断sdk是否是debug状态
     */
    private static class IsDebug extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            return LuaValue.valueOf(DebugStatus.getCurrentEnvironmentStatus().getEnvironmentValue());
        }
    }

    private static class OsType extends VarArgFunction {
        private Platform mPlatform;

        OsType(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            int value = mPlatform != null ? mPlatform.getPlatformInfo().getVideoType().getId() : VideoType.VIDEOOS.getId();
            return LuaValue.valueOf(value);
        }
    }
}
