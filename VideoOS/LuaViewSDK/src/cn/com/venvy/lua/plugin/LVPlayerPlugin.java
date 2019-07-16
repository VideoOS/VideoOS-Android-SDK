package cn.com.venvy.lua.plugin;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/***
 * 播放器相关插件
 */
public class LVPlayerPlugin {

    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("currentVideoTime", new CurrentVideoTime(platform));
        venvyLVLibBinder.set("videoDuration", new VideoDuration(platform));
    }

    /***
     * 获取播放器当前时间
     */
    private static class CurrentVideoTime extends VarArgFunction {
        private Platform mPlatform;

        CurrentVideoTime(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getMediaControlListener() != null ? LuaValue.valueOf(mPlatform.getMediaControlListener().getCurrentPosition()) : LuaValue.valueOf(0);
        }
    }

    /***
     * 获取播放器当前时间
     */
    private static class VideoDuration extends VarArgFunction {
        private Platform mPlatform;

        VideoDuration(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            return mPlatform != null && mPlatform.getMediaControlListener() != null ? LuaValue.valueOf(mPlatform.getMediaControlListener().getDuration()) : LuaValue.valueOf(0);
        }
    }
}
