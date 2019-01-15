package cn.com.venvy.lua.bridge;

import android.content.Context;

import org.luaj.vm2.LuaFunction;

import cn.com.venvy.common.image.scanner.ImageScannerDialog;
import cn.com.venvy.lua.view.VenvyLVImageScannerDialog;

/**
 * Created by mac on 17/12/22.
 */

public class LVImageScannerBridge {
    private VenvyLVImageScannerDialog mImageScannerDialog;
    private Context mContext;

    public LVImageScannerBridge(Context context) {
        mContext = context;
    }

    public void show(final LuaFunction luaFunction) {
        if (mImageScannerDialog == null) {
            mImageScannerDialog = new VenvyLVImageScannerDialog(mContext);
        }
        mImageScannerDialog.show(new ImageScannerDialog.ShowImageDialogResult() {
            @Override
            public void successful() {
                mImageScannerDialog.setCropImageResultListener(luaFunction);
            }
        });
    }
}
