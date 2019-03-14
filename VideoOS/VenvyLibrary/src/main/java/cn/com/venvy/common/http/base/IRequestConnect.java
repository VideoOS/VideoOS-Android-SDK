package cn.com.venvy.common.http.base;

import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.com.venvy.common.interf.Method;

/**
 * Created by yanjiangbo on 2017/4/26.
 */

public interface IRequestConnect {

    /**
     * 异步链接请求
     */
    void connect(Request request, IRequestHandler handler);

    /**
     * 同步链接请求,不能在主线程中调用，只能在异步线程中，并且注意同步引起的性能问题
     */
    @WorkerThread
    IResponse syncConnect(Request request) throws Exception;

    boolean abort(int requestID);

    boolean abortAll();
}
