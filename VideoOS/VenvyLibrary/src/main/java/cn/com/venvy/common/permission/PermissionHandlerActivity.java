package cn.com.venvy.common.permission;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by yanjiangbo on 2017/5/3.
 */

public class PermissionHandlerActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int OPEN_APPLICATION_SETTING_CODE = 2;

    private static String appName;

    private PermissionRequestInfo requestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNextPermission();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        requestNextPermission();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // 禁止返回键
    }

    /**
     * 处理下一个权限请求
     */
    private void requestNextPermission() {
        requestInfo = PermissionCheckHelper.instance().getNextRequest(getApplicationContext());
        if (requestInfo != null) {
            String[] permissionArray = requestInfo.getPermissions();
            if (permissionArray != null && permissionArray.length > 0) {
                try {
                    ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_REQUEST_CODE);
                } catch (Exception ex) {
                    showNeverAskRationaleDialog(this, parseRationalMessage(requestInfo.getTipMessages()));
                }
            }
        } else {
            PermissionHandlerActivity.this.finish();
        }
    }

    private String parseRationalMessage(String[] tipMsgs) {
        if (tipMsgs == null || tipMsgs.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String msg : tipMsgs) {
            builder.append(msg);
        }
        return builder.toString();
    }


    /**
     * 被拒绝的权限并且shouldShowRequestPermissionRationale返回false就是用户选中Never Ask Again的权限
     * 弹框提示用户去设置里授予权限，不请求权限
     *
     * @return true表示处理了Never Ask Again
     */
    private boolean handleNeverAsk(final String[] permissionArray, String[] messageArray, int[] grantResults) {
        boolean hasNeverAsk = false;
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < permissionArray.length && grantResults[index] == PackageManager.PERMISSION_DENIED; ++index) {
            String permission = permissionArray[index];
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                hasNeverAsk = true;
                if (!messageArray[index].isEmpty()) {
                    builder.append(messageArray[index]);
                }
            }
        }
        if (hasNeverAsk) {
            showNeverAskRationaleDialog(this, builder.toString());
        }
        return hasNeverAsk;
    }

    /**
     * 权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (PERMISSION_REQUEST_CODE == requestCode) {
            if (requestInfo == null) {
                requestNextPermission();
                return;
            }
            for (int index = 0; index < permissions.length; ++index) {
                requestInfo.updateRequestResult(permissions[index], grantResults[index]);
            }
            if (!handleNeverAsk(permissions, requestInfo.getTipMessages(), grantResults)) {
                requestInfo.doCallback();
                requestNextPermission();
            }
        }
    }

    /**
     * 打开设置中app应用信息界面
     */
    private void openAppSetting() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, OPEN_APPLICATION_SETTING_CODE);
        } catch (Exception ex) {
            Log.d("PermissionHandler", "open app setting failed");
            if (requestInfo != null) {
                requestInfo.doCallback();
            }
            requestNextPermission();
        }
    }

    /**
     * 处理Never Ask Again情况，用户返回后再次去请求权限来获取最终的权限请求结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_APPLICATION_SETTING_CODE) {
            requestInfo = PermissionCheckHelper.instance().findShouldCheckPermission(getApplicationContext(), requestInfo);
            if (requestInfo != null) {
                requestInfo.doCallback();
            }
            requestNextPermission();
        }
    }

    /**
     * 处理Never Ask Again
     * 自定义权限请求解释提示框，带有设置引导
     */
    private void showNeverAskRationaleDialog(Context context, String message) {
        if (this.isFinishing()) {
            return;
        }
        if (!TextUtils.isEmpty(message)) {
            message = message.substring(0, message.length() - 1);
        }
        StringBuilder builder = new StringBuilder(message)
                .append("。\n操作路径：")
                .append("设置->应用->")
                .append(getAppLabel())
                .append("->权限");
        new AlertDialog.Builder(context)
                .setTitle("我们需要一些权限")
                .setMessage(builder)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestInfo != null) {
                            requestInfo.doCallback();
                        }
                        requestNextPermission();
                    }
                }).setCancelable(false).create().show();
    }

    private String getAppLabel() {
        if (appName != null) {
            return appName;
        }
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            appName = pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "应用名称";
        }
        return appName;
    }
}
