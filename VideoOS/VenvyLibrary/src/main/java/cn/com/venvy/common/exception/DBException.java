package cn.com.venvy.common.exception;

public class DBException extends IException {

	private static final long serialVersionUID = 1L;

	public DBException() {
        super();
    }

    public DBException(String msg) {
        super(msg);
    }

    public DBException(String msg, Exception exception) {
        super(msg, exception);
    }

    public DBException(Exception exception) {
        super(exception);
    }
}

