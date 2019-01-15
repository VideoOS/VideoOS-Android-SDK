
package cn.com.venvy.common.utils;


import android.util.Log;

import cn.com.venvy.common.debug.DebugStatus;


/**
 * 也可调用{@link #setTag(String)} 方法设置log的tag
 * Created by YanQiu on 2017/4/26.
 */
public class VenvyLog {

    public static boolean isDebug = DebugStatus.isDebug() || DebugStatus.isPreView();
    public static boolean needLog = true;
    private static volatile String venvyLogTag = "VenvyLog";
    private static int LOG_MAXLENGTH = 2000;

    public static void setTag(String tag) {
        venvyLogTag = tag;
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg) {
        if (isDebug || needLog) {
            Log.v(venvyLogTag + ":" + tag, buildMessage(msg));
        }
    }

    public static void v(String msg) {
        if (isDebug || needLog) {
            Log.v(venvyLogTag, buildMessage(msg));
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg       The message you would like logged.
     * @param throwable An exception to log
     */
    public static void v(String tag, String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.v(venvyLogTag + ":" + tag, buildMessage(msg), throwable);
        }
    }

    public static void v(String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.v(venvyLogTag, buildMessage(msg), throwable);
        }
    }

    public static void e(String msg) {
        if (isDebug || needLog) {
            Log.e(venvyLogTag, buildMessage(msg));
        }
    }

    /**
     * Send a DEBGU log message.
     *
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (isDebug || needLog) {
            Log.d(venvyLogTag + ":" + tag, buildMessage(msg));
        }
    }

    public static void d(String msg) {
        if (isDebug || needLog) {
            Log.d(venvyLogTag, buildMessage(msg));
        }
    }

    /**
     * Send a DEBGU log message and log the exception.
     *
     * @param msg       The message you would like logge,d.
     * @param throwable An exception to log
     */
    public static void d(String tag, String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.d(venvyLogTag + ":" + tag, buildMessage(msg), throwable);
        }
    }

    public static void d(String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.d(venvyLogTag, buildMessage(msg), throwable);
        }
    }

    /**
     * Send a INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (isDebug || needLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(venvyLogTag + ":" + tag + i, buildMessage(msg.substring(start, end)));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.i(venvyLogTag + ":" + tag + i, buildMessage(msg.substring(start, strLength)));
                    break;
                }
            }
        }
    }

    public static void i(String msg) {
        if (isDebug || needLog) {
            Log.i(venvyLogTag + ":", buildMessage(msg));
        }
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg       The message you would like logged.
     * @param throwable An exception to log
     */
    public static void i(String tag, String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.i(venvyLogTag + ":" + tag, buildMessage(msg), throwable);
        }
    }

    public static void i(String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.i(venvyLogTag, buildMessage(msg), throwable);
        }
    }

    /**
     * Send a WARN log message.
     *
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (isDebug || needLog) {
            Log.w(venvyLogTag + ":" + tag, buildMessage(msg));
        }
    }

    public static void w(String msg) {
        if (isDebug || needLog) {
            Log.w(venvyLogTag, buildMessage(msg));
        }
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg       The message you would like logged.
     * @param throwable An exception to log
     */
    public static void w(String tag, String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.w(venvyLogTag + ":" + tag, buildMessage(msg), throwable);
        }
    }

    public static void w(String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.w(venvyLogTag, buildMessage(msg), throwable);
        }
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (isDebug || needLog) {
            Log.e(venvyLogTag + ":" + tag, buildMessage(msg));
        }
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg       The message you would like logged.
     * @param throwable An exception to log
     */
    public static void e(String tag, String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.e(venvyLogTag + ":" + tag, buildMessage(msg), throwable);
        }
    }

    public static void e(String msg, Throwable throwable) {
        if (isDebug || needLog) {
            Log.e(venvyLogTag, buildMessage(msg), throwable);
        }
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    private static String buildMessage(String msg) {

        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder().append(caller.getClassName()).append(".")
                .append(caller.getMethodName()).append("()::").append(msg).toString();
    }
}
