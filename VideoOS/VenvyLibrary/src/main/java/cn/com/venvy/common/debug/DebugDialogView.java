package cn.com.venvy.common.debug;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * debug控制面板的view
 * Created by Arthur on 2017/6/2.
 */

class DebugDialogView extends FrameLayout {
    private Context mContext;

    //开启debug模式
    private CheckBox mDebugCheckBox;
    //是否关闭或者错误上报功能
    private CheckBox mReportCheckBox;
    //是否输出打印的Log信息
    private CheckBox mLogCheckBox;
    //是否打开预发环境
    private CheckBox mPreCheckoutBox;

    private LinearLayout mListView;
    private String openDebugTxt = "是否打开debug模式?";
    private String closeDebugTxt = "是否关闭debug模式?";
    private String openPreTxt = "是否打开预发环境?";
    private String closePreTxt = "是否关闭预发环境?";

    public DebugDialogView(@NonNull Context context) {
        super(context);
        mContext = context;
        setParams();
        initListView();
        addView(mListView);
    }

    private void setParams() {
        int width = VenvyUIUtil.dip2px(mContext, 100);
        int height = VenvyUIUtil.dip2px(mContext, 400);
        FrameLayout.LayoutParams rootParams = new FrameLayout.LayoutParams(width, height);
        setLayoutParams(rootParams);
    }

    private void initListView() {
        mListView = new LinearLayout(mContext);
        mListView.setOrientation(LinearLayout.VERTICAL);

        LayoutParams params =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int padding = VenvyUIUtil.dip2px(mContext, 15);
        mListView.setPadding(padding, 0, padding, 0);
        mListView.setLayoutParams(params);

        //初始化mDebugCheckBox
        initDebugCheckBox();
        //出汗后mReportCheckBox
        initReportCheckBox();
        //初始化Log信息
        initLoggingCheckBox();
        initPreCheckBox();
        mListView.addView(mDebugCheckBox);
        mListView.addView(mPreCheckoutBox);
        mListView.addView(mReportCheckBox);
        mListView.addView(mLogCheckBox);
    }

    private void initPreCheckBox() {
        mPreCheckoutBox = new CheckBox(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mPreCheckoutBox.setText(openDebugTxt);
        mPreCheckoutBox.setLayoutParams(params);
        mPreCheckoutBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked && mDebugCheckBox.isChecked()) {
                    mDebugCheckBox.setChecked(false);
                }
            }
        });
    }

    private void initReportCheckBox() {
        mReportCheckBox = new CheckBox(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mReportCheckBox.setLayoutParams(params);
    }

    private void initLoggingCheckBox() {
        mLogCheckBox = new CheckBox(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mLogCheckBox.setText("是否开启log信息？");
        mLogCheckBox.setLayoutParams(params);
    }

    private void initDebugCheckBox() {
        mDebugCheckBox = new CheckBox(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mDebugCheckBox.setLayoutParams(params);
        mDebugCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked && mPreCheckoutBox.isChecked()) {
                    mPreCheckoutBox.setChecked(false);
                }
            }
        });
    }


    PositiveClickListener createPositiveClickListener() {
        return new PositiveClickListener();
    }

    void updateCheckBoxTxt(DebugStatus.EnvironmentStatus environmentStatus) {
        switch (environmentStatus) {
            case DEBUG:
                mDebugCheckBox.setText(closeDebugTxt);
                mLogCheckBox.setVisibility(View.GONE);
                mPreCheckoutBox.setText(openPreTxt);
                break;
            case PREVIEW:
                mDebugCheckBox.setText(openDebugTxt);
                mPreCheckoutBox.setText(closePreTxt);
                break;
            case RELEASE:
                mDebugCheckBox.setText(openDebugTxt);
                mPreCheckoutBox.setText(openPreTxt);
                break;
        }
    }

    private class PositiveClickListener implements DialogInterface.OnClickListener {


        @Override
        public void onClick(DialogInterface dialog, int which) {
            DebugStatus.EnvironmentStatus status = DebugStatus.EnvironmentStatus.RELEASE;
            if (mDebugCheckBox.isChecked()) {
                String currentDebugTxt = (String) mDebugCheckBox.getText();
                if (currentDebugTxt.contains("打开")) {
                    status = DebugStatus.EnvironmentStatus.DEBUG;
                }
            } else if (mPreCheckoutBox.isChecked()) {
                String currentPreTxt = (String) mPreCheckoutBox.getText();
                if (currentPreTxt.contains("打开")) {
                    status = DebugStatus.EnvironmentStatus.PREVIEW;
                }
            }

            DebugStatus.changeEnvironmentStatus(status);
            if (mReportCheckBox.isChecked()) {

            }

            //根据选中与否来决定是否打开LOG
            VenvyLog.needLog = mLogCheckBox.isChecked();

            VenvyUIUtil.dismissDialogSafe(dialog);
        }
    }

}
