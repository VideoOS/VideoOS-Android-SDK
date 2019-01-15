package cn.com.venvy.common.priority;

import cn.com.venvy.common.priority.base.PriorityTask;

/**
 * 任务监听
 * Created by Arthur on 2017/8/2.
 */

public interface PriorityTaskCallback {
    void execute(PriorityTask priorityTask);
}
