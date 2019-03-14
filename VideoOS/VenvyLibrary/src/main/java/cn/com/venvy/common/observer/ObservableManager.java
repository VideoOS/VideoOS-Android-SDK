package cn.com.venvy.common.observer;


import android.util.SparseArray;
import cn.com.venvy.common.utils.VenvyLog;


public class ObservableManager {

    private static final String TAG = "ObservableManager";

    private static ObservableManager sInstance = null;

    private SparseArray<VenvyObservable> mObservableSparseArray;

    public synchronized static ObservableManager getInstance() {

        if (sInstance == null) {
            sInstance = new ObservableManager();
        }
        return sInstance;
    }

    private ObservableManager() {
        mObservableSparseArray = new SparseArray<>();
    }

    public <T extends VenvyObservable> T getObservable(Class<T> t) {

        VenvyObservable observable = mObservableSparseArray.get(t.hashCode());
        if (observable == null) {
            try {
                observable = t.newInstance();
                mObservableSparseArray.put(t.hashCode(), observable);
            } catch (InstantiationException e) {
                VenvyLog.e(TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                VenvyLog.e(TAG, e.getMessage());
            }
        }
        // 仅在内部使用一次强制类型转换，外部调用者不再需要强制类型转换
        return (T) observable;
    }

    public static VenvyObservable getDefaultObserable() {
        return ObservableManager.getInstance().getObservable(VenvyObservable.class);
    }
}

