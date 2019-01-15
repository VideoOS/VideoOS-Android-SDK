package cn.com.venvy.lua;

import android.content.Context;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.global.LuaViewCore;

import org.luaj.vm2.LuaValue;


/**
 * Created by mac on 18/2/1.
 */

public class VenvyLuaView extends LuaView {


    /**
     * @param context     View级别的Context
     * @param luaViewCore
     * @param metaTable
     */
    public VenvyLuaView(Context context, LuaViewCore luaViewCore, LuaValue metaTable) {
        super(context, luaViewCore, metaTable);
    }

    public static void createAsync(final Context context, final CreatedCallback createdCallback) {
        LuaViewCore.createAsync(context, new LuaViewCore.CreatedCallback() {
            @Override
            public void onCreated(LuaViewCore luaViewCore) {
                LuaView luaView = createLuaView(context, luaViewCore);
                if (createdCallback != null) {
                    createdCallback.onCreated(luaView);
                }
            }
        });
    }

    public static LuaView create(Context context) {
        LuaViewCore luaViewCore = LuaViewCore.create(context);
        return createLuaView(context, luaViewCore);
    }

    static LuaView createLuaView(Context context, LuaViewCore luaViewCore) {
        LuaView luaView = new VenvyLuaView(context, luaViewCore, LuaView.createMetaTableForLuaView());
        luaViewCore.setRenderTarget(luaView);
        luaViewCore.setWindowUserdata(luaView.getUserdata());
        return luaView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }
}
