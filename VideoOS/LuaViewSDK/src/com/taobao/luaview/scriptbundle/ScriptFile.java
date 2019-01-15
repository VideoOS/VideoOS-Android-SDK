/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle;

import org.luaj.vm2.Prototype;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 脚本文件类，封装了从服务端下发的脚本，包含脚本名称以及对应的代码
 *
 * @author song
 * @date 15/11/10
 */
public class ScriptFile {
    //脚本的下载地址
    public String url;

    //脚本文件本地地址
    public String baseFilePath;

    //文件名称
    public String fileName;

    //sign file name
    public String signFileName;

    //语法树原型
    public Prototype prototype;

    //lua源码二进制数据
    public byte[] scriptData;

    //lua源码对应的加密数据
    public byte[] signData;


    public ScriptFile(String script, String fileName) {
        this(null, null, fileName, script != null ? script.getBytes() : null);
    }

    public ScriptFile(String url, String baseFilePath, String fileName, byte[] scriptData) {
        this(url, baseFilePath, fileName, scriptData, null);
    }

    public ScriptFile(String url, String baseFilePath, String fileName, byte[] scriptData, byte[] signData) {
        this.url = url;
        this.baseFilePath = baseFilePath;
        this.fileName = LuaScriptManager.changeSuffix(fileName, LuaScriptManager.POSTFIX_LUA);//main.lv->main.lua 这里使用真实的lua脚本名称，因为这里的脚本都是被解密过的
        this.signFileName = fileName + LuaScriptManager.POSTFIX_SIGN;//sign文件名称, 使用的是fileName，main.lv.sign
        this.scriptData = scriptData;
        this.signData = signData;
    }

    public String getScriptString() {
        return this.scriptData != null ? new String(this.scriptData) : null;
    }

    public String getFilePath() {
        return baseFilePath != null && baseFilePath.endsWith("/") ?
                new StringBuffer().append(baseFilePath).append(fileName).toString() :
                new StringBuffer().append(baseFilePath).append("/").append(fileName).toString();
    }

    public InputStream getInputStream() {
        return scriptData != null ?
                new ByteArrayInputStream(scriptData) :
                null;
    }
}
