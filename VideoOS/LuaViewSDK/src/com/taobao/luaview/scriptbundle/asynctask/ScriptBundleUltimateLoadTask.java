/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle.asynctask;

import android.content.Context;

import com.taobao.luaview.cache.AppCache;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleDownloadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleLoadDelegate;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleUnpackDelegate;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * download lua script bundle from server and load as a ScriptBundle
 *
 * @author song
 */
public class ScriptBundleUltimateLoadTask extends BaseAsyncTask<String, Integer, ScriptBundle> {
    Context mContext;
    LuaScriptLoader.ScriptLoaderCallback mScriptLoaderCallback;

    public ScriptBundleUltimateLoadTask(Context context, LuaScriptLoader.ScriptLoaderCallback scriptLoaderCallback) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
        this.mScriptLoaderCallback = scriptLoaderCallback;
    }

    public void load(String... params) {
        super.executeInPool(params);
    }


    /**
     * 接受两个参数，第一个是下载的url，第二个是存储的地址
     *
     * @param params
     * @return
     */
    @Override
    public ScriptBundle doInBackground(String... params) {

         String url = params[0];
         String destFolderPath = LuaScriptManager.buildScriptBundleFolderPath(url);
         String sha256 = params.length > 1 ? params[1] : null;

        ScriptBundle scriptBundle = null;

        if (LuaScriptManager.existsScriptBundle(url)) {//读取并加载
            scriptBundle = AppCache.getCache(AppCache.CACHE_SCRIPTS).getLru(url);
            if (scriptBundle != null) {
                return scriptBundle;
            } else {

                scriptBundle = ScriptBundleUnpackDelegate.loadBundle(LuaScriptManager.isLuaBytecodeUrl(url), url, destFolderPath);//TODO 性能瓶颈

            }

        } else {//下载解压加载
            ScriptBundleDownloadDelegate downloadDelegate = new ScriptBundleDownloadDelegate(url, sha256);
            HttpURLConnection connection = downloadDelegate.createHttpUrlConnection();
            InputStream inputStream = downloadDelegate.downloadAsStream(connection);

            if (inputStream != null) {
                scriptBundle = ScriptBundleUnpackDelegate.unpack(url, inputStream);//unpack
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        scriptBundle = new ScriptBundleLoadDelegate().load(mContext, scriptBundle);//解密脚本或者加载Prototype

        if (scriptBundle != null) {

            if (url != null) {
                scriptBundle.setUrl(url);
                scriptBundle.setBaseFilePath(destFolderPath);
            }

            //cache
            AppCache.getCache(AppCache.CACHE_SCRIPTS).putLru(url != null ? url : scriptBundle.getUrl(), scriptBundle);
        }

        return scriptBundle;
    }


    @Override
    protected void onCancelled() {
        callLoaderCallback(null);
    }

    @Override
    protected void onCancelled(ScriptBundle scriptBundle) {
        callLoaderCallback(scriptBundle);
    }

    @Override
    protected void onPostExecute(ScriptBundle unzippedScripts) {
        callLoaderCallback(unzippedScripts);
    }

    void callLoaderCallback(ScriptBundle unzippedScripts) {
        if (mScriptLoaderCallback != null) {
            mScriptLoaderCallback.onScriptLoaded(unzippedScripts);
        }
    }
}