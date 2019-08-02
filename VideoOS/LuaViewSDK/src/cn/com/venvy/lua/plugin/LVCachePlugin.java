package cn.com.venvy.lua.plugin;

import android.app.Activity;
import android.content.SharedPreferences;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Map;
import java.util.Set;

import cn.com.venvy.App;
import cn.com.venvy.common.utils.VenvyPreferenceHelper;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

import static cn.com.venvy.lua.binder.VenvyLVLibBinder.luaValueToString;

/**
 * lua缓存数据插件
 * Created by mac on 17/12/7.
 */

public class LVCachePlugin {

    private static final String cacheFileName = "luaCache";
    private static GetCacheData mGetCacheData;
    private static SaveCacheData mSaveCacheData;
    private static FuzzyCacheData mFuzzyCacheData;
    private static DeleteBatchCacheData mDeleteBatchCacheData;

    public static void install(VenvyLVLibBinder venvyLVLibBinder) {
        venvyLVLibBinder.set("getCacheData", mGetCacheData == null ? mGetCacheData = new GetCacheData() : mGetCacheData);
        venvyLVLibBinder.set("saveCacheData", mSaveCacheData == null ? mSaveCacheData = new SaveCacheData() : mSaveCacheData);
        venvyLVLibBinder.set("getFuzzyCacheData", mFuzzyCacheData == null ? mFuzzyCacheData = new FuzzyCacheData() : mFuzzyCacheData);
        venvyLVLibBinder.set("deleteBatchCacheData", mDeleteBatchCacheData == null ? mDeleteBatchCacheData = new DeleteBatchCacheData() : mDeleteBatchCacheData);
    }

    private static class GetCacheData extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue luaKey = args.arg(fixIndex + 1);
            String key = luaValueToString(luaKey);
            String data = VenvyPreferenceHelper.getString(App.getContext(), cacheFileName, key, null);
            return data != null ? LuaValue.valueOf(data) : LuaValue.NIL;
        }
    }

    private static class SaveCacheData extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);

            LuaValue luaKey = args.arg(fixIndex + 1);
            LuaValue luaCache = args.arg(fixIndex + 2);
            String key = luaValueToString(luaKey);
            String data = luaValueToString(luaCache);
            VenvyPreferenceHelper.putString(App.getContext(), cacheFileName, key, data);
            return LuaValue.TRUE;
        }
    }

    private static class DeleteBatchCacheData extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                LuaTable table = LuaUtil.getTable(args, fixIndex + 1);
                Map<String, String> map = LuaUtil.toMap(table);
                if (map == null) {
                    return LuaValue.NIL;
                }
                SharedPreferences.Editor edit = App.getContext()
                        .getSharedPreferences(cacheFileName, Activity.MODE_PRIVATE)
                        .edit();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    edit.remove(entry.getValue()).apply();
                }
            }
            return LuaValue.NIL;
        }
    }

    private static class FuzzyCacheData extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            int fixIndex = VenvyLVLibBinder.fixIndex(args);
            LuaValue luaKey = args.arg(fixIndex + 1);
            String key = luaValueToString(luaKey);
            SharedPreferences spf = App.getContext().getSharedPreferences(cacheFileName, Activity.MODE_PRIVATE);
            final Map<String, ?> spfValues = spf.getAll();
            if (spfValues == null) {
                return null;
            }
            LuaTable table = new LuaTable();
            Set<String> keys = spfValues.keySet();
            for (String s : keys) {
                //包含指定key
                if (s.contains(key)) {
                    String value = (String) spfValues.get(s);
                    if (value == null) {
                        value = "";
                    }
                    table.set(LuaValue.valueOf(s), LuaValue.valueOf(value));
                }
            }
            return table;
        }
    }
}
