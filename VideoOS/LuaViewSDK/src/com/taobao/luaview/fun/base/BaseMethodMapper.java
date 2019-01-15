

package com.taobao.luaview.fun.base;

import com.taobao.luaview.cache.AppCache;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class BaseMethodMapper<U extends LuaValue> extends VarArgFunction {
    static String CACHE_METHODS = AppCache.CACHE_METHODS;

    /**
     * 该函数使用反射，调用方法，并且被调用方法签名必须为：fun(UIView, Varargs)格式，否则不被支持
     * 所有的method都是被注册的public方法，使用class.getMethods返回
     * TODO 如果这里性能受限的话，考虑使用其他方式实现(反射性能大概低了20倍)，但是只会在创建的时候消耗，其他情况下不消耗性能
     */
    public Varargs invoke(Varargs args) {
        try {
            if (opcode != -1) {
                return invoke(opcode, getUD(args), args);
            } else {
                return (Varargs) method.invoke(this, getUD(args), args);
            }
        } catch (Exception e) {
            return NONE;
        }
    }

    /**
     * 获取userdata
     *
     * @param varargs
     * @return
     */
    public U getUD(Varargs varargs) {
        return (U) varargs.arg1();
    }
    //----------------------------------------------------------------------------------------------

    /**
     * merge function names with cache tag
     *
     * @param tag
     * @param supernames
     * @param names
     * @return
     */
    public List<String> mergeFunctionNames(String tag, List<String> supernames, String[] names) {
        List<String> result = AppCache.getCache(CACHE_METHODS).get(tag);
        if (result == null) {
            result = mergeFunctionNames(supernames, names);
            AppCache.getCache(CACHE_METHODS).put(tag, result);
        }
        return result;
    }

    public List<String> mergeFunctionNames(String tag, List<String> supernames, List<String> names) {
        List<String> result = AppCache.getCache(CACHE_METHODS).get(tag);
        if (result == null) {
            result = mergeFunctionNames(supernames, names);
            AppCache.getCache(CACHE_METHODS).put(tag, result);
        }
        return result;
    }

    /**
     * merge function names
     * 将names拼接在supernames之后
     */
    List<String> mergeFunctionNames(List<String> supernames, String[] names) {
        return mergeFunctionNames(supernames, Arrays.asList(names));
    }

    /**
     * merge FunctionNames
     * 将自己的names拼接在supernames之后
     */
    List<String> mergeFunctionNames(List<String> supernames, List<String> names) {
        List<String> result = new ArrayList<String>();
        if (supernames != null && supernames.size() > 0) {
            result.addAll(supernames);
        }
        if (supernames != null && names != null) {
            result.addAll(supernames.size(), names);
        }
        return result;
    }

    /**
     * 获取所有函数名称，供子类调用
     *
     * @return
     */
    public List<String> getAllFunctionNames() {
        return new ArrayList<>();
    }

    /**
     * 调用子类
     *
     * @param code
     * @param target
     * @param varargs
     * @return
     */
    public Varargs invoke(int code, U target, Varargs varargs) {
        return LuaValue.NIL;
    }

}