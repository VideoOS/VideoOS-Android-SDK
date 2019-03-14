package cn.com.venvy.common.exception;

/**
 * Created by Arthur on 2017/5/16.
 */

public class HttpException extends Exception{
    private static final long serialVersionUID = 1L;

    public HttpException() {
        super();
    }

    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(String msg, Exception exception) {
        super(msg, exception);
    }

    public HttpException(Exception exception) {
        super(exception);
    }
}
