package cn.com.venvy.common.observer;

import android.os.Bundle;

/**
 * Created by yanjiangbo on 2018/1/29.
 */

public abstract class ActivityStatusObserver implements VenvyObserver {

    public static final int STATUS_CREATE = 1;
    public static final int STATUS_START = 2;
    public static final int STATUS_RESTART = 3;
    public static final int STATUS_RESUME = 4;
    public static final int STATUS_STOP = 5;
    public static final int STATUS_PAUSE = 6;
    public static final int STATUS_DESTROY = 7;


    public abstract void onCreate();

    public abstract void onStart();

    public abstract void onResume();

    public abstract void onRestart();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onDestroy();


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ACTIVITY_CHANGED.equals(tag)) {
            int status = bundle.getInt("activity_status");
            switch (status) {
                case STATUS_CREATE:
                    onCreate();
                    break;
                case STATUS_RESUME:
                    onResume();
                    break;

                case STATUS_RESTART:
                    onRestart();
                    break;

                case STATUS_DESTROY:
                    onDestroy();
                    break;

                case STATUS_PAUSE:
                    onPause();
                    break;

                case STATUS_STOP:
                    onStop();
                    break;

                case STATUS_START:
                    onStart();
                    break;
            }
        }
    }
}
