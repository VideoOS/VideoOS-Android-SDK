package cn.com.venvy.lua.ud;


import com.taobao.luaview.userdata.ui.UDView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.lua.view.VenvyLVSvgeImageView;

/**
 * Created by mac on 18/3/27.
 */

public class VenvyUDSvgaImageView extends UDView<VenvyLVSvgeImageView> {


    public VenvyUDSvgaImageView(VenvyLVSvgeImageView view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public VenvyUDSvgaImageView svgaCallback(Varargs varargs) {
        LuaTable callbacks = varargs.opttable(2, null);
        getView().setLuaCallback(callbacks);
        return this;
    }

    public LuaValue isAnimating(Varargs varargs) {
        return getView().isAnimation(varargs);
    }

    public VenvyUDSvgaImageView svga(final Varargs varargs) {
        getView().svga(varargs);
        return this;
    }

    public VenvyUDSvgaImageView stepToPercentage(final Varargs varargs) {
        getView().stepToPercentage(varargs);
        return this;
    }

    public VenvyUDSvgaImageView stepToFrame(final Varargs varargs) {
        getView().stepToFrame(varargs);
        return this;
    }

    public VenvyUDSvgaImageView startAnimation(Varargs varargs) {
        getView().startAnimation(varargs);
        return this;
    }

    public VenvyUDSvgaImageView readyToPlay(Varargs varargs) {
        getView().readyToPlay(varargs);
        return this;
    }

    public VenvyUDSvgaImageView stopAnimation(Varargs varargs) {
        getView().stopAnimation(varargs);
        return this;
    }

    public VenvyUDSvgaImageView pauseAnimation(Varargs varargs) {
        getView().pauseAnimation(varargs);
        return this;
    }

    public LuaValue frames(Varargs varargs) {
        return getView().frames(varargs);
    }


    public LuaValue fps(Varargs varargs) {
        return getView().fps(varargs);
    }

    public VenvyUDSvgaImageView loops(Varargs varargs) {
        getView().loops(varargs);
        return this;
    }


}
