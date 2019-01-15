/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.provider;

import android.content.Context;
import android.view.ViewGroup;

import com.taobao.luaview.view.imageview.BaseImageView;
import com.taobao.luaview.view.imageview.DrawableLoadCallback;

import java.lang.ref.WeakReference;

/**
 * 提供图片下载功能，用作ImageView相关
 *
 * @author song
 * @date 16/4/11
 * 主要功能描述
 * 修改描述
 * 下午4:48 song XXX
 */
public interface ImageProvider {
    /**
     * 下载图片
     *
     * @param imageView
     * @param url
     * @param callback
     */
    void load( Context context, WeakReference<BaseImageView> imageView, String url, DrawableLoadCallback callback);


    /**
     * 预下载图片
     *
     * @param context
     * @param url
     * @param callback
     */
    void preload(Context context, String url, DrawableLoadCallback callback);

    /**
     * pause all requests
     *
     * @param context
     */
    void pauseRequests(ViewGroup view, Context context);

    /**
     * resume all requests
     *
     * @param context
     */
    void resumeRequests(ViewGroup view, Context context);


}
