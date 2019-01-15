package cn.com.venvy.common.observer;

import android.os.Bundle;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.venvy.common.utils.VenvyLog;


public class VenvyObservable {

    protected HashMap<String, ArrayList<WeakReference<VenvyObserver>>> observerMap = new HashMap();

    /**
     * @param tag      action 标识
     * @param observer
     */
    public void addObserver(String tag, VenvyObserver observer) {

        if (TextUtils.isEmpty(tag)) {
            throw new NullPointerException("Observer tag can't be null");
        }
        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (VenvyObservable.class) {
            if (observerMap.containsKey(tag)) {
                ArrayList<WeakReference<VenvyObserver>> list = observerMap.get(tag);
                if (list != null) {
                    boolean needToAdd = true;
                    for (WeakReference<VenvyObserver> weakReference : list) {
                        if (weakReference != null && weakReference.get() != null && weakReference.get() == observer) {
                            needToAdd = false;
                            break;
                        }
                    }
                    if (needToAdd) {
                        list.add(new WeakReference<>(observer));
                    }
                    observerMap.put(tag, list);
                    return;
                }
            }
            ArrayList<WeakReference<VenvyObserver>> listObserver = new ArrayList();
            listObserver.add(new WeakReference<>(observer));
            observerMap.put(tag, listObserver);
        }
    }

    public boolean removeObserverByTag(String tag) {
        synchronized (VenvyObservable.class) {
            return observerMap.containsKey(tag) && observerMap.remove(tag) != null;
        }

    }


    public boolean removeObserver(String tag, VenvyObserver observer) {
        if (!observerMap.containsKey(tag)) {
            return false;
        }
        ArrayList<WeakReference<VenvyObserver>> list = observerMap.get(tag);
        if (list == null || list.isEmpty()) {
            return false;
        }
        synchronized (VenvyObservable.class) {
            WeakReference<VenvyObserver> removeTag = null;
            for (WeakReference<VenvyObserver> weakReference : list) {
                if (weakReference != null && weakReference.get() != null && weakReference.get() == observer) {
                    removeTag = weakReference;
                    break;
                }
            }
            return removeTag != null && list.remove(removeTag);
        }
    }

    public void removeAllObserver() {
        synchronized (VenvyObservable.class) {
            observerMap.clear();
        }
    }

    public void sendToTarget(String tag, Bundle bundle) {
        if (!observerMap.containsKey(tag)) {
            return;
        }
        ArrayList<WeakReference<VenvyObserver>> list = observerMap.get(tag);
        if (list == null) {
            return;
        }
        ArrayList<WeakReference<VenvyObserver>> cloneList = (ArrayList<WeakReference<VenvyObserver>>) list.clone();
        if (cloneList == null) {
            return;
        }
        synchronized (VenvyObservable.class) {
            try {
                for (WeakReference<VenvyObserver> observerWeakReference : cloneList) {
                    if (observerWeakReference != null) {
                        VenvyObserver observerResult = observerWeakReference.get();
                        if (observerResult != null) {
                            observerResult.notifyChanged(this, tag, bundle);
                        }
                    }
                }

            } catch (Exception e) {
                VenvyLog.e(VenvyObservable.class.getName(), e);
            }
        }
    }

    public void sendToTarget(String tag) {
        this.sendToTarget(tag, null);
    }

}
