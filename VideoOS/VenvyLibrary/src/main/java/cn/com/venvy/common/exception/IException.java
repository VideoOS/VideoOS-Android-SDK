package cn.com.venvy.common.exception;

public class IException extends Exception {

    private static final long serialVersionUID = 1L;

    public IException() {
        super();
    }

    public IException(String msg) {
        super(msg);
    }

    public IException(String msg, Exception exception) {
        super(msg, exception);
    }

    public IException(Exception exception) {
        super(exception);
    }

}
