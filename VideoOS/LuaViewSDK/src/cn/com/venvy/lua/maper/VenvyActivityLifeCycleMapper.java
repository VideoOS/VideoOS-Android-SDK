package cn.com.venvy.lua.maper;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDActivityLifeCycle;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class VenvyActivityLifeCycleMapper<U extends VenvyUDActivityLifeCycle> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyActivityLifeCycleMapper";
    private static final String[] sMethods = new String[]{
            "pageCallback",//0
            "onPageWillAppear",//1 create
            "onPageDidAppear",//2  resume
            "onPagePause",//3  //pause
            "onPageWillDisappear",//4  null实现，兼容ios
            "onPageDidDisappear",//5  // stop
            "onPageDestroy"//6  destroy
    };


    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return pageCallback(target, varargs);
            case 1:
                return onPageWillAppear(target, varargs);
            case 2:
                return onPageDidAppear(target, varargs);
            case 3:
                return onPagePause(target, varargs);
            case 4:
                return onPageWillDisappear(target, varargs);
            case 5:
                return onPageDidDisappear(target, varargs);
            case 6:
                return onPageDestroy(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue pageCallback(U target, Varargs varargs) {
        final LuaTable callback = varargs.opttable(2, null);
        return target.setPageCallback(callback);
    }

    public Varargs onPageWillAppear(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnActivityCreate(callback);
    }

    public Varargs onPageDidAppear(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnActivityResume(callback);
    }

    public Varargs onPagePause(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnActivityPause(callback);
    }

    public Varargs onPageDidDisappear(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnActivityStop(callback);
    }

    public Varargs onPageWillDisappear(U target, Varargs varargs) {
        return LuaValue.NIL;
    }

    public Varargs onPageDestroy(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnActivityDestroy(callback);
    }


}
