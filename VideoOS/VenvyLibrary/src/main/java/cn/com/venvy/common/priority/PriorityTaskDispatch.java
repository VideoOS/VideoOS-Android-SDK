package cn.com.venvy.common.priority;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.venvy.common.priority.base.PriorityTask;
import cn.com.venvy.common.utils.VenvyLog;


/**
 * Created by Arthur on 2017/8/1.
 */

public class PriorityTaskDispatch {
    private ExecutorService mExecutorService;

    private final Deque<PriorityTask> mRunningAsyncCalls = new ArrayDeque<>();

    public PriorityTaskDispatch() {

        PriorityBlockingQueue<Runnable> priorityBlockingQueue =
                new PriorityBlockingQueue<>
                        (60, new PriorityTaskComparator<Runnable>());
        mExecutorService = new ThreadPoolExecutor(
                1,
                1,
                20,
                TimeUnit.SECONDS,
                priorityBlockingQueue,
                threadFactory("Priority Dispatcher", false));
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override public Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    public PriorityTaskDispatch(ExecutorService executorService) {
        mExecutorService = executorService;
    }

    public  void enqueue(PriorityTask priorityTask) {
        mRunningAsyncCalls.add(priorityTask);
        mExecutorService.execute(priorityTask);
    }


    public void cancel(PriorityTask priorityTask) {
        for (PriorityTask task : mRunningAsyncCalls) {
            if (task.getTaskId() == priorityTask.getTaskId()) {
                VenvyLog.i("cancel task");
                priorityTask.cancelTask();
                mRunningAsyncCalls.remove(task);
                break;
            }
        }
    }

    public List<PriorityTask> runningPriorityTasks() {
        List<PriorityTask> result = new ArrayList<>();
        for (PriorityTask priorityTask : mRunningAsyncCalls) {
            result.add(priorityTask);
        }
        return Collections.unmodifiableList(result);
    }

    public  void cancelAll() {
        VenvyLog.i("task is empty ? " + mRunningAsyncCalls.isEmpty());
        if (mRunningAsyncCalls.isEmpty()) {
            return;
        }
        for (PriorityTask task : mRunningAsyncCalls) {
            task.cancelTask();
        }
        mRunningAsyncCalls.clear();
        try {
            if (mExecutorService.isShutdown()) {
                VenvyLog.i("task thread pool shutdown ");
                mExecutorService.shutdownNow();
            }
        } catch (SecurityException e) {
        }
    }

    public void finished(PriorityTask priorityTask) {
        synchronized (this) {
            if (!mRunningAsyncCalls.remove(priorityTask)) {
                VenvyLog.i(" remove priority task error");
                return;
            }
        }
        if (mRunningAsyncCalls.isEmpty()) {
            VenvyLog.i("priority quue is empty");
        }
    }


}
