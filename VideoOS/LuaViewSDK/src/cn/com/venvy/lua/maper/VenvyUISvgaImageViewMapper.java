package cn.com.venvy.lua.maper;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDSvgaImageView;

/**
 * Created by mac on 18/3/27.
 */

@LuaViewLib(revisions = {"20170306已对标"})
public class VenvyUISvgaImageViewMapper<U extends VenvyUDSvgaImageView> extends UIViewMethodMapper<U> {

    private static final String TAG = "VenvyUISvgaImageViewMapper";
    private static final String[] sMethods = new String[]{
            "svga",//0
            "loops",//1
            "fps",//2get方法
            "frames",//3get方法
            "readyToPlay",//4
            "startAnimation",//5
            "stopAnimation",//6
            "pauseAnimation",//7
            "svgaCallback",//8
            "isAnimating",//9
            "stepToFrame",//10
            "stepToPercentage"//11
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
                return svga(target, varargs);
            case 1:
                return loops(target, varargs);
            case 2:
                return fps(target, varargs);
            case 3:
                return frames(target, varargs);
            case 4:
                return readyToPlay(target, varargs);
            case 5:
                return startAnimation(target, varargs);
            case 6:
                return stopAnimation(target, varargs);
            case 7:
                return pauseAnimation(target, varargs);
            case 8:
                return svgaCallback(target, varargs);
            case 9:
                return isAnimating(target, varargs);
            case 10:
                return stepToFrame(target, varargs);
            case 11:
                return stepToPercentage(target, varargs);

        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue isAnimating(U target, Varargs varargs) {
        return target.isAnimating(varargs);
    }

    public LuaValue readyToPlay(U target, Varargs varargs) {

        return target.readyToPlay(varargs);
    }

    public LuaValue startAnimation(U target, Varargs varargs) {

        return target.startAnimation(varargs);
    }

    public LuaValue stepToPercentage(U target, Varargs varargs) {
        return target.stepToPercentage(varargs);
    }

    public LuaValue stepToFrame(U target, Varargs varargs) {
        return target.stepToFrame(varargs);
    }

    public LuaValue stopAnimation(U target, Varargs varargs) {

        return target.stopAnimation(varargs);
    }

    public LuaValue pauseAnimation(U target, Varargs varargs) {

        return target.pauseAnimation(varargs);
    }

    public LuaValue frames(U target, Varargs varargs) {
        return target.frames(varargs);
    }


    public LuaValue fps(U target, Varargs varargs) {
        return target.fps(varargs);
    }

    public LuaValue loops(U target, Varargs varargs) {
        return target.loops(varargs);
    }

    public LuaValue svga(U target, Varargs varargs) {
        return target.svga(varargs);
    }

    public LuaValue svgaCallback(U target, Varargs varargs) {
        return target.svgaCallback(varargs);
    }
}
