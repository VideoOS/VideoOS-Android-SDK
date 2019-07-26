package cn.com.videopls.pub;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by yanjiangbo on 2017/5/16.
 */

public class ServiceQueryAdsInfo implements Parcelable {

    private final String mQueryAdsTemplate;
    private final String mQueryAdsId;
    private final int mQueryAdsType;

    private ServiceQueryAdsInfo(Builder builder) {
        this.mQueryAdsTemplate = builder.mQueryAdsTemplate;
        this.mQueryAdsId = builder.mQueryAdsId;
        this.mQueryAdsType = builder.mQueryAdsType;
    }

    public String getQueryAdsTemplate() {
        return mQueryAdsTemplate;
    }

    public String getQueryAdsId() {
        return mQueryAdsId;
    }

    public int getQueryAdsType() {
        return mQueryAdsType;
    }

    public static class Builder {
        private String mQueryAdsTemplate;
        private String mQueryAdsId;
        private int mQueryAdsType;

        public Builder setQueryAdsId(String queryAdsId) {
            if (!TextUtils.isEmpty(queryAdsId)) {
                this.mQueryAdsId = queryAdsId;
            }
            return this;
        }

        public Builder setQueryAdsTemplate(String queryAdsTemplate) {
            if (!TextUtils.isEmpty(queryAdsTemplate)) {
                this.mQueryAdsTemplate = queryAdsTemplate;
            }
            return this;
        }

        public Builder setQueryAdsType(int queryAdsType) {
            this.mQueryAdsType = queryAdsType;
            return this;
        }


        public ServiceQueryAdsInfo build() {
            return new ServiceQueryAdsInfo(this);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mQueryAdsTemplate);
        dest.writeString(this.mQueryAdsId);
        dest.writeInt(this.mQueryAdsType);
    }

    protected ServiceQueryAdsInfo(Parcel in) {
        this.mQueryAdsTemplate = in.readString();
        this.mQueryAdsId = in.readString();
        this.mQueryAdsType = in.readInt();
    }

    public static final Parcelable.Creator<ServiceQueryAdsInfo> CREATOR = new Parcelable.Creator<ServiceQueryAdsInfo>() {
        @Override
        public ServiceQueryAdsInfo createFromParcel(Parcel source) {
            return new ServiceQueryAdsInfo(source);
        }

        @Override
        public ServiceQueryAdsInfo[] newArray(int size) {
            return new ServiceQueryAdsInfo[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(mQueryAdsId) ? super.hashCode() : mQueryAdsId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return super.equals(obj);
        }
        if (!(obj instanceof ServiceQueryAdsInfo)) {
            return super.equals(obj);
        }
        ServiceQueryAdsInfo queryAdsInfo = (ServiceQueryAdsInfo) obj;
        return TextUtils.equals(mQueryAdsId,queryAdsInfo.getQueryAdsId());
    }
}
