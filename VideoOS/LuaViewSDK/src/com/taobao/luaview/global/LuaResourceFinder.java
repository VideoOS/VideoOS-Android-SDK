/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.taobao.luaview.scriptbundle.LuaScriptManager;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.scriptbundle.asynctask.delegate.ScriptBundleLoadDelegate;
import com.taobao.luaview.util.DrawableUtil;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.ParamUtil;
import com.taobao.luaview.util.TypefaceUtil;

import org.luaj.vm2.lib.ResourceFinder;

import java.io.InputStream;

import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;

/**
 * 给定相对路径，找对应的资源（脚本、图片、字体等)
 *
 * @author song
 * @date 16/1/5
 */
public class LuaResourceFinder implements ResourceFinder {
    public static String DEFAULT_MAIN_ENTRY = "main.lua";//默认的脚本入口，在加载folder或者bundle的时候会默认加载该名称的脚本

    Context mContext;

    //内存脚本
    ScriptBundle mScriptBundle;

    //加载的uri（可以是url，也可以是包名称）
    String mUri;

    //基础scriptPath
    String mBaseScriptFolderPath;//默认cache目录
    String mBaseBundlePath;//Bundle文件系统路径
    String mBaseAssetPath;//asset下路径

    public LuaResourceFinder(Context context) {
        if (context != null) {
            this.mContext = context.getApplicationContext();
        }
    }

    public void setScriptBundle(ScriptBundle scriptBundle) {
        mScriptBundle = scriptBundle;
    }

    public void setUri(String uri) {
        mUri = uri;
        mBaseScriptFolderPath = LuaScriptManager.getBaseScriptFolderPath();
        mBaseBundlePath = LuaScriptManager.buildScriptBundleFolderPath(uri);
        mBaseAssetPath = FileUtil.getAssetFolderPath(uri);//脚本默认放在asset目录下
    }

    public String getUri() {
        return mUri;
    }

    public String getBaseBundlePath() {
        return mBaseBundlePath;
    }

    /**
     * find Script in given name or path
     *
     * @param nameOrPath
     * @return
     */
    @Override
    public InputStream findResource(String nameOrPath) {
        if (mScriptBundle != null && mScriptBundle.containsKey(nameOrPath)) {//from cache, 这里直接使用文件名称来处理
            ScriptFile scriptFile = mScriptBundle.getScriptFile(nameOrPath);
            return scriptFile.getInputStream();
        }

        if (LuaScriptManager.isLuaEncryptScript(nameOrPath)) {//.lv TODO 这里需要去掉，不再有本地lv文件
            return ScriptBundleLoadDelegate.loadEncryptScript(mContext, findFile(nameOrPath));
        } else {//.lua 或者 输入folder名字（其实是lvbundle的名字，如ppt440), 则加载 main.lua
            String newName = LuaScriptManager.isLuaScript(nameOrPath) ? nameOrPath : DEFAULT_MAIN_ENTRY;//如果是脚本则加载，否则加载main.lua
            InputStream inputStream = ScriptBundleLoadDelegate.loadEncryptScript(mContext, findFile(LuaScriptManager.changeSuffix(newName, LuaScriptManager.POSTFIX_LV)));
            if (inputStream == null) {//如果.lv不存在，则尝试读取.lua
                inputStream = findFile(newName);
            }
            return inputStream;
        }
    }

    /**
     * 在 res 或者 asset 或者 文件系统 找drawable
     * TODO 异步
     *
     * @param nameOrPath
     * @return
     */
    public Drawable findDrawable(String nameOrPath) {
        Drawable drawable = null;
        if (!TextUtils.isEmpty(nameOrPath)) {
            String drawableName = FileUtil.hasPostfix(nameOrPath) ? nameOrPath : ParamUtil.getFileNameWithPostfix(nameOrPath, "png");//如果没有后缀，则处理成.png

            String filepath = buildPathInSdcardIfExists(drawableName);
            if (filepath != null) {//从filepath加载
                drawable = DrawableUtil.getByPath(filepath);
            }

            if (drawable == null) {//从asset加载
                filepath = buildPathInAssetsIfExists(drawableName);
                if (filepath != null) {
                    drawable = DrawableUtil.getAssetByPath(mContext, drawableName);
                }
            }

            if (drawable == null) {//直接使用name加载
                drawable = DrawableUtil.getByName(mContext, nameOrPath);
            }
        }
        return drawable;
    }

    /**
     * 在 asset 或者 文件系统 找字体文件
     * TODO 优化
     *
     * @param nameOrPath
     * @return
     */
    public Typeface findTypeface(String nameOrPath) {
        Typeface typeface = null;
        if (!TextUtils.isEmpty(nameOrPath)) {
            String typefaceNameOrPath = FileUtil.hasPostfix(nameOrPath) ? nameOrPath : ParamUtil.getFileNameWithPostfix(nameOrPath, "ttf");//如果没有后缀，则处理成.ttf
            String filepath = buildPathInSdcardIfExists(typefaceNameOrPath);

            if (filepath != null) {//从文件系统加载
                typeface = TypefaceUtil.create(filepath);
            }

            if (typeface == null) {//从asset下加载
                filepath = buildPathInAssetsIfExists(typefaceNameOrPath);
                if (filepath != null) {
                    typeface = TypefaceUtil.create(mContext, filepath);
                }
            }

            if (typeface == null) {//default name
                typeface = TypefaceUtil.create(mContext, nameOrPath);
            }
        }
        return typeface != null ? typeface : Typeface.DEFAULT;
    }


    /**
     * 在 文件系统 或者 asset下 找资源
     * TODO 异步
     *
     * @param nameOrPath
     * @return
     */
    public InputStream findFile(String nameOrPath) {
        InputStream inputStream = null;

        if (!TextUtils.isEmpty(nameOrPath)) {
            String filepath = buildPathInSdcardIfExists(nameOrPath);
            if (filepath != null) {//从文件系统加载
                inputStream = FileUtil.open(filepath);
            }

            if (inputStream == null) {//从asset下加载
                filepath = buildPathInAssetsIfExists(nameOrPath);
                if (filepath != null) {
                    inputStream = VenvyIOUtils.open(mContext, filepath);
                }
            }

            if (inputStream == null) {//直接使用name加载
                inputStream = VenvyIOUtils.open(mContext, nameOrPath);
            }
        }
        return inputStream;
    }

    /**
     * exists
     *
     * @param nameOrPath
     * @return
     */
    public boolean exists(String nameOrPath) {
        if (!TextUtils.isEmpty(nameOrPath)) {
            String fullPath = buildSecurePathInSdcard(nameOrPath);
            if (fullPath != null) {//文件
                return VenvyFileUtil.isExistFile(fullPath);
            } else {
                fullPath = buildSecurePathInAssets(nameOrPath);
                return VenvyAesUtil.exists(mContext, fullPath);
            }
        }
        return false;
    }

    /**
     * 找文件在SD卡的路径，如果存在在获取
     *
     * @param nameOrPath
     * @return
     */
    String buildPathInSdcardIfExists(String nameOrPath) {
        String filepath = buildPathInBundleFolder(nameOrPath);
        if (VenvyFileUtil.isExistFile(filepath)) {//check bundle folder
            return filepath;
        }

        if (filepath == null) {//check script folder
            filepath = buildPathInRootFolder(nameOrPath);
            if (VenvyFileUtil.isExistFile(filepath)) {
                return filepath;
            }
        }
        return null;
    }

    /**
     * 获取安全路径（不一定在bundle下面，但必须在script目录下）
     *
     * @param nameOrPath
     * @return
     */
    public String buildSecurePathInSdcard(String nameOrPath) {//主要用在文件操作
        String path = buildPathInBundleFolder(nameOrPath);
        if (path != null) {//处理../../../ TODO 这里如何应对全局路径
            String canonicalPath = FileUtil.getCanonicalPath(path);
            if (canonicalPath != null && canonicalPath.startsWith(mBaseScriptFolderPath)) {
                return path;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * build file path
     *
     * @param nameOrPath
     * @return
     */
    public String buildPathInBundleFolder(String nameOrPath) {
        String result = null;

        if (nameOrPath != null && nameOrPath.startsWith("/")) {//如果反斜杠开头，则直接返回，TODO 待优化
            return nameOrPath;
        }

        if (!TextUtils.isEmpty(mBaseBundlePath)) {
            if (!FileUtil.isContainsFolderPath(nameOrPath, mBaseBundlePath)) {//不带基础路径的情况
                result = FileUtil.buildPath(mBaseBundlePath, nameOrPath);
            } else {
                result = nameOrPath;
            }
        }
        return result;
    }

    /**
     * build file path
     *
     * @param nameOrPath
     * @return
     */
    public String buildPathInRootFolder(String nameOrPath) {
        String result = null;

        if (nameOrPath != null && nameOrPath.startsWith("/")) {//如果反斜杠开头，则直接返回，TODO 待优化
            return nameOrPath;
        }

        if (!TextUtils.isEmpty(mBaseScriptFolderPath)) {
            if (!FileUtil.isContainsFolderPath(nameOrPath, mBaseScriptFolderPath)) {//不带基础路径的情况
                result = FileUtil.buildPath(mBaseScriptFolderPath, nameOrPath);
            } else {
                result = nameOrPath;
            }
        }
        return result;
    }


    /**
     * 获取安全路径
     *
     * @param nameOrPath
     * @return
     */
    public String buildSecurePathInAssets(String nameOrPath) {//主要用在文件操作
        String path = buildPathInAssets(nameOrPath);
        if (path != null) {//处理../../../
            String canonicalPath = FileUtil.getCanonicalPath(path);
            return canonicalPath != null && canonicalPath.startsWith(mBaseAssetPath) ? path : null;
        }
        return null;
    }

    /**
     * build path in assets
     *
     * @param nameOrPath
     * @return
     */
    String buildPathInAssetsIfExists(String nameOrPath) {
        String filepath = buildPathInAssets(nameOrPath);
        if (VenvyAesUtil.exists(mContext, filepath)) {
            return filepath;
        } else if (VenvyAesUtil.exists(mContext, nameOrPath)) {
            return nameOrPath;
        }
        return null;
    }

    /**
     * 找文件在asset下的路径
     *
     * @param nameOrPath
     * @return
     */
    String buildPathInAssets(String nameOrPath) {
        String result;

        if (nameOrPath != null && nameOrPath.startsWith("/")) {//如果反斜杠开头，则直接返回，TODO 待优化
            return nameOrPath;
        }

        if (!TextUtils.isEmpty(mBaseAssetPath) && !FileUtil.isContainsFolderPath(nameOrPath, mBaseAssetPath)) {//不带基础路径，或者不在asset下
            result = FileUtil.buildPath(mBaseAssetPath, nameOrPath);
        } else {
            result = nameOrPath;
        }
        return result;
    }
}
