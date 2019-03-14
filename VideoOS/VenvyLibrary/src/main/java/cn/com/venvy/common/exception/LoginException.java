package cn.com.venvy.common.exception;

/**
 * Created by mac on 18/1/5.
 */

public class LoginException extends IException {
    public LoginException() {
        super();
    }

    public LoginException(String msg) {
        super(msg);
    }

    public LoginException(String msg, Exception exception) {
        super(msg, exception);
    }

    public LoginException(Exception exception) {
        super(exception);
    }

}
