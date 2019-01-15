package cn.com.venvy.lua.binder;

import android.view.ViewGroup;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.mapper.LuaViewLib;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

import java.lang.ref.WeakReference;

import cn.com.venvy.Platform;
import cn.com.venvy.lua.bridge.LVHttpBridge;

/**
 * lua Binder,提供了Android与Lua的公共api
 * :应该想法设置一个可插拔的插件功能
 * Created by Arthur on 2017/8/17.
 */
@LuaViewLib
public class VenvyNativeBinder extends BaseFunctionBinder {
    private Platform mPlatform;
    private WeakReference<ViewGroup> rootView;
    private LVHttpBridge httpBridge;

    public VenvyNativeBinder() {
        super("Native");
    }

    public void setPlatform(Platform platform) {
        this.mPlatform = platform;
    }

    public void setRootView(ViewGroup rootView) {
        this.rootView = rootView != null ? new WeakReference<>(rootView) : null;
    }

    public void setHttpBridge(LVHttpBridge httpBridge) {
        this.httpBridge = httpBridge;
    }

    public ViewGroup getRootView() {
        return rootView != null ? rootView.get() : null;
    }

    public Platform getPlatform() {
        return mPlatform;
    }

    public LVHttpBridge getHttpBridge() {
        return httpBridge;
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VenvyNativeBinder.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        VenvyLVLibBinder venvyLVLibBinder = new VenvyLVLibBinder(env.checkglobals(), metaTable);
        venvyLVLibBinder.installPlugin(this);
        return venvyLVLibBinder;
    }
}
