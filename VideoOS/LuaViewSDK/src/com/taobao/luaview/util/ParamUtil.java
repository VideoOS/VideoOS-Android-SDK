/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import org.luaj.vm2.Varargs;

/**
 * Param handler
 *
 * @author song
 * @date 15/10/22
 */
public class ParamUtil {

    /**
     * get float value from Varargs
     *
     * @param varargs
     * @param firstIndex 开始的位置
     * @return
     */
    public static float[] getFloatValues(Varargs varargs, int firstIndex) {
        float[] values = null;
        if (varargs.narg() > (firstIndex - 1)) {
            values = new float[varargs.narg() - (firstIndex - 1)];
            for (int i = firstIndex; i <= varargs.narg(); i++) {
                values[i - firstIndex] = (float) varargs.optdouble(i, 0f);
            }
        }
        return values;
    }


    /**
     * remove postfix of given name if postfix found
     *
     * @param name
     * @return
     */
     static String getFileNameWithoutPostfix( String name) {
        if (name != null && name.indexOf('.') != -1) {//has postfix TODO 文件名带.需要特殊处理
            return name.substring(0, name.lastIndexOf('.'));
        }
        return name;
    }

    /**
     * add postfix to given name if no postfix found
     *
     * @param name
     * @param postfix
     * @return
     */
    public static String getFileNameWithPostfix( String name,  String postfix) {
        if (name != null && name.indexOf('.') == -1) {//no postfix TODO 文件名带.需要特殊处理
            return name + "." + postfix;
        }
        return name;
    }
}
