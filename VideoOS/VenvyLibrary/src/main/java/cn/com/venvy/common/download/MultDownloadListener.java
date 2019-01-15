package cn.com.venvy.common.download;


import android.support.annotation.Nullable;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public abstract class MultDownloadListener<Task, Result> implements TaskListener<Task, Result> {

    @Override
    public void onTaskFailed(Task task, @Nullable Throwable throwable) {

    }

    @Override
    public void onTaskSuccess(Task task, Result result) {

    }
}
