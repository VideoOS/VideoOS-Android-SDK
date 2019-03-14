package cn.com.venvy.lua.view;

import android.content.Context;
import android.support.annotation.Nullable;


import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import cn.com.venvy.common.image.scanner.ImageScannerDialog;
import cn.com.venvy.common.interf.IWidgetClickListener;

/**
 * Created by mac on 17/12/22.
 */

public class VenvyLVImageScannerDialog extends ImageScannerDialog {

    public VenvyLVImageScannerDialog(Context context) {
        super(context);
    }

    public void setCropImageResultListener(final LuaFunction luaFunction) {
        if (mImageScannerDialogLayout != null) {
            mImageScannerDialogLayout.mCropImageResultListener = new IWidgetClickListener<String>() {
                @Override
                public void onClick(@Nullable String imgPath) {
                    if (luaFunction != null) {
                        LuaValue value = LuaValue.valueOf(imgPath);
                        LuaUtil.callFunction(luaFunction, value);
                    }
                    dismiss();
                }
            };
        }
    }

}
