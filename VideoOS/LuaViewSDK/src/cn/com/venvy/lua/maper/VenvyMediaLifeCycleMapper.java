package cn.com.venvy.lua.maper;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDMediaLifeCycle;

/**
 * Created by mac on 18/3/27.
 */
@LuaViewLib(revisions = {"20170306已对标"})
public class VenvyMediaLifeCycleMapper<U extends VenvyUDMediaLifeCycle> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyMediaLifeCycleMapper";
    private static final String[] sMethods = new String[]{
            "mediaCallback",//0
            "onMediaPause",//1
            "onMediaPlay",//2
            "onMediaEnd",//3
            "onMediaSeeking",//4
            "onMediaDefault",//5
            "onMediaProgress",//6
            "onPlayerSize",//7
            "startVideoTime",//8
            "stopVideoTime"//9
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
                return mediaCallback(target, varargs);

            case 1:
                return onMediaPause(target, varargs);
            case 2:
                return onMediaPlay(target, varargs);

            case 3:
                return onMediaEnd(target, varargs);

            case 4:
                return onMediaSeeking(target, varargs);
            case 5:
                return onMediaDefault(target, varargs);
            case 6:
                return onMediaProgress(target, varargs);
            case 7:
                return onPlayerSize(target, varargs);
            case 8:
                return onStartVideoTime(target, varargs);
            case 9:
                return onStopVideoTime(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue mediaCallback(U target, Varargs varargs) {
        final LuaTable callback = varargs.opttable(2, null);
        return target.setMediaCallback(callback);
    }

    public Varargs onMediaPause(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaPause(callback);
    }

    public Varargs onMediaPlay(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaPlay(callback);
    }


    public Varargs onMediaEnd(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaEnd(callback);
    }

    public Varargs onMediaSeeking(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaSeeking(callback);
    }

    public Varargs onMediaDefault(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaDefault(callback);
    }

    public Varargs onMediaProgress(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnMediaProgress(callback);
    }

    public Varargs onPlayerSize(U target, Varargs varargs) {
        final LuaValue callback = varargs.optvalue(2, NIL);
        return target.setOnPlayerSize(callback);
    }

    public Varargs onStartVideoTime(U target, Varargs varargs) {
        target.startVideoTime();
        return LuaValue.NIL;
    }


    public Varargs onStopVideoTime(U target, Varargs varargs) {
        target.stopVideoTime();
        return LuaValue.NIL;
    }


}
