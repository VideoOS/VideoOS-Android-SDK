package cn.com.venvy.common.permission;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/4/28.
 */

public class PermissionCheckHelper {

    private final static String TAG = "PermissionCheckHelper";
    private List<PermissionRequestInfo> requestList;
    private boolean isRequesting;

    public interface PermissionCallbackListener {
        void onPermissionCheckCallback(int requestCode, String[] permissions, int[] grantResults);
    }

    private PermissionCheckHelper() {
        requestList = new ArrayList<>();
    }

    public static PermissionCheckHelper instance() {
        return PermissionCheckHelperInner.instance;
    }

    private static class PermissionCheckHelperInner {
        static PermissionCheckHelper instance = new PermissionCheckHelper();
    }

    /**
     * 是否在请求权限
     */
    public boolean isRequesting() {
        return isRequesting;
    }

    /**
     * 只检查权限是否授予，不请求权限
     */
    public static boolean isPermissionGranted(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission)) {
            VenvyLog.d(TAG, "runtime permission check params error");
            return false;
        }
        int isGranted;
        try {
            isGranted = ContextCompat.checkSelfPermission(context, permission);
        } catch (Exception ex){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isGranted = PackageManager.PERMISSION_DENIED;
            } else {
                isGranted = PackageManager.PERMISSION_GRANTED;
            }
        }

        boolean result = isGranted == PackageManager.PERMISSION_GRANTED;
        if (result) {
            result = checkOpsPermission(context, permission);
        }
        return result;
    }

    private static boolean checkOpsPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                String opsName = AppOpsManager.permissionToOp(permission);
                if (opsName == null) {
                    return true;
                }
                int opsMode = appOpsManager.checkOpNoThrow(opsName, Process.myUid(), context.getPackageName());
                return opsMode == AppOpsManager.MODE_ALLOWED;
            } catch (Exception ex) {
                return true;
            }
        }
        return true;
    }

    /**
     * 请求权限
     */
    public void requestPermissions(Context context, int requestCode, String[] permissions, String[] messages,
                                   PermissionCallbackListener listener) {
        PermissionRequestInfo requestInfo = new PermissionRequestInfo.Builder()
                .setRequestCode(requestCode)
                .setRequestPermissions(permissions)
                .setTipMessages(messages)
                .setCallbackListener(listener)
                .build();
        requestPermissions(context, requestInfo);
    }

    /**
     * 请求权限
     *
     */
    public void requestPermissions(Context context, PermissionRequestInfo requestInfo) {
        if (context == null || requestInfo == null) {
            VenvyLog.d(TAG, "runtime permission request params error");
            return;
        }
        requestInfo = findShouldCheckPermission(context, requestInfo);
        if (requestInfo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                requestInfo.doCallback();
            } else {
                enqueueRequest(context, requestInfo);
            }
        }
    }

    /**
     * 权限请求入队
     */
    private synchronized void enqueueRequest(Context context, PermissionRequestInfo requestInfo) {
        requestList.add(requestInfo);
        if (!isRequesting) {
            try {
                Intent intent = new Intent(context, PermissionHandlerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
                isRequesting = true;
            } catch (Exception ex){
                VenvyLog.d(TAG, "permission handle activity start failed!");
                isRequesting = false;
            }
        }
    }

    /**
     * 检查权限是否授予，剔除已经授予的权限请求
     */
    public PermissionRequestInfo findShouldCheckPermission(Context context, PermissionRequestInfo requestInfo) {
        if (requestInfo == null) {
            return null;
        }
        String[] permissionArray = requestInfo.getPermissions();
        int count = permissionArray.length;
        if (count == 1) {
            if (isPermissionGranted(context, permissionArray[0])) {
                requestInfo.grantedCallback();
                return null;
            }
            return requestInfo;
        }
        List<String> permissionList = new ArrayList<>(count);
        List<String> messageList = new ArrayList<>(count);
        for (int index = 0; index < count; ++index) {
            String permission = permissionArray[index];
            if (isPermissionGranted(context, permission)) {
                requestInfo.updateRequestResult(permission, PackageManager.PERMISSION_GRANTED);
                continue;
            }
            permissionList.add(permission);
            messageList.add(requestInfo.getTipMessages()[index]);
        }
        if (permissionList.size() == 0) {
            requestInfo.grantedCallback();
            return null;
        }
        requestInfo.setPermissions(permissionList.toArray(new String[permissionList.size()]));
        requestInfo.setTipMessages(messageList.toArray(new String[messageList.size()]));
        return requestInfo;
    }

    /**
     * 循环获取下一个未授予的权限请求
     */
    public synchronized PermissionRequestInfo getNextRequest(Context context) {
        PermissionRequestInfo requestInfo = null;
        while (requestList != null && requestList.size() > 0) {
            requestInfo = requestList.get(0);
            requestList.remove(0);
            requestInfo = findShouldCheckPermission(context, requestInfo);
            if (requestInfo != null) {
                break;
            }
        }
        if (requestInfo == null) {
            isRequesting = false;
        }

        return requestInfo;
    }

}

