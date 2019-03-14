package cn.com.venvy.common.priority;

import java.util.Comparator;

import cn.com.venvy.common.priority.base.PriorityTask;

/**
 * Created by Arthur on 2017/8/2.
 */
class PriorityTaskComparator<Task> implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        if ((task1 instanceof PriorityTask)
                && (task2 instanceof PriorityTask)) {
            PriorityTask priorityTask1 = (PriorityTask) task1;
            PriorityTask priorityTask2 = (PriorityTask) task2;
            //数字越大优先级越高
            return priorityTask2.getPriority().ordinal() - priorityTask1.getPriority().ordinal();
        }
        return 0;
    }
}
