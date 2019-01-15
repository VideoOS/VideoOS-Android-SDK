/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.vm.extend;


import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * Base Lib extend for LuaJ
 * @author song
 * @date 16/2/22
 */
public class BaseLib {
    org.luaj.vm2.lib.BaseLib baseLib;
    Globals globals;

    public BaseLib(org.luaj.vm2.lib.BaseLib baseLib, Globals globals) {
        this.baseLib = baseLib;
        this.globals = globals;
    }

    public void extend(LuaValue env) {
        env.set("printLV", new printLV(baseLib));
    }

    // "print", // (...) -> void
     class printLV extends VarArgFunction {
         org.luaj.vm2.lib.BaseLib baselib;

        printLV(org.luaj.vm2.lib.BaseLib baselib) {
            this.baselib = baselib;
        }

        public Varargs invoke(Varargs args) {
            return NONE;
        }
    }
}
