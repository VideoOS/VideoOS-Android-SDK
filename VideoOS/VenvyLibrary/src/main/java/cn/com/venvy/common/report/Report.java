package cn.com.venvy.common.report;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.com.venvy.Platform;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/5/4.
 */

public class Report {


    private static ReportHelper mReportHelper;

    public enum ReportLevel {

        i(0, "info", 0b00000001, true),
        w(1, "warning", 0b00000010, true),
        e(2, "error", 0b00000100, true),
        u(3, "u", 0b00001000, true),
        d(4, "default", 0b00010000, true);

        private int mSing = -1;
        private boolean mEnable;
        private int mTagNum;
        private String mName;

        ReportLevel(int sing, String name, int num, boolean enable) {
            mSing = sing;
            mEnable = enable;
            mTagNum = num;
            mName = name;
        }

        public void setEnable(boolean enable) {
            this.mEnable = enable;
        }

        public boolean isEnable() {
            return mEnable;
        }

        public int getTagNum() {
            return mTagNum;
        }

        public String getName() {
            return mName;
        }

        public int getValue() {
            return mSing;
        }

        public static void buildLevelAble(int value) {
            i.setEnable(((i.getTagNum() & value) >> i.getValue()) == 1);
            w.setEnable(((w.getTagNum() & value) >> w.getValue()) == 1);
            e.setEnable(((e.getTagNum() & value) >> e.getValue()) == 1);
            u.setEnable(((u.getTagNum() & value) >> u.getValue()) == 1);
            d.setEnable(((d.getTagNum() & value) >> d.getValue()) == 1);
        }

        public static ReportLevel getLevel(int value) {
            if (value == i.getValue()) {
                return i;
            }
            if (value == w.getValue()) {
                return w;
            }
            if (value == e.getValue()) {
                return e;
            }
            if (value == u.getValue()) {
                return u;
            }
            return d;
        }
    }

    public static void initReport(Platform platform) {
        if (mReportHelper == null) {
            mReportHelper = new ReportHelper(platform);
        }
    }

    public static void report(@NonNull final ReportLevel level, @NonNull final String tag, @NonNull final String reportString) {

        if (!level.isEnable()) {
            VenvyLog.w("the level is " + level.getValue() + " of Report has closed");
            return;
        }
        if (mReportHelper == null) {
            return;
        }
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.level = level;
        reportInfo.message = reportString;
        reportInfo.tag = tag;
        mReportHelper.report(reportInfo);
    }

    @Deprecated
    public static void report(@NonNull Exception e) {
        report("crash", e);
    }

    public static void report(String tag, @NonNull Exception e) {
        report(tag, e, null);
    }

    public static void report(String tag, @NonNull Exception e, String ex) {
        if (!ReportLevel.e.isEnable()) {
            VenvyLog.w("the level is " + ReportLevel.e.getName() + " of Report has closed");
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(ex)) {
            builder.append("ex:");
            builder.append(ex);
            builder.append("\\n");
        }
        if (!TextUtils.isEmpty(e.toString())) {
            builder.append("Cause by:");
            builder.append(e.toString());
            builder.append("\n");
        }
        builder.append("\\n");
        StackTraceElement[] element = e.getStackTrace();
        if (element != null) {
            for (StackTraceElement i : element) {
                builder.append(i.toString());
                builder.append("\\n");
            }
        }
        report(ReportLevel.e, tag, builder.toString());
    }

    public static void report(@NonNull final ReportInfo reportInfo) {

        if (mReportHelper == null) {
            return;
        }
        if (!reportInfo.level.isEnable()) {
            VenvyLog.w("the level is " + reportInfo.level.getValue() + " of Report has closed");
            return;
        }
        if (TextUtils.isEmpty(reportInfo.tag) || TextUtils.isEmpty(reportInfo.message) || reportInfo.level == null) {
            VenvyLog.e("reportInfo is not vaild");
            return;
        }
        mReportHelper.report(reportInfo);

    }

    public static void setReportAble(boolean reportAble) {
        if (mReportHelper == null) {
            return;
        }
        mReportHelper.setReportEnable(reportAble);
    }

    public static boolean isReportAble() {
        return mReportHelper.isEnable();
    }

    public static void onDestroy() {
        if (mReportHelper != null) {
            mReportHelper.onDestroy();
        }
        mReportHelper = null;
    }
}
