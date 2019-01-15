package cn.com.venvy.common.download;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public interface TaskListener<Task, Result> {

    /**
     * @return 返回true则不再继续调用
     */
    @MainThread
    boolean isFinishing();

    /**
     * 任务开始
     */
    @MainThread
    void onTaskStart(Task task);

    /**
     * 任务进度
     */
    @MainThread
    void onTaskProgress(Task task, int progress);

    /**
     * 任务失败
     */
    @MainThread
    void onTaskFailed(Task task, @Nullable Throwable throwable);

    /**
     * 任务成功
     */
    @MainThread
    void onTaskSuccess(Task task, Result result);

    /**
     * 全部任务完成
     */
    @MainThread
    void onTasksComplete(@Nullable List<Task> successfulTasks, @Nullable List<Task> failedTasks);
}
