/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.graphics.Typeface;

import com.taobao.luaview.cache.SimpleCache;

/**
 * 字体处理，字体使用SimpleCache，全局缓存
 *
 * @author song
 * @date 15/11/6
 */
public class TypefaceUtil {
     static  String TAG = "TypefaceUtil";
     static  String TAG_TYPEFACE_NAME = "TypefaceUtil_NAME";

    /**
     * 未知
     *
     * @param typeface
     * @return
     */
    public static String getTypefaceName(Typeface typeface) {
         String name = SimpleCache.getCache(TAG_TYPEFACE_NAME).get(typeface);
        return name != null ? name : "unknown";
    }

    /**
     * create typeface
     *
     * @param context
     * @param name
     * @return
     */
    public static Typeface create( Context context,  String name) {
        Typeface result = SimpleCache.getCache(TAG).get(name);
        if (result == null) {
             String fontNameOrAssetPathOrFilePath = ParamUtil.getFileNameWithPostfix(name, "ttf");
            result = createFromAsset(context, fontNameOrAssetPathOrFilePath);
            if (result == null) {
                result = createFromFile(fontNameOrAssetPathOrFilePath);
            }
            if (result == null) {
                result = createByName(fontNameOrAssetPathOrFilePath);
            }
        }
        SimpleCache.getCache(TAG_TYPEFACE_NAME).put(result, name);//cache name
        return SimpleCache.getCache(TAG).put(name, result);
    }

    /**
     * create a typeface
     *
     * @param name
     * @return
     */
    public static Typeface create( String name) {
        Typeface result = SimpleCache.getCache(TAG).get(name);
        if (result == null) {
             String fontNameOrFilePath = ParamUtil.getFileNameWithPostfix(name, "ttf");
            result = createFromFile(fontNameOrFilePath);
            if (result == null) {
                result = createByName(fontNameOrFilePath);
            }
        }
        SimpleCache.getCache(TAG_TYPEFACE_NAME).put(result, name);//cache name
        return SimpleCache.getCache(TAG).put(name, result);
    }

    /**
     * create typeface by name or path
     *
     * @param fontName
     * @return
     */
     static Typeface createByName( String fontName) {
        try {
             Typeface typeface = Typeface.create(fontName, Typeface.BOLD_ITALIC);
            if (typeface != null && Typeface.BOLD_ITALIC == typeface.getStyle()) {//得到的是默认字体则返回null
                return null;
            }
            return typeface;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * create typeface from asset
     *
     * @param context
     * @param assetPath
     * @return
     */
     static Typeface createFromAsset( Context context,  String assetPath) {
        try {
            return Typeface.createFromAsset(context.getAssets(), assetPath);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * create typeface from file path
     *
     * @param filePath
     * @return
     */
     static Typeface createFromFile( String filePath) {
        try {
            return Typeface.createFromFile(filePath);
        } catch (Exception e) {
            return null;
        }
    }

}
