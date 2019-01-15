package cn.com.venvy.common.router;

/*
 * Created by yanjiangbo on 2018/1/30.
 */

public interface InterceptorCallback {

    void onContinue();

    void onInterrupt();
}
