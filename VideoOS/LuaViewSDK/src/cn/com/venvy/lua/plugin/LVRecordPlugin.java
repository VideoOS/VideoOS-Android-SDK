package cn.com.venvy.lua.plugin;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.Platform;
import cn.com.venvy.common.interf.IPlatformRecordInterface;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static android.R.attr.data;

/**
 * Created by lgf on 2020/2/26.
 */

public class LVRecordPlugin {
    public static void install(VenvyLVLibBinder venvyLVLibBinder, Platform platform) {
        venvyLVLibBinder.set("acrRecordStart", new AcrRecordStart(platform));
        venvyLVLibBinder.set("acrRecordEnd", new AcrRecordEnd(platform));
    }

    /**
     * 录制开始事件
     */
    private static class AcrRecordStart extends VarArgFunction {
        private Platform mPlatform;

        AcrRecordStart(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (mPlatform != null && mPlatform.getPlatformRecordInterface() != null) {
                mPlatform.getPlatformRecordInterface().startRecord();
            }
            return LuaValue.NIL;
        }
    }

    /**
     * 录制开始事件
     */
    private static class AcrRecordEnd extends VarArgFunction {
        private Platform mPlatform;

        AcrRecordEnd(Platform platform) {
            this.mPlatform = platform;
        }

        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                if (mPlatform != null && mPlatform.getPlatformRecordInterface() != null) {
                    final LuaFunction callback = LuaUtil.getFunction(args, fixIndex + 1);
                    if (callback == null) {
                        return LuaValue.NIL;
                    }
                    mPlatform.getPlatformRecordInterface().endRecord(new IPlatformRecordInterface.RecordCallback() {
                        @Override
                        public void onRecordResult(byte[] data) {
                            LuaUtil.callFunction(callback, LuaValue.valueOf(data));
                        }

                        @Override
                        public void onRecordResult(String filePath) {
                            LuaUtil.callFunction(callback, LuaValue.valueOf(filePath));
                        }
                    });
                }
            }
            return LuaValue.NIL;
        }
    }
}
