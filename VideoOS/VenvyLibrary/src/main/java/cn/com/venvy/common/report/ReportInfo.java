package cn.com.venvy.common.report;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yanjiangbo on 2017/5/5.
 */

public class ReportInfo implements Parcelable {

    long id;
    public Report.ReportLevel level;
    public String tag;
    public String message;
    String createTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.level.getValue());
        dest.writeString(this.tag);
        dest.writeString(this.message);
        dest.writeString(this.createTime);
    }

    public ReportInfo() {

    }

    protected ReportInfo(Parcel in) {
        this.id = in.readLong();
        int leaveType = in.readInt();
        this.level = Report.ReportLevel.getLevel(leaveType);
        this.tag = in.readString();
        this.message = in.readString();
        this.createTime = in.readString();
    }

    public static final Creator<ReportInfo> CREATOR = new Creator<ReportInfo>() {
        @Override
        public ReportInfo createFromParcel(Parcel source) {
            return new ReportInfo(source);
        }

        @Override
        public ReportInfo[] newArray(int size) {
            return new ReportInfo[size];
        }
    };
}
