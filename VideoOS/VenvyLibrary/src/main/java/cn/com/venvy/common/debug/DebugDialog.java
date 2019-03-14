package cn.com.venvy.common.debug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Debug弹出框
 * Created by Arthur on 2017/6/1.
 */

class DebugDialog {
    private AlertDialog mDialog;
    private DebugDialogView mDialogView;

    DebugDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //点击否什么都不做
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VenvyUIUtil.dismissDialogSafe(dialog);
            }
        });

        mDialogView = new DebugDialogView(context);
        //点击是切换debug状态
        builder.setPositiveButton("确定", mDialogView.createPositiveClickListener());
        builder.setView(mDialogView);
        mDialog = builder.create();
    }


    void showDialog(DebugStatus.EnvironmentStatus environmentStatus) {
        mDialogView.updateCheckBoxTxt(environmentStatus);
        mDialog.show();
    }

    void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        mDialog.setOnDismissListener(dismissListener);
    }

}
