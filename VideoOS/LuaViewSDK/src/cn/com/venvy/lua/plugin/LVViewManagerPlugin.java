package cn.com.venvy.lua.plugin;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.App;
import cn.com.venvy.lua.binder.VenvyLVLibBinder;

/**
 * Created by Arthur on 2017/9/6.
 */

public class LVViewManagerPlugin {

    private static StringDrawLength sStringDrawLength;
    private static StringSizeWithWidth sStringSizeWithWidth;

    public static void install(VenvyLVLibBinder venvyLVLibBinder, View targetView) {
        venvyLVLibBinder.set("stringDrawLength", sStringDrawLength == null ? sStringDrawLength = new StringDrawLength() : sStringDrawLength);
        venvyLVLibBinder.set("destroyView", new DestroyManager(targetView));
        venvyLVLibBinder.set("stringSizeWithWidth", sStringSizeWithWidth == null ? sStringSizeWithWidth = new StringSizeWithWidth() : sStringSizeWithWidth);
    }

    private static class StringDrawLength extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                String text = LuaUtil.getString(args, fixIndex + 1);
                Float size = LuaUtil.getFloat(args, fixIndex + 2);
                if (!TextUtils.isEmpty(text)) {
                    Paint paint = new Paint();
                    if (size != null) {
                        paint.setTextSize(size);
                    }
                    float text_width = paint.measureText(String.valueOf(text));//得到总体长度
                    return valueOf(text_width);
                }
            }
            return valueOf(0);
        }
    }

    private static class StringSizeWithWidth extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (args.narg() > fixIndex) {
                String text = LuaUtil.getString(args, fixIndex + 1);
                final Float width = LuaUtil.getFloat(args, fixIndex + 2);
                Float size = LuaUtil.getFloat(args, fixIndex + 3);
                float mathWidth = 0f;
                float mathHeight = 0f;
                if (!TextUtils.isEmpty(text)) {
                    Paint paint = new Paint();
                    if (size != null) {
                        paint.setTextSize(size);
                    }
                    float widthMeasureSpec = paint.measureText(text);
                    Rect bounds = new Rect();
                    paint.getTextBounds(text, 0, text.length(), bounds);
                    float hightMeasureSpec = bounds.height();
                    if (width >= widthMeasureSpec) {
                        mathWidth = widthMeasureSpec;
                    } else {
                        mathWidth = width;
                    }
                    int charNumInThisLine = paint.breakText(text, 0, text.length(), true, mathWidth, null);
                    if(charNumInThisLine>=text.length()){
                        mathHeight = (float) (hightMeasureSpec * (Math.ceil(text.length() / charNumInThisLine)));
                    }else{
                        mathHeight = (float) (hightMeasureSpec * (Math.ceil(text.length() / charNumInThisLine)+1));
                    }
                }
                LuaValue[] luaValue = new LuaValue[]{LuaValue.valueOf(mathWidth), LuaValue.valueOf(mathHeight)};
                return LuaValue.varargsOf(luaValue);
            }
            return valueOf(0);
        }
    }

    private static class DestroyManager extends VarArgFunction {

        private View rootView;

        DestroyManager(View rootView) {
            this.rootView = rootView;
        }

        @Override
        public Varargs invoke(Varargs args) {
            if (rootView == null) {//VideoOsLuaView
                return LuaValue.FALSE;
            }
            ViewGroup parent = (ViewGroup) rootView.getParent();
            int argsNum = args.narg();
            final int fixIndex = VenvyLVLibBinder.fixIndex(args);
            if (argsNum > 1) {
                String id = LuaUtil.getString(args, fixIndex + 1);
                if (!TextUtils.isEmpty(id)) {
                    View view = parent != null ? parent.findViewWithTag(id) : null;
                    if (view != null) {
                        parent.removeView(view);
                    }
                    return LuaValue.TRUE;
                }
            }
            if (parent != null) {
                parent.removeView(rootView);
                return LuaValue.TRUE;
            }
            return LuaValue.FALSE;
        }
    }
}
