package cn.com.venvy.common.permission;


import android.content.pm.PackageManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanjiangbo on 2016/12/27.
 */

public class PermissionRequestInfo {
    private int requestCode;
    private PermissionCheckHelper.PermissionCallbackListener listener;
    private String[] permissions;
    private String[] tipMessages;
    private Map<String, Integer> permissionResultMap;
    private int count = 0;

    private PermissionRequestInfo(PermissionCheckHelper.PermissionCallbackListener listener,
                                  int requestCode, String[] permissions, String[] tipMessages) {
        this.listener = listener;
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.tipMessages = tipMessages;
        initMap();
    }

    private void initMap() {
        permissionResultMap = new HashMap<>();
        if (permissions == null || permissions.length == 0) {
            return;
        }
        for (String permission : permissions) {
            permissionResultMap.put(permission, PackageManager.PERMISSION_DENIED);
            count++;
        }
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String[] getTipMessages() {
        return tipMessages;
    }

    public void setTipMessages(String[] tipMessages) {
        this.tipMessages = tipMessages;
    }

    /**
     * 根据权限请求结果更新
     *
     * @param permission
     * @param result
     */
    public void updateRequestResult(String permission, int result) {
        if (permissionResultMap != null) {
            permissionResultMap.put(permission, result);
        }
    }

    /**
     * 获取请求的权限数组
     *
     * @return
     */
    public String[] getRequestPermissions() {
        String[] permissions = new String[count];
        if (permissionResultMap != null && count > 0) {
            permissions = permissionResultMap.keySet().toArray(new String[count]);
        }
        return permissions;
    }

    /**
     * 获取权限对应的请求结果
     *
     * @return
     */
    public int[] getRequestResults() {
        int[] grantResults = new int[count];
        if (permissionResultMap != null && count > 0) {
            Integer[] results = permissionResultMap.values().toArray(new Integer[count]);
            for (int index = 0; index < count; ++index) {
                grantResults[index] = results[index];
            }
        }
        return grantResults;
    }

    /**
     * 回调权限授予结果
     */
    public void doCallback() {
        if (listener == null) {
            return;
        }
        listener.onPermissionCheckCallback(requestCode, getRequestPermissions(), getRequestResults());
    }

    /**
     * 权限全部已经授予,直接回调
     */
    public void grantedCallback() {
        if (listener == null) {
            return;
        }
        int[] grantResults = new int[count];
        Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
        listener.onPermissionCheckCallback(requestCode, getRequestPermissions(), grantResults);
    }

    public static class Builder {
        private int requestCode;
        private String[] permissions;
        private String[] tipMessages;
        private PermissionCheckHelper.PermissionCallbackListener listener;

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setRequestPermissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder setTipMessages(String... messages) {
            this.tipMessages = messages;
            return this;
        }

        public Builder setCallbackListener(PermissionCheckHelper.PermissionCallbackListener listener) {
            this.listener = listener;
            return this;
        }

        public PermissionRequestInfo build() {
            if (listener == null || permissions == null || permissions.length == 0
                    || tipMessages == null || permissions.length != tipMessages.length) {
                throw new IllegalArgumentException("permission request build failed: params error");
            }
            return new PermissionRequestInfo(listener, requestCode, permissions, tipMessages);
        }

    }
}
