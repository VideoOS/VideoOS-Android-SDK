/*******************************************************************************
 * Copyright (c) 2009-2011 Luaj.org. All rights reserved.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib.jse;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.DebugLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;

public class JsePlatform {

    /**
     * Create a standard set of globals for JSE including all the libraries.
     *
     * @return Table of globals initialized with the standard JSE libraries
     * @see #debugGlobals()
     * @see JsePlatform
     * @see JmePlatform
     */
    public static Globals standardGlobals() {
        return standardGlobals(new Globals());
    }

    /**
     * Create a standard set of globals for JSE including all the libraries.
     *
     * @return Table of globals initialized with the standard JSE libraries
     * @see #debugGlobals()
     * @see JsePlatform
     * @see JmePlatform
     */
    public static Globals standardGlobals(Globals globals) {
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new JseOsLib());
//		globals.load(new JseIoLib());//安全考虑，删除for LuaView
//		globals.load(new LuajavaLib());//安全考虑，删除for LuaView
        LoadState.install(globals);
        LuaC.install(globals);
        return globals;
    }


//    public static Globals debugGlobals() {
//        Globals globals = standardGlobals();
//        globals.load(new DebugLib());
//        return globals;
//    }
//
//    public static Globals debugGlobals(Globals globals) {
//        standardGlobals(globals);
//        globals.load(new DebugLib());
//        return globals;
//    }
//
//
//    public static void luaMain(LuaValue mainChunk, String[] args) {
//        Globals g = standardGlobals();
//        int n = args.length;
//        LuaValue[] vargs = new LuaValue[args.length];
//        for (int i = 0; i < n; ++i)
//            vargs[i] = LuaValue.valueOf(args[i]);
//        LuaValue arg = LuaValue.listOf(vargs);
//        arg.set("n", n);
//        g.set("arg", arg);
//        mainChunk.initupvalue1(g);
//        mainChunk.invoke(LuaValue.varargsOf(vargs));
//    }
}
