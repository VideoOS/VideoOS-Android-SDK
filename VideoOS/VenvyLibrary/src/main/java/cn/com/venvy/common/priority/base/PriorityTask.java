package cn.com.venvy.common.priority.base;


import android.support.annotation.NonNull;

import cn.com.venvy.common.priority.Priority;
import cn.com.venvy.common.priority.PriorityTaskCallback;

/**
 * Created by Arthur on 2017/8/2.
 */

public abstract class PriorityTask implements Runnable, Comparable<PriorityTask> {
    @NonNull
    private Priority mPriority = Priority.NORMAL;
    private boolean isCanceled = false;
    private PriorityTaskCallback mPriorityTaskCallback;

    public PriorityTask(Priority priority) {
        setPriority(priority);
    }

    public PriorityTask() {

    }

    public void cancel(boolean cancel) {
        isCanceled = cancel;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void run() {
        if (isCanceled) {
            return;
        }
        execute();
    }

    public void setPriorityTaskCallback(PriorityTaskCallback priorityTaskCallback) {
        mPriorityTaskCallback = priorityTaskCallback;
    }

    public void executeCallback() {
        if (mPriorityTaskCallback != null) {
            mPriorityTaskCallback.execute(this);
        }
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void cancelTask() {
        isCanceled = true;
    }

    public abstract void execute();

    public abstract int getTaskId();

    @Override
    public int compareTo(@NonNull PriorityTask priorityTask) {
        if (mPriority.ordinal() < priorityTask.getPriority().ordinal()) {
            return 1;
        } else if (mPriority.ordinal() > priorityTask.getPriority().ordinal()) {
            return -1;
        } else {
            return 0;
        }
    }
}
