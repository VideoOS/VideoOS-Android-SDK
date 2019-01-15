package cn.com.venvy.common.track;

/**
 * 打点参数异常
 * Created by Arthur on 2017/5/9.
 */

public class TrackParamsException extends IllegalArgumentException {
    public TrackParamsException() {
    }

    public TrackParamsException(String s) {
        super(s);
    }

    public TrackParamsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackParamsException(Throwable cause) {
        super(cause);
    }
}
