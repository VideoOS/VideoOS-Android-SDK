package cn.com.venvy.common.http.urlconnection;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by mac on 18/3/23.
 */

class Dispatcher {

    private ExecutorService executorService;


    private final Deque<WeakReference<AsyncCall>> runningAsyncCalls = new ArrayDeque<>();
    private final Deque<WeakReference<SyncCall>> runningSyncCalls = new ArrayDeque<>();

    public Dispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Dispatcher() {
        this(null);
    }

    synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), threadFactory("Venvy HttpURLConnection Dispatcher", false));
        }
        return executorService;
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }


    public synchronized void executeAsync(AsyncCall asyncCall) {
        executorService().execute(asyncCall);
        runningAsyncCalls.add(new WeakReference<>(asyncCall));
    }

    public synchronized void executeSync(SyncCall syncCall) {
        runningSyncCalls.add(new WeakReference<>(syncCall));
    }


    public synchronized void cancelAll() {
        for (WeakReference reference : runningAsyncCalls) {
            AsyncCall asyncCall = (AsyncCall) reference.get();
            if (asyncCall != null) {
                asyncCall.cancel();
            }
        }

        for (WeakReference reference : runningSyncCalls) {
            SyncCall asyncCall = (SyncCall) reference.get();
            if (asyncCall != null) {
                asyncCall.cancel();
            }
        }

        runningAsyncCalls.clear();
        runningSyncCalls.clear();
    }


    public void remove(AsyncCall asyncCall) {
        removeAsyncCallByID(asyncCall.getRequestId());
    }


    public void removeAsyncCallByID(int requestId) {
        for (WeakReference reference : runningAsyncCalls) {
            AsyncCall asyncCall = (AsyncCall) reference.get();
            if (asyncCall != null && asyncCall.getRequestId() == requestId) {
                asyncCall.cancel();
            }
        }
    }

    public void remove(SyncCall syncCall) {
        removeSyncCallByID(syncCall.getRequestId());
    }

    public void removeSyncCallByID(int requestId) {
        for (WeakReference reference : runningSyncCalls) {
            SyncCall asyncCall = (SyncCall) reference.get();
            if (asyncCall != null && asyncCall.getRequestId() == requestId) {
                asyncCall.cancel();
            }
        }

    }
}
