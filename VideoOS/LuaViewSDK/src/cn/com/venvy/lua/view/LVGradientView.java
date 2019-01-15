package cn.com.venvy.lua.view;

import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

import cn.com.venvy.lua.ud.UDGradientView;


/**
 * Created by lgf on 2018/1/19.
 */

public class LVGradientView extends RelativeLayout implements ILVViewGroup {
    private UDGradientView mLuaUserdata;

    public LVGradientView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        mLuaUserdata = new UDGradientView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
        mLuaUserdata.setChildNodeViews(childNodeViews);
    }
}
