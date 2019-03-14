package cn.com.venvy.common.debug;

import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.com.venvy.common.utils.VenvyUIUtil;


/**
 * debug控制面板
 * Created by Arthur on 2017/5/31.
 */

public class DebugHelper {

    public static void addDebugLayout(ViewGroup debugContentView) {
        final View invokeDialogView = createInvokeDialogView(debugContentView.getContext());
        View debugSwitchView = createDebugSwitchView(debugContentView.getContext());
        debugSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeDialogView.setClickable(!invokeDialogView.isClickable());
                invokeDialogView.setVisibility(invokeDialogView.isClickable() ? View.VISIBLE : View.GONE);
            }
        });

        debugContentView.addView(invokeDialogView);
        debugContentView.addView(debugSwitchView);
    }

    private static View createInvokeDialogView(final Context context) {
        View invokeDialogView = new View(context);
        invokeDialogView.setOnClickListener(new View.OnClickListener() {
            private long[] mHits = new long[5];
            private DebugDialog mDebugDialog;

            @Override
            public void onClick(View v) {
                //debug状态是否发生变化
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
                    initDialog(v.getContext());
                    mDebugDialog.showDialog(DebugStatus.getCurrentEnvironmentStatus());
                }
            }

            private void initDialog(Context context) {
                if (mDebugDialog == null) {
                    mDebugDialog = new DebugDialog(context);
                    mDebugDialog.setDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mDebugDialog = null;
                        }
                    });
                }
            }

        });
        invokeDialogView.setClickable(false);
        invokeDialogView.setVisibility(View.GONE);
        int size = VenvyUIUtil.dip2px(context, 30);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(size, size);
        invokeDialogView.setLayoutParams(layoutParams);

        return invokeDialogView;
    }

    private static View createDebugSwitchView(final Context context) {
        View debugSwitchView = new View(context);
        debugSwitchView.setClickable(true);
        int size = VenvyUIUtil.dip2px(context, 25);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(size, size);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        debugSwitchView.setLayoutParams(layoutParams);
        return debugSwitchView;
    }


}
