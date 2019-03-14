package cn.com.venvy.lua.plugin;

import android.text.TextUtils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.IOException;

import cn.com.venvy.common.utils.VenvyGzipUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * 压缩插件
 * Created by Arthur on 2017/8/21.
 */

public class LVCompressPlugin {

    private static ZipString sZipString;
    private static UnZipString sUnZipString;
    private static UnZipFile sUnZipFile;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("zipString", sZipString == null ? sZipString = new ZipString() : sZipString);
        venvyLVLibBinder.set("unZipString", sUnZipString == null ? sUnZipString = new UnZipString() : sUnZipString);
        venvyLVLibBinder.set("unZipFile", sUnZipFile == null ? sUnZipFile = new UnZipFile() : sUnZipFile);
    }

    /**
     * zip压缩字符串
     */
    private static class ZipString extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue target = args.arg(fixIndex + 1);
            String zipString = luaValueToString(target);
            try {
                zipString = VenvyGzipUtil.compress(zipString);
                return LuaValue.valueOf(zipString == null ? "" : zipString);
            } catch (IOException e) {
                return LuaValue.NIL;
            }
        }
    }

    /**
     * zip反解压字符串
     */
    private static class UnZipString extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue target = args.arg(fixIndex + 1);  //key
            String zipString = luaValueToString(target);
            zipString = VenvyGzipUtil.unCompress(zipString);
            return LuaValue.valueOf(zipString == null ? "" : zipString);
        }
    }

    private static class UnZipFile extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            try {
                int fixIndex = VenvyLVLibBinder.fixIndex(args);
                LuaValue zipFilePath = args.arg(fixIndex + 1);  //key
                String filePath = luaValueToString(zipFilePath);
                LuaValue targetPath = args.arg(fixIndex + 2);  //key
                String out = luaValueToString(targetPath);
                if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(out)) {
                    long value = VenvyGzipUtil.unzipFile(filePath, out,true);
                    return LuaValue.valueOf(value);
                }
            } catch (Exception e) {
                VenvyLog.e(LVCompressPlugin.class.getName(), e);
            }
            return LuaValue.NIL;
        }
    }
}
