package cn.com.venvy.lua.plugin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.App;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * Created by videojj_pls on 2018/12/25.
 */

public class LVCopyPlugin {
    private static CopyStringToPasteBoard sCopyStringToPasteBoard;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("copyStringToPasteBoard", sCopyStringToPasteBoard == null ? sCopyStringToPasteBoard = new LVCopyPlugin.CopyStringToPasteBoard() : sCopyStringToPasteBoard);
    }

    /**
     * zip压缩字符串
     */
    private static class CopyStringToPasteBoard extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue target = args.arg(fixIndex + 1);
            String copyStringToPasteBoard = luaValueToString(target);
            if (!TextUtils.isEmpty(copyStringToPasteBoard)) {
                ClipboardManager systemService = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                systemService.setPrimaryClip(ClipData.newPlainText("text", copyStringToPasteBoard));
                return LuaValue.NIL;
            } else {
                return LuaValue.NIL;
            }
        }
    }
}

