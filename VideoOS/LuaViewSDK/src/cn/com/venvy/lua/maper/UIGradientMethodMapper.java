package cn.com.venvy.lua.maper;

import android.graphics.Color;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;

import android.graphics.drawable.GradientDrawable;

import com.taobao.luaview.userdata.constants.UDOrientation;
import com.taobao.luaview.util.ColorUtil;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.UDGradientView;


/**
 * Created by lgf on 2018/1/19.
 */
@LuaViewLib(revisions = {"20180410"})
public class UIGradientMethodMapper<U extends UDGradientView> extends UIViewGroupMethodMapper<U> {
    private static final String TAG = "UIGradientMethodMapper";
    private static final int MAX_COLOR_VALUE = 16777216;//16^6，6位的
    private static final String[] sMethods = new String[]{
            "gradient",
            "corner",
            "stroke"
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
                return gradient(target, varargs);
            case 1:
                return corner(target, varargs);
            case 2:
                return stroke(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue stroke(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        Integer integer = LuaUtil.getInt(varargs, 2);
        Integer color = ColorUtil.parse(LuaUtil.getInt(varargs, 3));
        if (integer != null && color != null) {
            view.setStroke(DimenUtil.dpiToPx(integer), color);
        }
        return this;
    }

    public LuaValue gradient(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        final int narg = varargs.narg();
        Integer startColor;
        Integer endColor;
        String orientationName = null;
        if (narg >= 5) {
            Float alphaStart = LuaUtil.getFloat(varargs, 3);
            Float alphaEnd = LuaUtil.getFloat(varargs, 5);
            startColor = colorParse(LuaUtil.getInt(varargs, 2), alphaStart != null ? alphaStart : 0.0f);
            endColor = colorParse(LuaUtil.getInt(varargs, 4), alphaEnd != null ? alphaEnd : 0.0f);
            orientationName = LuaUtil.getString(varargs, 6);
        } else {
            startColor = ColorUtil.parse(LuaUtil.getInt(varargs, 2), 1);
            endColor = ColorUtil.parse(LuaUtil.getInt(varargs, 3), 1);
        }
        GradientDrawable.Orientation orientation = UDOrientation.parse(orientationName);
        int[] colors = {startColor != null ? startColor : 0, endColor != null ? endColor : 0};
        view.setGradient(colors, orientation);
        return this;
    }

    public LuaValue corner(U view, Varargs varargs) {
        if (varargs != null) {
            Float leftTopX = LuaUtil.getFloat(varargs, 2);
            Float leftTopY = LuaUtil.getFloat(varargs, 3);
            Float rightTopX = LuaUtil.getFloat(varargs, 4);
            Float rightTopY = LuaUtil.getFloat(varargs, 5);
            Float leftBottomX = LuaUtil.getFloat(varargs, 6);
            Float leftBottomY = LuaUtil.getFloat(varargs, 7);
            Float rightBottomX = LuaUtil.getFloat(varargs, 8);
            Float rightBottomY = LuaUtil.getFloat(varargs, 9);
            float[] radii = {
                    leftTopX != null ? DimenUtil.dpiToPxF(leftTopX) : 0.0f,
                    leftTopY != null ? DimenUtil.dpiToPxF(leftTopY) : 0.0f,
                    rightTopX != null ? DimenUtil.dpiToPxF(rightTopX) : 0.0f,
                    rightTopY != null ? DimenUtil.dpiToPxF(rightTopY) : 0.0f,
                    leftBottomX != null ? DimenUtil.dpiToPxF(leftBottomX) : 0.0f,
                    leftBottomY != null ? DimenUtil.dpiToPxF(leftBottomY) : 0.0f,
                    rightBottomX != null ? DimenUtil.dpiToPxF(rightBottomX) : 0.0f,
                    rightBottomY != null ? DimenUtil.dpiToPxF(rightBottomY) : 0.0f
            };
            view.setCorner(radii);
        }
        return this;
    }

    private Integer colorParse(Integer color, float alpha) {
        if (color != null) {
            if (color < MAX_COLOR_VALUE) {
                return Color.argb((int) (255 * alpha), Color.red(color), Color.green(color), Color.blue(color));//去除alpha信息，alpha信息由函数传入
            } else {//8位的
                return Color.argb(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            }
        }
        return color;
    }
}
