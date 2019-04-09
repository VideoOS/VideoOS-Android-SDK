/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.ImageView;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Gravity 文本布局
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20190408已对标", "IOS不支持"})
public class UDOrientation extends BaseLuaTable {

    public UDOrientation(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    void init() {
        set("TOP_BOTTOM", GradientDrawable.Orientation.TOP_BOTTOM.name());
        set("TR_BL", GradientDrawable.Orientation.TR_BL.name());
        set("RIGHT_LEFT", GradientDrawable.Orientation.RIGHT_LEFT.name());
        set("BR_TL", GradientDrawable.Orientation.BR_TL.name());
        set("BOTTOM_TOP", GradientDrawable.Orientation.BOTTOM_TOP.name());
        set("BL_TR", GradientDrawable.Orientation.BL_TR.name());
        set("LEFT_RIGHT", GradientDrawable.Orientation.LEFT_RIGHT.name());
        set("TL_BR", GradientDrawable.Orientation.TL_BR.name());
    }

    public static GradientDrawable.Orientation parse(String orientation) {
        return parse(orientation, GradientDrawable.Orientation.TOP_BOTTOM);
    }

    public static GradientDrawable.Orientation parse(String orientation, GradientDrawable.Orientation defaultValue) {
        try {
            return GradientDrawable.Orientation.valueOf(orientation);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
