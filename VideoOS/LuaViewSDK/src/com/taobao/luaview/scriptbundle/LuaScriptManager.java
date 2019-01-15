/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.scriptbundle;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.util.FileUtil;

import java.io.File;

import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyStringUtil;

/**
 * Lua脚本管理类
 *
 * @author song
 * @date 15/11/9
 */
public class LuaScriptManager {
     static String PACKAGE_NAME;
     static String BASE_FILECACHE_PATH;
    //folders
     static  String PACKAGE_NAME_DEFAULT = "luaview";
    public static  String FOLDER_SCRIPT = "script";

    //默认缓存文件的后缀
    public static  String POSTFIX_SCRIPT_BUNDLE = ".lvraw";

    public static  String POSTFIX_LUA = ".lua";
    public static  String POSTFIX_LV = ".lv";//Lua加密脚本(source or bytecode)
    public static  String POSTFIX_LV_BYTECODE_ZIP = ".bzip";//lua的二进制zip包
    public static  String POSTFIX_LV_STANDARD_SYNTAX_ZIP = ".szip";//标准语法的zip包
    public static  String POSTFIX_SIGN = ".sign";

    /**
     * 初始化
     *
     * @param context
     */
    public static void init( Context context) {
        if (TextUtils.isEmpty(BASE_FILECACHE_PATH) && context != null) {
            if (!LuaViewConfig.isDebug()) {//真实环境优先使用data/data目录
                initInternalFilePath(context);
            } else {//测试环境优先使用sd卡路径
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    PACKAGE_NAME = context.getPackageName();
                    BASE_FILECACHE_PATH = context.getExternalCacheDir() + File.separator;
                } else {
                    initInternalFilePath(context);
                }
            }
        }
    }

    /**
     * 初始化内部存储目录路径
     *
     * @param context
     */
     static void initInternalFilePath(Context context) {
         File dir = context.getDir(PACKAGE_NAME_DEFAULT, Context.MODE_PRIVATE);
        if (dir != null) {//优先存在 data/data/packagename/luaview
            PACKAGE_NAME = PACKAGE_NAME_DEFAULT;
            BASE_FILECACHE_PATH = dir.getPath() + File.separator;
        } else {
            PACKAGE_NAME = PACKAGE_NAME_DEFAULT;
            BASE_FILECACHE_PATH = context.getCacheDir() + File.separator;
        }
    }

    //--------------------------------static methods for get file path------------------------------


    /**
     * get scriptFolderPath
     *
     * @return
     */
    public static String getBaseScriptFolderPath() {
        return BASE_FILECACHE_PATH + PACKAGE_NAME + File.separator + FOLDER_SCRIPT + File.separator;
    }

    /**
     * get path of given folder
     *
     * @param subFolderName
     * @return
     */
    public static String getFolderPath( String subFolderName) {
        return new StringBuffer()
                .append(getBaseScriptFolderPath())
                .append(subFolderName)
                .append(File.separator)
                .toString();
    }

    /**
     * get file path
     *
     * @param subFolderName
     * @param fileNameWithPostfix
     * @return
     */
    public static String getFilePath( String subFolderName,  String fileNameWithPostfix) {
        return new StringBuffer()
                .append(getFolderPath(subFolderName))
                .append(fileNameWithPostfix)
                .toString();
    }

    /**
     * 构建文件名称
     *
     * @param nameWithoutPostfix 不带后缀的文件名称
     * @param postfixWithDot     带点的文件后缀
     * @return
     */
    public static String buildFileName( String nameWithoutPostfix,  String postfixWithDot) {
        return new StringBuffer().append(nameWithoutPostfix).append(postfixWithDot).toString();
    }


    //------------------------------------------script function-------------------------------------

    /**
     * 根据Url构建ScriptBundle的文件路径名称
     *
     * @param uri
     * @return
     */
    public static String buildScriptBundleFolderPath( String uri) {
         String fileNameWithoutPostfix = VenvyStringUtil.md5Hex(uri);
         String folderName = fileNameWithoutPostfix;//使用文件名作为子目录的名称//new StringBuffer().append(FOLDER_SCRIPT).append(File.separator).append(fileNameWithoutPostfix).toString();
        return getFolderPath(folderName);
    }

    /**
     * 构建脚本文件文件名
     *
     * @param uri
     * @return
     */
    public static String buildScriptBundleFilePath( String uri) {
         String fileNameWithoutPostfix = VenvyStringUtil.md5Hex(uri);
         String folderName = fileNameWithoutPostfix;//使用文件名作为子目录的名称//new StringBuffer().append(FOLDER_SCRIPT).append(File.separator).append(fileNameWithoutPostfix).toString();
         String fileName = buildFileName(fileNameWithoutPostfix, POSTFIX_SCRIPT_BUNDLE);
        return getFilePath(folderName, fileName);
    }

    //------------------------------------------exists----------------------------------------------

    /**
     * Script Bundle 是否存在
     *
     * @param uri
     * @return
     */
    public static boolean existsScriptBundle( String uri) {
        if (!TextUtils.isEmpty(uri)) {
            return VenvyFileUtil.isExistFile(buildScriptBundleFilePath(uri));
        }
        return false;
    }

    //--------------------------------------------判断函数-------------------------------------------




    /**
     * 是否是lua 二进制zip包
     *
     * @param url
     * @return
     */
    public static boolean isLuaBytecodeUrl( String url) {
        return FileUtil.isSuffix(url, LuaScriptManager.POSTFIX_LV_BYTECODE_ZIP);
    }


    /**
     * 是否是标准语法的 zip包
     *
     * @param url
     * @return
     */
    public static boolean isLuaStandardSyntaxUrl( String url) {
        return FileUtil.isSuffix(url, LuaScriptManager.POSTFIX_LV_STANDARD_SYNTAX_ZIP);
    }

    /**
     * 是否是lua加密脚本
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaEncryptScript( String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LV);
    }

    /**
     * 是否是普通lua文件
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaScript( String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_LUA);
    }

    /**
     * 签名文件
     *
     * @param fileName
     * @return
     */
    public static boolean isLuaSignFile( String fileName) {
        return FileUtil.isSuffix(fileName, LuaScriptManager.POSTFIX_SIGN);
    }

    /**
     * 改变filename的名称
     *
     * @param fileName
     * @param newSuffix
     * @return
     */
    public static String changeSuffix( String fileName,  String newSuffix) {
        if (fileName != null && fileName.lastIndexOf('.') != -1) {
            return fileName.substring(0, fileName.lastIndexOf('.')) + newSuffix;
        }
        return fileName;
    }

}
