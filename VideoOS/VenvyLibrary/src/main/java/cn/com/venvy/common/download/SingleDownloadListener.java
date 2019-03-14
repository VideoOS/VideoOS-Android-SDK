package cn.com.venvy.common.download;

import android.support.annotation.MainThread;

import java.util.List;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public abstract class SingleDownloadListener<Task, Result> implements TaskListener<Task, Result> {

    @MainThread
    public void onTasksComplete(List<Task> successfulTasks, List<Task> failedTasks) {

    }
}
