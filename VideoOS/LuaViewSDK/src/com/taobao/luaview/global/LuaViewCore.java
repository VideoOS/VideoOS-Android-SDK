/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.taobao.luaview.fun.binder.ui.UICustomPanelBinder;
import com.taobao.luaview.provider.ImageProvider;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.LVCustomPanel;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Prototype;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.IOException;
import java.io.InputStream;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.utils.VenvyStringUtil;

/**
 * Core of LuaView functions
 *
 * @author song
 * @date 17/2/22
 * 主要功能描述
 * 修改描述
 * 下午5:04 song XXX
 */
public class LuaViewCore {

    //image provider class
    static Class<? extends ImageProvider> mImageProviderClazz;

    //provider for loading image
    static ImageProvider mImageProvider;

    //Context
    Context mContext;

    //globals
    public Globals mGlobals;

    //userdata for render target
    UDView mWindowUserdata;

    /**
     * 执行lua window的callback函数
     *
     * @param calbackName
     */
    public void executeWindowCallback(String calbackName, String data) {
        if (isContainWindowCallback()) {
            LuaUtil.callFunction(LuaUtil.getFunction(mWindowUserdata.getCallback(), calbackName, calbackName),
                    LuaValue.valueOf(data));
        }
    }


    public boolean isContainWindowCallback() {
        return mWindowUserdata != null
                && mWindowUserdata.getCallback() != null
                && mWindowUserdata.getCallback().istable();
    }

    public interface CreatedCallback {
        void onCreated(LuaViewCore luaViewCore);
    }

    //---------------------------------------静态方法------------------------------------------------

    /**
     * create a LuaViewCore
     *
     * @param context
     * @return
     */
    public static LuaViewCore create(Context context) {
        return createLuaViewCore(context, LuaViewManager.createGlobals());
    }

    /**
     * create LuaViewCore async （带返回值）
     *
     * @param context
     */
    public static LuaViewCore createAsync(Context context) {
        return createLuaViewCore(context, LuaViewManager.createGlobalsAsync());
    }

    /**
     * create LuaViewCore async（兼容老的，没必要包装一层SimpleTask）
     *
     * @param context
     * @param createdCallback
     */
    public static void createAsync(final Context context, final LuaViewCore.CreatedCallback createdCallback) {
        new SimpleTask1<LuaViewCore>() {
            @Override
            protected LuaViewCore doInBackground(Object... params) {
                return create(context);
            }

            @Override
            protected void onPostExecute(LuaViewCore luaViewCore) {
                if (createdCallback != null) {
                    createdCallback.onCreated(luaViewCore);
                }
            }
        }.executeInPool();
    }

    /**
     * create LuaViewCore and setup everything
     *
     * @param context
     * @param globals
     * @return
     */
    static LuaViewCore createLuaViewCore(Context context, Globals globals) {
        return new LuaViewCore(context, globals);
    }


    //-----------------------------------------load script------------------------------------------

    /**
     * 加载，可能是url，可能是Asset，可能是文件，也可能是脚本
     * url : http or https, http://[xxx] or https://[xxx]
     * TODO asset : folder or file, file://android_asset/[xxx]
     * TODO file : folder or file, file://[xxx]
     * TODO script: content://[xxx]
     *
     * @param urlOrFileOrScript
     * @return
     */
    public LuaViewCore load(String urlOrFileOrScript) {
        return load(urlOrFileOrScript, null, null);
    }

    public LuaViewCore load(String urlOrFileOrScript, LuaScriptLoader.ScriptExecuteCallback callback) {
        return load(urlOrFileOrScript, null, callback);
    }

    public LuaViewCore load(String urlOrFileOrScript, String sha256) {
        return load(urlOrFileOrScript, sha256, null);
    }

    public LuaViewCore load(String urlOrFileOrScript, String sha256, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (!TextUtils.isEmpty(urlOrFileOrScript)) {
            if (URLUtil.isNetworkUrl(urlOrFileOrScript)) {//url, http:// or https://
                loadUrl(urlOrFileOrScript, sha256, callback);
            } else {
                loadFile(urlOrFileOrScript, callback);
            }
            //TODO other schema
        } else if (callback != null) {
            callback.onScriptExecuted(null, false);
        }
        return this;
    }

    /**
     * 直接加载网络脚本
     *
     * @param url http://[xxx] or https://[xxx]
     * @return
     */
    public LuaViewCore loadUrl(String url, String sha256) {
        return loadUrl(url, sha256, null);
    }

    public LuaViewCore loadUrl(final String url, String sha256, final LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(url);
        if (!TextUtils.isEmpty(url)) {
            new LuaScriptLoader(mContext).load(url, sha256, new LuaScriptLoader.ScriptLoaderCallback() {
                @Override
                public void onScriptLoaded(ScriptBundle bundle) {
                    if (callback == null || !callback.onScriptPrepared(bundle)) {//脚本准备完成，且不第三方自己执行
                        loadScriptBundle(bundle, callback);
                    } else {
                        callback.onScriptExecuted(url, false);
                    }
                }
            });
        } else if (callback != null) {
            callback.onScriptExecuted(null, false);
        }
        return this;
    }


    public LuaViewCore loadFile(String luaFileName, LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri(luaFileName);
        if (!TextUtils.isEmpty(luaFileName)) {
            this.loadFileInternal(luaFileName, callback);//加载文件
        } else {
            if (callback != null) {
                callback.onScriptExecuted(getUri(), false);
            }
        }
        return this;
    }


    public LuaViewCore loadScript(String script, LuaScriptLoader.ScriptExecuteCallback callback) {
        updateUri("");
        if (!TextUtils.isEmpty(script)) {
            this.loadScriptInternal(new ScriptFile(script, VenvyStringUtil.md5Hex(script)), callback);
        } else {
            if (callback != null) {
                callback.onScriptExecuted(getUri(), false);
            }
        }
        return this;
    }


    public LuaViewCore loadScript(ScriptFile scriptFile, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptFile != null) {
            this.loadScriptInternal(scriptFile, callback);
        } else if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return this;
    }


    public LuaViewCore loadScriptBundle(ScriptBundle scriptBundle, LuaScriptLoader.ScriptExecuteCallback callback) {
        loadScriptBundle(scriptBundle, LuaResourceFinder.DEFAULT_MAIN_ENTRY, callback);
        return this;
    }

    public LuaViewCore loadScriptBundle(ScriptBundle scriptBundle, String mainScriptFileName, LuaScriptLoader.ScriptExecuteCallback callback) {
        if (scriptBundle != null) {
            if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
                mGlobals.getLuaResourceFinder().setScriptBundle(scriptBundle);
            }
            if (scriptBundle.containsKey(mainScriptFileName)) {
                loadScript(scriptBundle.getScriptFile(mainScriptFileName), callback);
                return this;
            }
        }
        if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return this;
    }

    /**
     * load prototype (lua bytecode or sourcecode)
     *
     * @param inputStream
     * @return
     */
    public LuaViewCore loadPrototype(final InputStream inputStream, final String name, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {
            @Override
            protected LuaValue doInBackground(Object... params) {
                try {
                    if (mGlobals != null) {
                        Prototype prototype = mGlobals.loadPrototype(inputStream, name, "bt");
                        if (prototype != null) {
                            return mGlobals.load(prototype, name);
                        }
                    }
                } catch (IOException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                LuaValue activity = CoerceJavaToLua.coerce(mContext);
                LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || !callback.onScriptCompiled(value, activity, viewObj)) {
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();
        return this;
    }
    //---------------------------------------注册函数----------------------------------------------

    /**
     * 加载一个binder，可以用作覆盖老功能
     * Lib 必须注解上 LuaViewLib
     *
     * @param binders
     * @return
     */
    public synchronized LuaViewCore registerLibs(LuaValue... binders) {
        if (mGlobals != null && binders != null) {
            for (LuaValue binder : binders) {
                mGlobals.tryLazyLoad(binder);
            }
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
    public synchronized LuaViewCore register(String luaName, Object obj) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName)) {
            LuaValue value = mGlobals.get(luaName);
            if (obj != value) {
                mGlobals.set(luaName, CoerceJavaToLua.coerce(obj));
            }
        }
        return this;
    }

    /**
     * 注册一个名称到该lua对象的命名空间中
     *
     * @param luaName
     * @param clazz
     * @return
     */
    public synchronized LuaViewCore registerPanel(String luaName, Class<? extends LVCustomPanel> clazz) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName) && (clazz != null && clazz.getSuperclass() == LVCustomPanel.class)) {
            LuaValue value = mGlobals.get(luaName);
            if (value == null || value.isnil()) {
                mGlobals.tryLazyLoad(new UICustomPanelBinder(clazz, luaName));
            }
        }
        return this;
    }

    /**
     * 解注册一个命名空间中的名字
     *
     * @param luaName
     * @return
     */
    public synchronized LuaViewCore unregister(String luaName) {
        if (mGlobals != null && !TextUtils.isEmpty(luaName)) {
            mGlobals.set(luaName, LuaValue.NIL);
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
        if (mGlobals != null && funName != null) {
            LuaValue callback = mGlobals.get(funName);
            return LuaUtil.callFunction(callback, objs);
        }
        return LuaValue.NIL;
    }

    //----------------------------------------Image Provider----------------------------------------

    /**
     * 注册ImageProvider
     */

    public static void registerImageProvider(Class<? extends ImageProvider> clazz) {
        mImageProviderClazz = clazz;
    }

    /**
     * 获取ImageProvider
     *
     * @return
     */
    public static ImageProvider getImageProvider() {
        if (mImageProvider == null && mImageProviderClazz != null) {
            try {
                mImageProvider = mImageProviderClazz.newInstance();
            } catch (Exception e) {
            }
        }
        return mImageProvider;
    }

    //----------------------------------------setup functions---------------------------------------

    /**
     * 设置使用标准语法
     *
     * @param standardSyntax
     */
    public void setUseStandardSyntax(boolean standardSyntax) {
        if (mGlobals != null) {
            mGlobals.setUseStandardSyntax(standardSyntax);
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     *
     * @param enable
     */
    public void setRefreshContainerEnable(boolean enable) {
        if (this.mGlobals != null) {
            this.mGlobals.isRefreshContainerEnable = enable;
        }
    }

    /**
     * 刷新容器是否可以刷新(用在RefreshCollectionView初始化的地方)
     */
    public boolean isRefreshContainerEnable() {
        return this.mGlobals == null || this.mGlobals.isRefreshContainerEnable;
    }


    public String getUri() {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            return mGlobals.getLuaResourceFinder().getUri();
        }
        return null;
    }

    public void setUri(String uri) {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            mGlobals.getLuaResourceFinder().setUri(uri);
        }
    }

    public Globals getGlobals() {
        return mGlobals;
    }

    /**
     * @param globals
     */
    LuaViewCore(Context context, Globals globals) {
        init(context);
        this.mContext = context;
        this.mGlobals = globals;
    }

    void init(Context context) {
        //常量初始化
        Constants.init(context);
        //初始化脚本管理
        LuaScriptManager.init(context);
    }

    //-----------------------------------------私有load函数------------------------------------------
    void updateUri(String uri) {
        if (mGlobals != null && mGlobals.getLuaResourceFinder() != null) {
            mGlobals.getLuaResourceFinder().setUri(uri);
        }
    }

    /**
     * 初始化
     *
     * @param luaFileName
     */
    LuaViewCore loadFileInternal(final String luaFileName, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {
            @Override
            protected LuaValue doInBackground(Object... params) {
                if (mGlobals != null) {
                    if (mGlobals.isInited) {
                        try {
                            return mGlobals.loadfile(luaFileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(16);
                            return doInBackground(params);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                LuaValue activity = CoerceJavaToLua.coerce(mContext);
                LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || !callback.onScriptCompiled(value, activity, viewObj)) {
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();//TODO 这里使用execute，而不是executeInPoll，与createGlobalAsync保持一致

        return this;
    }

    /**
     * 加载纯脚本
     *
     * @param scriptFile
     */

    LuaViewCore loadScriptInternal(final ScriptFile scriptFile, final LuaScriptLoader.ScriptExecuteCallback callback) {
        new SimpleTask1<LuaValue>() {//load async
            @Override
            protected LuaValue doInBackground(Object... params) {
                if (mGlobals != null) {
                    if (mGlobals.isInited) {
                        if (scriptFile != null) {//prototype
                            String filePath = scriptFile.getFilePath();
                            if (scriptFile.prototype != null) {//prototype
                                return mGlobals.load(scriptFile.prototype, filePath);
                            } else {//source code
                                try {
                                    return mGlobals.load(scriptFile.getScriptString(), filePath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(16);
                            return doInBackground(params);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(LuaValue value) {
                LuaValue activity = CoerceJavaToLua.coerce(mContext);
                LuaValue viewObj = CoerceJavaToLua.coerce(LuaViewCore.this);
                if (callback == null || !callback.onScriptCompiled(value, activity, viewObj)) {
                    //执行脚本，在主线程
                    executeScript(value, activity, viewObj, callback);
                }
            }
        }.executeInPool();//TODO 这里使用execute，而不是executeInPoll，与createGlobalAsync保持一致
        return this;
    }

    /**
     * 执行脚本
     *
     * @param value
     * @param activity
     * @param viewObj
     * @param callback
     */
    public boolean executeScript(LuaValue value, LuaValue activity, LuaValue viewObj, LuaScriptLoader.ScriptExecuteCallback callback) {
        try {
            if (mGlobals != null && value != null) {
                mGlobals.saveContainer(getRenderTarget());
                mGlobals.set("window", mWindowUserdata);//TODO 优化到其他地方?，设置window对象
                value.call(activity, viewObj);
                mGlobals.restoreContainer();
                if (callback != null) {
                    callback.onScriptExecuted(getUri(), true);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.onScriptExecuted(getUri(), false);
        }
        return false;
    }


    //----------------------------------------getter and setter-------------------------------------

    /**
     * set window userdata
     *
     * @param userdata
     * @return
     */
    public LuaViewCore setWindowUserdata(UDView userdata) {
        this.mWindowUserdata = userdata;
        return this;
    }

    /**
     * set render target
     *
     * @param viewGroup
     * @return
     */
    public LuaViewCore setRenderTarget(ViewGroup viewGroup) {
        if (mGlobals != null) {
            mGlobals.setRenderTarget(viewGroup);
        }
        return this;
    }

    /**
     * get render target
     *
     * @return
     */
    public ViewGroup getRenderTarget() {
        return mGlobals != null ? mGlobals.getRenderTarget() : null;
    }

    /**
     * 在onDetached的时候清空cache
     */
    public void onDetached() {
        clearCache();
    }

    /**
     * 销毁的时候从外部调用，清空所有外部引用
     */
    public synchronized void onDestroy() {
        clearCache();
        if (mGlobals != null) {
            mGlobals.onDestroy();
            mGlobals = null;
        }

        mContext = null;
        mWindowUserdata = null;
    }

    /**
     * 清空cache
     */
    void clearCache() {
        if (mGlobals != null) {
            mGlobals.clearCache();
        }
    }
}