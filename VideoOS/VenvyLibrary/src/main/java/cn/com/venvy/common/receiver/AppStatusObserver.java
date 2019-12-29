package cn.com.venvy.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/12/17.
 * <p>
 * 监听其他App的安装和卸载
 */
public class AppStatusObserver {

    private Context mContext;

    private AppStatusReceiver mReceiver;

    public AppStatusObserver(Context mContext) {
        this.mContext = mContext;
    }


    public void registerReceiver(String targetPackage, AppStatusChangeListener appStatusChangeListener) {
        if (mReceiver == null) {
            mReceiver = new AppStatusReceiver();
        }
        mReceiver.setAppStatusChangeListener(targetPackage, appStatusChangeListener);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);// 有新的app被安装
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);// 有新的app被卸载
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    public void unRegisterReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }

    }

    private static class AppStatusReceiver extends BroadcastReceiver {


        private AppStatusChangeListener appStatusChangeListener;
        private String targetPackageName;

        public void setAppStatusChangeListener(String packageName, AppStatusChangeListener appStatusChangeListener) {
            this.appStatusChangeListener = appStatusChangeListener;
            this.targetPackageName = packageName;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getData().getSchemeSpecificPart();
            if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(action)) {
                VenvyLog.d("add app : " + packageName);
                if (appStatusChangeListener != null && packageName.equalsIgnoreCase(targetPackageName)) {
                    appStatusChangeListener.onAppInstall(packageName);
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(action) && packageName.equalsIgnoreCase(targetPackageName)) {
                VenvyLog.d("uninstall app : " + packageName);
                appStatusChangeListener.onAppUninstall(packageName);
            }
        }
    }

    public interface AppStatusChangeListener {
        void onAppInstall(String packageName);

        void onAppUninstall(String packageName);
    }
}
