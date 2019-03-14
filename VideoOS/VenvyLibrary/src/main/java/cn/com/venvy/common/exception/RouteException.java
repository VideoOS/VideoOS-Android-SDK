package cn.com.venvy.common.exception;

public class RouteException extends IException {

	private static final long serialVersionUID = 13489792L;

	public RouteException() {
        super();
    }

    public RouteException(String msg) {
        super(msg);
    }

    public RouteException(String msg, Exception exception) {
        super(msg, exception);
    }

    public RouteException(Exception exception) {
        super(exception);
    }
}

