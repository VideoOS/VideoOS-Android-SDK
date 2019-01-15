/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.global;

import android.content.Context;


/**
 * LuaView 全局设置
 *
 * @author song
 * @date 15/9/9
 */
public class LuaViewConfig {
    static boolean isDebug = true;
    static boolean isLibsLazyLoad = false;//是否延迟加载Libs，如果延迟加载则只会加载用到的libs，并且只会在用到的时候才加载，不用到不加载
    static boolean isUseLuaDC = false;//是否使用LuaDC Compiler，直接将lua代码编译成dex文件，能够加速虚拟机执行
    static boolean isUseNoReflection = false;//是否不使用反射调用接口
    static boolean isCachePrototype = false;//是否缓存prototype，默认不缓存
    static boolean isAutoSetupClickEffects = false;//是否自动设置点击效果


    /**
     * init luaview
     *
     * @param context
     */
    public static void init(Context context) {
        //延迟加载Libs
        LuaViewConfig.setLibsLazyLoad(true);
        //是否使用非反射方式API调用（默认为true)
        LuaViewConfig.setUseNoReflection(true);
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static boolean isLibsLazyLoad() {
        return isLibsLazyLoad;
    }

    public static boolean isUseLuaDC() {
        return isUseLuaDC;
    }

    /**
     * 全局是否debug
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        isDebug = debug;
    }


    /**
     * 是否延迟加载libs，如果设置为true的话则会在运行的时候才会加载用户lib，而不是初始化虚拟机的时候加载
     *
     * @param lazyLoad
     */
    public static void setLibsLazyLoad(boolean lazyLoad) {
        isLibsLazyLoad = lazyLoad;
    }


    /**
     * 设置不使用反射
     *
     * @param useNoReflection
     */
    public static void setUseNoReflection(boolean useNoReflection) {
        isUseNoReflection = useNoReflection;
    }

    public static boolean isUseNoReflection() {
        return isUseNoReflection;
    }

    public static void setCachePrototype(boolean cachePrototype) {
        isCachePrototype = cachePrototype;
    }


    public static boolean isCachePrototype() {
        return isCachePrototype;
    }


    public static boolean isAutoSetupClickEffects() {
        return isAutoSetupClickEffects;
    }
}
