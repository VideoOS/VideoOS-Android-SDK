/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.asynctask.ScriptBundleUltimateLoadTask;

import org.luaj.vm2.LuaValue;

/**
 * script loader
 *
 * @author song
 * @date 15/11/10
 */
public class LuaScriptLoader {
    Context mContext;

    public LuaScriptLoader( Context context) {
        if (context != null) {
            try {
                this.mContext = context.getApplicationContext();
            } catch (Exception e) {
            }
        }
        LuaScriptManager.init(context);
    }

    public interface ScriptLoaderCallback {
        void onScriptLoaded( ScriptBundle bundle);//脚本加载
    }

    /**
     * 脚本运行回调
     */
    public interface ScriptExecuteCallback {
        /**
         * 脚本准备完毕, 返回true，表示需要自己处理执行，onScriptCompiled，onScriptExecuted不会被执行，返回false表示系统继续执行
         *
         * @param bundle
         * @return
         */
        boolean onScriptPrepared(ScriptBundle bundle);

        /**
         * 脚本编译完成，参数表示编译之后的结果，不保证一定被调用到
         * 返回true，表示需要自己处理执行，返回false，表示系统继续执行
         *
         * @param value
         */
        boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view);

        /**
         * 脚本执行完成，参数表示是否执行成功，保证一定被调用到
         *
         * @param uri             原始的加载url
         * @param executedSuccess
         */
        void onScriptExecuted(String uri, boolean executedSuccess);
    }


    /**
     * fetch a script from network (if needed) or from local file system
     *
     * @param url
     * @param callback
     */
    public void load( String url,  ScriptLoaderCallback callback) {
        load(url, null, callback);
    }

    /**
     * fetch a script from network (if needed) or from local file system
     *
     * @param url
     * @param sha256
     * @param callback
     */
    public void load( String url,  String sha256,  ScriptLoaderCallback callback) {
        new ScriptBundleUltimateLoadTask(mContext, callback).load(url, sha256);
    }


}
