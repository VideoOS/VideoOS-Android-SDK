/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.userdata.constants;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.userdata.base.BaseLuaTable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Created by tuoli on 11/7/16.
 * <p>
 * 标记可吸顶的列表行
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class UDPinned extends BaseLuaTable {
    public static int PINNED_YES = 1;
    public static int PINNED_NO = 0;

    public UDPinned(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    void init() {
        this.set("YES", PINNED_YES);
        this.set("NO", PINNED_NO);
    }
}
