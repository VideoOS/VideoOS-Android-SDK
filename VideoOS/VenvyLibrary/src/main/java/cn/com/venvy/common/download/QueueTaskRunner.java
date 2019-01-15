package cn.com.venvy.common.download;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by yanjiangbo on 2017/6/20.
 */

public abstract class QueueTaskRunner<Task extends QueueTaskRunner.ITask, Result> {

    public interface ITask {

        int getTaskId();

        boolean isComplete();
    }


    boolean mCanceled = false;
    List<Task> mSuccessTask;
    List<Task> mFailedTask;
    private boolean mIsWorking = false;

    @NonNull
    private final Queue<Task> mTasks = new LinkedList<Task>();
    private TaskListener<Task, Result> mListener;
    private AsyncTask<Void, Void, Void> mAsyncTask;


    public Collection<Task> getAllTask() {
        return mTasks;
    }

    @WorkerThread
    protected abstract Result execute(Task task) throws Throwable;

    public boolean isWorking() {
        return mIsWorking;
    }

    public void destroy() {
        if (!mCanceled && getAllTask().size() > 0) {
            cancel();
        }
    }

    public boolean cancel() {
        if (isWorking() && !mCanceled) {
            mCanceled = true;
            getAllTask().clear();
            if (mSuccessTask != null) {
                mSuccessTask.clear();
            }
            if (mFailedTask != null) {
                mFailedTask.clear();
            }
            if (mAsyncTask != null) {
                mAsyncTask.cancel(true);
            }
            return true;
        }
        return false;
    }

    public boolean startTasks(@NonNull final List<Task> tasks, final TaskListener<Task, Result> listener) {
        try {
            if (isWorking()) {
                if (VenvyUIUtil.isOnUIThread()) {
                    VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                        @Override
                        public void run() {
                            startTasks(tasks, listener);
                        }
                    }, 3000);
                } else {
                    Thread.sleep(3000);
                    startTasks(tasks, listener);
                }
                return false;
            }
            mListener = listener;
            return startInternal(tasks);
        } catch (Exception e) {
            VenvyLog.e(QueueTaskRunner.class.getName(), e);
            return false;
        }
    }

    public boolean startTask(@NonNull final Task task, final TaskListener<Task, Result> listener) {
        ArrayList<Task> list = new ArrayList<>();
        list.add(task);
        return startTasks(list, listener);
    }

    private boolean startInternal(@NonNull List<Task> tasks) {
        if (tasks.isEmpty()) {
            return false;
        }
        mSuccessTask = new ArrayList<>();
        mFailedTask = new ArrayList<>();
        mCanceled = false;
        mIsWorking = true;
        getAllTask().clear();
        getAllTask().addAll(tasks);
        if (VenvyUIUtil.isOnUIThread()) {
            mAsyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    execNextTask();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mAsyncTask = null;
                    mIsWorking = false;
                }
            };
            mAsyncTask.execute();
        } else {
            execNextTask();
            mAsyncTask = null;
            mIsWorking = false;
        }
        return true;
    }

    @WorkerThread
    protected void execNextTask() {
        if (mCanceled) return;
        if (getAllTask().isEmpty()) {
            onTasksComplete(mSuccessTask, mFailedTask);
        } else {
            Task task = getSingleTask();
            if (task != null && !task.isComplete()) {
                if (execTask(task)) {
                    mSuccessTask.add(task);
                } else {
                    mFailedTask.add(task);
                }
            }
            execNextTask();
        }
    }

    protected Task getSingleTask() {
        Collection<Task> collection = getAllTask();
        if (collection != null && collection instanceof Queue) {
            Queue<Task> queue = (Queue) getAllTask();
            return queue.poll();
        }
        return null;
    }

    @WorkerThread
    protected boolean execTask(Task task) {
        if (mCanceled) return false;
        Result result = null;
        Throwable throwable = null;
        onTaskStart(task);
        try {
            result = execute(task);
        } catch (Throwable e) {
            e.printStackTrace();
            throwable = e;
        }
        if (result != null) {
            onSingleTaskSuccess(task, result);
            return true;
        } else {
            onSingleTaskFailed(task, throwable);
            return false;
        }
    }

    void setProgress(final Task task, final int progress) {
        final TaskListener<Task, Result> listener = mListener;
        if (listener != null && !listener.isFinishing()) {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskProgress(task, progress);
                }
            });
        }
    }

    void onTaskStart(final Task task) {
        final TaskListener<Task, Result> listener = mListener;
        if (listener != null && !listener.isFinishing()) {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskStart(task);
                }
            });
        }
    }

    void onSingleTaskFailed(final Task task, final Throwable throwable) {
        final TaskListener<Task, Result> listener = mListener;
        if (listener != null && !listener.isFinishing()) {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskFailed(task, throwable);
                }
            });
        }
    }

    void onSingleTaskSuccess(final Task task, final Result result) {
        final TaskListener<Task, Result> listener = mListener;
        if (listener != null && !listener.isFinishing()) {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTaskSuccess(task, result);
                }
            });
        }
    }

    void onTasksComplete(final List<Task> successfulTasks, final List<Task> failedTasks) {
        final TaskListener<Task, Result> listener = mListener;
        if (listener != null && !listener.isFinishing()) {
            VenvyUIUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listener.onTasksComplete(successfulTasks, failedTasks);
                }
            });
        }
    }
}
