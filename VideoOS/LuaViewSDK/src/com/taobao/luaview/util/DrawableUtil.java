/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.taobao.luaview.cache.WeakCache;

import java.io.ByteArrayOutputStream;

/**
 * drawable相关的util
 *
 * @author song
 * @date 15/9/9
 */
public class DrawableUtil {
    private static String TAG = "DrawableUtil";

    /**
     * get drawable by path
     *
     * @param filePath
     * @return
     */
    public static Drawable getByPath(String filePath) {
        Drawable drawable = WeakCache.getCache(TAG).get(filePath);
        if (drawable == null) {
            try {
                drawable = Drawable.createFromPath(filePath);
                WeakCache.getCache(TAG).put(filePath, drawable);
            } catch (Throwable e) {
            }
        }
        return drawable;
    }

    /**
     * 从Asset路径获取Drawable
     *
     * @param context
     * @param filePath
     * @return
     */
    public static Drawable getAssetByPath(Context context, String filePath) {
        Drawable drawable = WeakCache.getCache(TAG).get(filePath);
        if (drawable == null) {
            try {
                if (context != null) {
                    drawable = Drawable.createFromStream(context.getAssets().open(filePath), null);
                    WeakCache.getCache(TAG).put(filePath, drawable);
                }
            } catch (Throwable e) {
            }
        }
        return drawable;
    }

    /**
     * 根据名字获取drawable
     *
     * @param context
     * @param name
     * @return
     */
    public static Drawable getByName(Context context, String name) {
        Drawable drawable = WeakCache.getCache(TAG).get(name);
        if (drawable == null) {
            if (context != null && name != null) {
                Resources resources = context.getResources();
                try {
                    drawable = resources.getDrawable(resources.getIdentifier(ParamUtil.getFileNameWithoutPostfix(name),
                            "drawable", context.getPackageName()));
                    WeakCache.getCache(TAG).put(name, drawable);
                } catch (Throwable e) {
                }
            }
        }
        return drawable;
    }

    public static String drawable2StrByBase64(Drawable drawable) {
        if (drawable == null)
            return null;
        return Bitmap2StrByBase64(drawableToBitmap(drawable));
    }

    public static String Bitmap2StrByBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();// 取drawable的长宽
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
            Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);// 把drawable内容画到画布中
            return bitmap;
        }
    }
}
