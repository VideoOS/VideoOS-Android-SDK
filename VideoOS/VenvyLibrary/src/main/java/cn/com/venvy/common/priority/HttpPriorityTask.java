package cn.com.venvy.common.priority;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.priority.base.PriorityTask;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * 优先级请求任务
 * Created by Arthur on 2017/8/1.
 */

public class HttpPriorityTask extends PriorityTask {
    //优先级
    private Request mRequest;

    public HttpPriorityTask(@NonNull Request request){
        super(request.getPriority());
        mRequest = request;
    }

    public HttpPriorityTask() {

    }

    public void setRequest(Request request) {
        mRequest = request;
    }
    public Request getRequest() {
        return mRequest;
    }

    public void execute() {
        if(isCanceled()) {
            return;
        }
        executeCallback();
        VenvyLog.i("task proprity " + getPriority().name() + " is finish");
    }

    @Override
    public int getTaskId() {
        return mRequest.url.hashCode();
    }

    @Override
    public void cancelTask() {
        super.cancelTask();
    }

}
