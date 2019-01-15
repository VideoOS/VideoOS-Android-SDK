/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.graphics.Color;

import org.luaj.vm2.LuaValue;

/**
 * 颜色解析
 *
 * @author song
 * @date 15/10/27
 */
public class ColorUtil {
    static int MAX_COLOR_VALUE = 16777216;//16^6，6位的

    /**
     * parse a color value
     *
     * @param colorValue
     * @return
     */
    public static Integer parse(LuaValue colorValue) {
        return parse(colorValue, null);
    }

    public static Integer parse(LuaValue colorValue, Integer defaultValue) {
        if (LuaUtil.isNumber(colorValue)) {
            return parse(colorValue.toint(), defaultValue);
        }
        return defaultValue;//黑色
    }

    /**
     * parse a color value
     *
     * @param color
     * @return
     */
    public static Integer parse(Integer color) {
        return parse(color, null);
    }

    public static Integer parse(Integer color, Integer defaultColor) {
        if (color != null) {
            return Math.abs(color) < MAX_COLOR_VALUE ?
                    Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)) :
                    Color.argb(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
        }
        return defaultColor;
    }

    /***
     * parse a color value
     * @param color
     * @param alpha
     * @return
     */
    public static Integer parseWithAlpha(Integer color, Double alpha) {
        if (color != null) {
            return Math.abs(color) < MAX_COLOR_VALUE ?
                    Color.argb(alpha != null ? (int) (alpha * 255) : 255, Color.red(color), Color.green(color), Color.blue(color)) :
                    Color.argb(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
        }
        return null;
    }

    /**
     * convert a color to hex string
     *
     * @param color
     * @return
     */
    public static Integer getHexColor(Integer color) {
        if (color != null) {
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            return (red / 16) * 1048576 + (red % 16) * 65536 + (green / 16) * 4096 + (green % 16) * 256 + (blue / 16) * 16 + (blue % 16);
        }
        return 0;
    }


}
