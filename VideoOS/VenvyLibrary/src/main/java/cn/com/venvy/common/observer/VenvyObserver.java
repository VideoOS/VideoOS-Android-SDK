package cn.com.venvy.common.observer;

import android.os.Bundle;



public interface VenvyObserver {
    void notifyChanged(VenvyObservable observable, String tag, Bundle bundle);
}
