/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;
import android.view.ViewGroup;

import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVCustomPanel;
import com.taobao.luaview.view.LVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.InputStream;

/**
 * LuaView 实现类
 *
 * @author song
 * @date 15/8/20
 */
public class LuaView extends LVViewGroup {

    //LuaViewCore
    LuaViewCore mLuaViewCore;

    /**
     * created callback
     */
    public interface CreatedCallback {
        void onCreated(LuaView luaView);
    }
    //---------------------------------------静态方法------------------------------------------------

    /**
     * create a luaview
     *
     * @param context
     * @return
     */
    public static LuaView create(Context context) {
        LuaViewCore luaViewCore = LuaViewCore.create(context);
        return createLuaView(context, luaViewCore);
    }

    /**
     * create LuaView async
     *
     * @param context
     * @return
     */
    public static LuaView createAsync(Context context) {
        LuaViewCore luaViewCore = LuaViewCore.createAsync(context);
        return createLuaView(context, luaViewCore);
    }


    /**
     * 当显示的时候调用
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * create LuaView async (兼容老的，可以使用无回调方法）
     *
     * @param context
     * @param createdCallback
     */
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

    static LuaView createLuaView(Context context, LuaViewCore luaViewCore) {
        LuaView luaView = new LuaView(context, luaViewCore, createMetaTableForLuaView());
        luaViewCore.setRenderTarget(luaView);
        luaViewCore.setWindowUserdata(luaView.getUserdata());
        return luaView;
    }
    //-----------------------------------------加载函数----------------------------------------------

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * asset : folder or file, file://android_asset/[xxx]
     * file : folder or file, file://[xxx]
     * script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaView load(String urlOrFileOrScript) {
        return load(urlOrFileOrScript, null, null);
    }

    public LuaView load(String urlOrFileOrScript, LuaScriptLoader.ScriptExecuteCallback callback) {
        return load(urlOrFileOrScript, null, callback);
    }

    public LuaView load(String urlOrFileOrScript, String sha256) {
        return load(urlOrFileOrScript, sha256, null);
    }

    public LuaView load(String urlOrFileOrScript, String sha256, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.load(urlOrFileOrScript, sha256, callback);
        }
        return this;
    }

    /**
     * 直接加载网络脚本
     *
     * @param url http://[xxx] or https://[xxx]
     * @return
     */
    public LuaView loadUrl(String url, String sha256) {
        return loadUrl(url, sha256, null);
    }

    public LuaView loadUrl(String url, String sha256, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadUrl(url, sha256, callback);
        }
        return this;
    }


    /**
     * 加载脚本库，必须在主进程中执行，先判断asset下是否存在，再去文件系统中查找
     *
     * @param luaFileName plain file name or file://[xxx]
     * @return
     */
    LuaView loadFile(String luaFileName) {
        return loadFile(luaFileName, null);
    }

    LuaView loadFile(String luaFileName, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadFile(luaFileName, callback);
        }
        return this;
    }

    /**
     * Load plain Script Code
     *
     * @param script
     * @return
     */
    public LuaView loadScript(String script) {
        return loadScript(script, null);
    }

    public LuaView loadScript(String script, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadScript(script, callback);
        }
        return this;
    }

    /**
     * 加载Script Bundle (zip file)
     *
     * @param scriptFile
     * @return
     */
    LuaView loadScript(ScriptFile scriptFile) {
        return loadScript(scriptFile, null);
    }

    LuaView loadScript(ScriptFile scriptFile, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadScript(scriptFile, callback);
        }
        return this;
    }

    /**
     * 加载 Script Bundle
     *
     * @param scriptBundle
     * @return
     */
    public LuaView loadScriptBundle(ScriptBundle scriptBundle) {
        return loadScriptBundle(scriptBundle, null);
    }

    public LuaView loadScriptBundle(ScriptBundle scriptBundle, LuaScriptLoader.ScriptExecuteCallback callback) {
        return loadScriptBundle(scriptBundle, LuaResourceFinder.DEFAULT_MAIN_ENTRY, callback);
    }

    public LuaView loadScriptBundle(ScriptBundle scriptBundle, String mainScriptFileName, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadScriptBundle(scriptBundle, mainScriptFileName, callback);
        }
        return this;
    }

    /**
     * load prototype (lua bytecode or sourcecode)
     *
     * @param inputStream
     * @return
     */
    public LuaView loadPrototype(InputStream inputStream, String name, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (mLuaViewCore != null) {
            mLuaViewCore.loadPrototype(inputStream, name, callback);
        }
        return this;
    }


    /**
     * execute script
     *
     * @param value
     * @param activity
     * @param viewObj
     * @param callback
     * @return
     */
    public boolean executeScript(LuaValue value, LuaValue activity, LuaValue viewObj, LuaScriptLoader.ScriptExecuteCallback callback) {
        return mLuaViewCore != null ? mLuaViewCore.executeScript(value, activity, viewObj, callback) : false;
    }
    //---------------------------------------注册函数------------------------------------------------

    /**
     * 加载一个binder，可以用作覆盖老功能
     * Lib 必须注解上 LuaViewLib
     *
     * @param binders
     * @return
     */
    public LuaView registerLibs(LuaValue... binders) {
        if (mLuaViewCore != null) {
            mLuaViewCore.registerLibs(binders);
        }
        return this;
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param obj
     * @return
     */
    public LuaView register(String luaName, Object obj) {
        if (mLuaViewCore != null) {
            mLuaViewCore.register(luaName, obj);
        }
        return this;
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param clazz
     * @return
     */
    public LuaView registerPanel(Class<? extends LVCustomPanel> clazz) {
        return registerPanel(clazz != null ? clazz.getSimpleName() : null, clazz);
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param clazz
     * @return
     */
    public LuaView registerPanel(String luaName, Class<? extends LVCustomPanel> clazz) {
        if (mLuaViewCore != null) {
            mLuaViewCore.registerPanel(luaName, clazz);
        }
        return this;
    }

    /**
     * 解注册一个命名空间中的名字
     *
     * @param luaName
     * @return
     */
    public LuaView unregister(String luaName) {
        if (mLuaViewCore != null) {
            mLuaViewCore.unregister(luaName);
        }
        return this;
    }

    //----------------------------------------call lua function-------------------------------------

    /**
     * 调用lua的某个全局函数
     *
     * @param funName
     * @param objs
     * @return
     */
    public Object callLuaFunction(String funName, Object... objs) {
        if (mLuaViewCore != null) {
            mLuaViewCore.callLuaFunction(funName, objs);
        }
        return LuaValue.NIL;
    }

    /**
     * 调用window.callback下的某个函数
     *
     * @param funName
     * @param objs
     * @return
     */
    public Varargs callWindowFunction(String funName, Object... objs) {
        if (funName != null) {
            UDView userdata = getUserdata();
            if (userdata != null) {
                LuaValue callbacks = userdata.getCallback();
                if (LuaUtil.isValid(callbacks)) {
                    return LuaUtil.callFunction(callbacks.get(funName), objs);
                }
            }
        }
        return LuaValue.NIL;
    }

    //----------------------------------------Image Provider----------------------------------------

    /**
     * 注册ImageProvider
     */
    public static void registerImageProvider(Class<? extends ImageProvider> clazz) {
        LuaViewCore.registerImageProvider(clazz);
    }

    /**
     * 获取ImageProvider
     *
     * @return
     */
    public static ImageProvider getImageProvider() {
        return LuaViewCore.getImageProvider();
    }

    //----------------------------------------setup functions---------------------------------------

    /**
     * 设置使用标准语法
     *
     * @param standardSyntax
     */
    public void setUseStandardSyntax(boolean standardSyntax) {
        if (mLuaViewCore != null) {
            mLuaViewCore.setUseStandardSyntax(standardSyntax);
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     *
     * @param enable
     */
    public void setRefreshContainerEnable(boolean enable) {
        if (mLuaViewCore != null) {
            mLuaViewCore.setRefreshContainerEnable(enable);
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     */
    public boolean isRefreshContainerEnable() {
        return mLuaViewCore != null ? mLuaViewCore.isRefreshContainerEnable() : true;
    }


    public String getUri() {
        return mLuaViewCore != null ? mLuaViewCore.getUri() : null;
    }

    public void setUri(String uri) {
        if (mLuaViewCore != null) {
            mLuaViewCore.setUri(uri);
        }
    }

    public Globals getGlobals() {
        return mLuaViewCore != null ? mLuaViewCore.getGlobals() : null;
    }

    public LuaViewCore getLuaViewCore() {
        return mLuaViewCore;
    }
    //-------------------------------------------私有------------------------------------------------

    /**
     * TODO 优化
     * 创建LuaView的methods，这里可以优化，实现更加优雅，其实就是将window注册成一个userdata，并且userdata是UDViewGroup
     *
     * @return
     */
    public static LuaTable createMetaTableForLuaView() {
        return LuaViewManager.createMetatable(UIViewGroupMethodMapper.class);
    }

    /**
     * @param context     View级别的Context
     * @param luaViewCore
     * @param metaTable
     */
    public LuaView(Context context, LuaViewCore luaViewCore, LuaValue metaTable) {
        super(context, luaViewCore.getGlobals(), metaTable, LuaValue.NIL);
        this.mLuaViewCore = luaViewCore;
    }

    //-----------------------------------------网络回调----------------------------------------------


    /**
     * 离开的时候调用
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLuaViewCore != null) {
            mLuaViewCore.onDetached();
        }
    }

    //----------------------------------------getter and setter-------------------------------------

    /**
     * create a default render target (viewgroup)
     *
     * @return
     */
    public ViewGroup createDefaultRenderTarget() {
        return new LVViewGroup(getGlobals(), createMetaTableForLuaView(), null);
    }

    /**
     * set render target
     *
     * @param viewGroup
     * @return
     */
    public LuaView setRenderTarget(ViewGroup viewGroup) {
        if (mLuaViewCore != null) {
            mLuaViewCore.setRenderTarget(viewGroup);
            //TODO 需要在这里设置setWindowUserdata(XXX)
        }
        return this;
    }

    /**
     * get render target remove by yanqiu
     *
     * @return
     */
//    private ViewGroup getRenderTarget() {
//        return mLuaViewCore != null ? mLuaViewCore.getRenderTarget() : this;
//    }

    //----------------------------------------生命周期 管理----------------------------------------

    /**
     * 销毁的时候从外部调用，清空所有外部引用
     */
    public void onDestroy() {
        if (mLuaViewCore != null) {
            mLuaViewCore.onDestroy();
            mLuaViewCore = null;
        }
    }
}
