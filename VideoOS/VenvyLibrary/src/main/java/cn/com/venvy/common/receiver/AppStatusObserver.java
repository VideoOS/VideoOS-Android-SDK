package cn.com.venvy.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

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


    public void registerReceiver() {
        if (mReceiver == null) {
            mReceiver = new AppStatusReceiver();
        }
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

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String packageName = intent.getData().getSchemeSpecificPart();
            if(Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(action)){
                Toast.makeText(context,"add : "+packageName,Toast.LENGTH_SHORT).show();
            }else if(Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(action)){
                Toast.makeText(context,"uninstall : "+packageName,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
