package cn.com.venvy.lua.binder;


import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.lua.plugin.LVAesPlugin;
import cn.com.venvy.lua.plugin.LVCachePlugin;
import cn.com.venvy.lua.plugin.LVCallbackPlugin;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.venvy.lua.plugin.LVCompressPlugin;
import cn.com.venvy.lua.plugin.LVCopyPlugin;
import cn.com.venvy.lua.plugin.LVDevicePlugin;
import cn.com.venvy.lua.plugin.LVEventPlugin;
import cn.com.venvy.lua.plugin.LVHostPlugin;
import cn.com.venvy.lua.plugin.LVHttpPlugin;
import cn.com.venvy.lua.plugin.LVLoginPlugin;
import cn.com.venvy.lua.plugin.LVMd5Plugin;
import cn.com.venvy.lua.plugin.LVPlayerPlugin;
import cn.com.venvy.lua.plugin.LVPreLoadPlugin;
import cn.com.venvy.lua.plugin.LVRSAPlugin;
import cn.com.venvy.lua.plugin.LVReportPlugin;
import cn.com.venvy.lua.plugin.LVStatisticsPlugin;
import cn.com.venvy.lua.plugin.LVTableToJsonPlugin;
import cn.com.venvy.lua.plugin.LVUrlPlugin;
import cn.com.venvy.lua.plugin.LVVideoPlugin;
import cn.com.venvy.lua.plugin.LVViewManagerPlugin;


public class VenvyLVLibBinder extends BaseLuaTable implements Cloneable {

    public VenvyLVLibBinder(Globals globals, LuaValue metaTable) {
        super(globals, metaTable);
    }

    public static int fixIndex(Varargs args) {
        return args != null && args.arg1() instanceof VenvyLVLibBinder ? 1 : 0;
    }

    public static String luaValueToString(LuaValue luaValue) {
        String result = "";
        if (LuaUtil.isString(luaValue)) {
            result = luaValue.optjstring(null);
        }
        return result;
    }

    void installPlugin(VenvyNativeBinder venvyNativeBinder) {
        LVHttpPlugin.install(this, venvyNativeBinder.getHttpBridge());
        LVDevicePlugin.install(this, venvyNativeBinder.getPlatform());
        LVMd5Plugin.install(this);
        LVAesPlugin.install(this);
        LVRSAPlugin.install(this);
        LVCompressPlugin.install(this);
        LVUrlPlugin.install(this);
        LVViewManagerPlugin.install(this, venvyNativeBinder.getRootView());
        LVLoginPlugin.install(this, venvyNativeBinder.getPlatform());
        LVCachePlugin.install(this);
        LVCommonParamPlugin.install(this);
        LVTableToJsonPlugin.install(this);
        LVEventPlugin.install(this, venvyNativeBinder.getPlatform());
        LVReportPlugin.install(this);
        LVCopyPlugin.install(this);
        LVHostPlugin.install(this);
        LVVideoPlugin.install(this, venvyNativeBinder.getPlatform());
        LVPlayerPlugin.install(this, venvyNativeBinder.getPlatform());
        LVCallbackPlugin.install(this, venvyNativeBinder.getPlatform());
        LVPreLoadPlugin.install(this, venvyNativeBinder.getPlatform());
        LVStatisticsPlugin.install(this);
    }

    @Override
    protected VenvyLVLibBinder clone() {
        VenvyLVLibBinder o = null;
        try {
            o = (VenvyLVLibBinder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
