package cn.com.venvy.lua.ud;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;


import com.taobao.luaview.userdata.ui.UDViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import cn.com.venvy.lua.view.LVGradientView;


/**
 * Created by lgf on 2018/1/19.UIRefreshScrollerMethodMapper
 */

public class UDGradientView extends UDViewGroup<LVGradientView> {
    public UDGradientView(LVGradientView view, Globals globals, LuaValue metaTable, LuaValue initParams) {
        super(view, globals, metaTable, initParams);
    }

    public void setGradient(int[] colors) {
        LVGradientView view = getView();
        Drawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }

    public void setCorner(float[] radii) {
        LVGradientView view = getView();
        Drawable bg = view.getBackground();
        if (bg instanceof GradientDrawable) {
            ((GradientDrawable) bg).setCornerRadii(radii);
        } else {
            bg = new GradientDrawable();
            ((GradientDrawable) bg).setCornerRadii(radii);
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }

    public void setStroke(int width, int color) {
        LVGradientView view = getView();
        Drawable bg = view.getBackground();
        if (bg instanceof GradientDrawable) {
            ((GradientDrawable) bg).setStroke(width, color);
        } else {
            bg = new GradientDrawable();
            ((GradientDrawable) bg).setStroke(width, color);
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }
}
