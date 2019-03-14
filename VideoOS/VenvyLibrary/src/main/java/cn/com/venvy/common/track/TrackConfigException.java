package cn.com.venvy.common.track;

/**
 * Created by Arthur on 2017/5/9.
 */

public class TrackConfigException extends IllegalArgumentException {

    public TrackConfigException() {
    }

    public TrackConfigException(String s) {
        super(s);
    }

    public TrackConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrackConfigException(Throwable cause) {
        super(cause);
    }
}
