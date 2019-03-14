

package com.taobao.luaview.fun.base;

import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public abstract class BaseFunctionBinder extends TwoArgFunction {
    public String[] luaNames;

    public BaseFunctionBinder(String... name) {
        this.luaNames = name;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        return call(env, getMapperClass());
    }

    //ev是Gloabls的对象
    private LuaValue call(LuaValue env, Class<? extends LibFunction> libClass) {
         LuaTable metatable = (libClass == null || !LuaViewConfig.isLibsLazyLoad()) ? LuaViewManager.createMetatable(libClass) : null;//当不是lazyLoad或者lib为空（常量）的时候直接加载
        if (luaNames != null) {
            for (String name : luaNames) {
                env.set(name, createCreator(env, metatable));
            }
        }
        return metatable;
    }

    public abstract Class<? extends LibFunction> getMapperClass();

    /**
     * 默认返回metatable，如果要使用对象方式调用，则返回一个LuaFunction
     *
     * @param env
     * @param metaTable
     * @return
     */
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return metaTable;
    }
}
