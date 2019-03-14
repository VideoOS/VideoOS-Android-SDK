/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle;


import java.util.HashMap;
import java.util.Map;

/**
 * 脚本文件包，每一个加载的脚本包，加载成功后返回该bundle
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptBundle {
    //脚本网络地址
    String mUrl;

    //脚本本地地址
    String mBaseFilePath;

    //脚本文件
    HashMap<String, ScriptFile> mScriptFileMap;

    //是否是bytecode
    boolean isBytecode;


    public ScriptBundle() {
        mScriptFileMap = new HashMap<>();
    }

    public ScriptBundle addScript(ScriptFile scriptFile) {
        if (mScriptFileMap != null) {
            mScriptFileMap.put(scriptFile.fileName, scriptFile);
        }
        return this;
    }

    public int size() {
        return mScriptFileMap != null ? mScriptFileMap.size() : 0;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setBytecode(boolean isBytecode) {
        this.isBytecode = isBytecode;
    }

    public void setBaseFilePath(String mBaseFilePath) {
        this.mBaseFilePath = mBaseFilePath;
    }


    public String getUrl() {
        return mUrl;
    }

    public boolean isBytecode() {
        return isBytecode;
    }

    public Map<String, ScriptFile> getScriptFileMap() {
        return mScriptFileMap;
    }

    public boolean containsKey(String key) {
        return mScriptFileMap != null && mScriptFileMap.containsKey(key);
    }

    public ScriptFile getScriptFile(String key) {
        return mScriptFileMap != null ? mScriptFileMap.get(key) : null;
    }


}
