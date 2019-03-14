package cn.com.venvy.common.router;

import cn.com.venvy.common.exception.IException;

/**
 * Created by yanjiangbo on 2018/1/26.
 */

public class RouteInitException extends IException {

    private static final long serialVersionUID = 13489724192L;

    public RouteInitException() {
        super();
    }

    public RouteInitException(String msg) {
        super(msg);
    }

    public RouteInitException(String msg, Exception exception) {
        super(msg, exception);
    }

    public RouteInitException(Exception exception) {
        super(exception);
    }
}
