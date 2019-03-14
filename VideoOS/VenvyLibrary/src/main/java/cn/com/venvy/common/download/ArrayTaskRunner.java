package cn.com.venvy.common.download;

import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by yanjiangbo on 2017/7/5.
 */

public abstract class ArrayTaskRunner<Task extends QueueTaskRunner.ITask, Result> extends QueueTaskRunner<Task, Result> {

    private ArrayList<Task> mTask = new ArrayList<>();
    private int index = 0;
    private int mCount = -1;  // -1默认表示无线循环

    @Override
    public Collection<Task> getAllTask() {
        return mTask;
    }

    @Override
    protected void execNextTask() {
        while (!mCanceled) {
            if (index >= getAllTask().size()) {
                if (mCount >= 0) {
                    mCount -= 1;
                    if (mCount == 0) {
                        onTasksComplete(mSuccessTask, mFailedTask);
                        break;
                    }
                }
                jumpTaskIndex(0);
            }
            Task task = getSingleTask();
            if (task != null && !task.isComplete()) {
                execTask(task);
            }
            index += 1;
        }
    }

    public void looper(int count) {
        if (count == 0) {
            count = 1;
        }
        mCount = count;
    }

    @WorkerThread
    protected abstract Result execute(Task task) throws Throwable;


    public void jumpTaskIndex(int index) {
        if (index >= mTask.size()) {
            throw new RuntimeException("out of range of list index");
        }
        this.index = index;
    }

    @Override
    protected Task getSingleTask() {
        if (index >= mTask.size()) {
            throw new RuntimeException("out of range of list index");
        }
        return mTask.get(index);
    }

    @Override
    public boolean cancel() {
        index = 0;
        mCount = -1;
        mTask.clear();
        return super.cancel();
    }
}
