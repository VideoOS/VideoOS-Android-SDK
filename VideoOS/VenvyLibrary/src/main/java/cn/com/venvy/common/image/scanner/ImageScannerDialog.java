package cn.com.venvy.common.image.scanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cn.com.venvy.common.image.scanner.view.ImageScannerDialogLayout;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.permission.PermissionCheckHelper;
import cn.com.venvy.common.permission.PermissionRequestInfo;
import cn.com.venvy.common.utils.VenvyAPIUtil;


/**
 * Created by mac on 17/12/12.
 */

public class ImageScannerDialog {
    private AlertDialog mDialog;
    private Context mContext;
    public ImageScannerDialogLayout mImageScannerDialogLayout;
    private IWidgetClickListener<String> mCropImageResultListener;

    public ImageScannerDialog(Context context) {
        mContext = context;
    }

    public void show(ShowImageDialogResult result) {

        if (mDialog != null && mDialog.isShowing()) {
            return;
        }
        if (!checkPermission(result)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mDialog = builder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();
        mImageScannerDialogLayout = new ImageScannerDialogLayout(mContext);
        mImageScannerDialogLayout.setBackgroundColor(Color.WHITE);
        mImageScannerDialogLayout.mDismissDialogListener = new IWidgetClickListener() {
            @Override
            public void onClick(@Nullable Object o) {
                dismiss();
            }
        };
        mImageScannerDialogLayout.mCropImageResultListener = new IWidgetClickListener<String>() {
            @Override
            public void onClick(@Nullable String imgPath) {
                if (mCropImageResultListener != null) {
                    mCropImageResultListener.onClick(imgPath);
                }
                dismiss();
            }
        };
        //设置dialog全屏幕
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.CENTER);
        window.setContentView(mImageScannerDialogLayout);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        if (result != null) {
            result.successful();
        }
    }

    protected boolean checkPermission(final ShowImageDialogResult result) {
        if (VenvyAPIUtil.isSupport(23) &&
                !PermissionCheckHelper.isPermissionGranted(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mContext instanceof Activity) {
                PermissionRequestInfo requestInfo = new PermissionRequestInfo.Builder()
                        .setRequestCode(0)
                        .setRequestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})
                        .setTipMessages(new String[]{"请求获取您的图片信息"})
                        .setCallbackListener(new PermissionCheckHelper.PermissionCallbackListener() {
                            @Override
                            public void onPermissionCheckCallback(int requestCode, String[] permissions, int[] grantResults) {
                                if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                    show(result);
                                }
                            }
                        })
                        .build();
                PermissionCheckHelper.instance().requestPermissions(mContext, requestInfo);
            }
            return false;
        }
        return true;
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mImageScannerDialogLayout = null;
        }
    }

    public interface ShowImageDialogResult {
        void successful();
    }


}
