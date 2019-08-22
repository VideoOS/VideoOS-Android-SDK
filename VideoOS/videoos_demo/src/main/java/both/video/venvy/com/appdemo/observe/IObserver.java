package both.video.venvy.com.appdemo.observe;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/***
 * lgf
 * observer基类 管理生命周期
 */
public class IObserver<V extends IView> implements LifecycleObserver {
    private V modeView;
    private static final String TAG = "IObserver";

    public IObserver(V iView) {
        modeView = iView;
    }

    public V getModeView() {
        return modeView;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.i(TAG, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.i(TAG, "onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.i(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.i(TAG, "onCreate");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.i(TAG, "onStop");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
    }

    /***
     * 匹配所有事件
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny() {
        Log.i(TAG, "onAny");
    }
}
