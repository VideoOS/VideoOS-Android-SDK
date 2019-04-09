package cn.com.videopls.pub;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.VideoType;

/**
 * Created by yanjiangbo on 2017/5/16.
 */

public class Provider implements Parcelable {

    private final String mAppKey;
    private final String mAppSecret;
    private final String mPlatformId;
    private final String mVideoPath;
    private final VideoType mVideoType;
    private final String mVideoTitle;
    private final ScreenStatus mDirection;
    private final String mVideoCategory;


    private String mPackageName;
    private String mCustomUDID;


    @Deprecated
    private final int mHorVideoWidth;
    @Deprecated
    private final int mHorVideoHeight;
    @Deprecated
    private final int mVerVideoHeight;
    @Deprecated
    private final int mVerVideoWidth;
    @Deprecated
    private final int mVerticalSmallVideoHeight;
    @Deprecated
    private final int mVerticalSmallVideoWidth;

    private final String mExtendJSONString;

    private Provider(Builder builder) {
        this.mAppKey = builder.mAppKey;
        this.mAppSecret = builder.mAppSecret;
        this.mHorVideoHeight = builder.mHorVideoHeight;
        this.mHorVideoWidth = builder.mHorVideoWidth;
        this.mVerVideoHeight = builder.mVerVideoHeight;
        this.mVerVideoWidth = builder.mVerVideoWidth;
        this.mVideoType = builder.mVideoType;
        this.mVideoPath = builder.mVideoPath;
        this.mVideoTitle = builder.mVideoTitle;
        this.mPlatformId = builder.mPlatformId;
        this.mDirection = builder.mDirection;
        this.mVerticalSmallVideoHeight = builder.mVerticalSmallVideoHeight;
        this.mVerticalSmallVideoWidth = builder.mVerticalSmallVideoWidth;
        this.mPackageName = builder.mPackageName;
        this.mCustomUDID = builder.mCustomUDID;
        this.mVideoCategory = builder.videoCategory;
        this.mExtendJSONString = builder.mExtendJSONString;
    }

    public String getExtendJSONString() {
        return mExtendJSONString;
    }

    public String getVideoCategory() {
        return mVideoCategory;
    }

    public ScreenStatus getDirection() {
        return mDirection;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public String getAppSecret() {
        return mAppSecret;
    }

    public int getHorVideoWidth() {
        return mHorVideoWidth;
    }

    public int getHorVideoHeight() {
        return mHorVideoHeight;
    }

    public int getVerVideoHeight() {
        return mVerVideoHeight;
    }

    public int getVerVideoWidth() {
        return mVerVideoWidth;
    }

    public VideoType getVideoType() {
        return mVideoType;
    }

    public String getVideoTitle() {
        return mVideoTitle;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public String getCustomUDID() {
        return mCustomUDID;
    }

    @Deprecated
    public String getPlatformId() {
        return mPlatformId;
    }

    public String getPackageName() {
        return mPackageName;
    }


    public static class Builder {
        private String mAppKey;
        private String mAppSecret;
        private int mHorVideoWidth;
        private int mHorVideoHeight;
        private int mVerVideoHeight;
        private int mVerVideoWidth;
        private int mVerticalSmallVideoHeight;
        private int mVerticalSmallVideoWidth;

        private VideoType mVideoType;
        private String mVideoTitle;
        private String mVideoPath;
        private String mPlatformId;
        private ScreenStatus mDirection;
        private String mPackageName;
        private String mCustomUDID;
        private String videoCategory;
        private String mExtendJSONString;

        @Deprecated
        public Builder setTestUserId(String testUserId) {
            return this;
        }

        public Builder setExtendJSONString(String extendJSONString) {
            this.mExtendJSONString = extendJSONString;
            return this;
        }

        public Builder setCategory(String videoCategory) {
            this.videoCategory = videoCategory;
            return this;
        }

        public Builder setAppKey(String appKey) {
            if (!TextUtils.isEmpty(appKey)) {
                this.mAppKey = appKey;
            }
            return this;
        }

        public Builder setAppSecret(String appSecret) {
            if (!TextUtils.isEmpty(appSecret)) {
                this.mAppSecret = appSecret;
            }
            return this;
        }

        public Builder setCustomUDID(String udid) {
            this.mCustomUDID = udid;
            return this;
        }

        @Deprecated
        public Builder setVerticalFullVideoHeight(int verticalSmallVideoHeight) {
            mVerticalSmallVideoHeight = verticalSmallVideoHeight;
            return this;
        }

        @Deprecated
        public Builder setVerticalFullVideoWidth(int verticalSmallVideoWidth) {
            mVerticalSmallVideoWidth = verticalSmallVideoWidth;
            return this;
        }

        @Deprecated
        public Builder setVerticalType(int value) {
            this.mDirection = ScreenStatus.getStatusById(value);
            return this;
        }

        @Deprecated
        public Builder setDirection(int direction) {
            this.mDirection = ScreenStatus.getStatusById(direction);
            return this;
        }

        public Builder setDirection(ScreenStatus direction) {
            this.mDirection = direction;
            return this;
        }

        @Deprecated
        public Builder setVideoPath(String path) {
            this.mVideoPath = path;
            return this;
        }

        public Builder setVideoID(String path) {
            this.mVideoPath = path;
            return this;
        }

        public Builder setPlatformId(String platformId) {
            mPlatformId = platformId;
            return this;
        }

        @Deprecated
        public Builder setHorVideoWidth(int horVideoWidth) {
            this.mHorVideoWidth = horVideoWidth;
            return this;
        }

        @Deprecated
        public Builder setHorVideoHeight(int horVideoHeight) {
            this.mHorVideoHeight = horVideoHeight;
            return this;
        }

        @Deprecated
        public Builder setVerVideoHeight(int verVideoHeight) {
            this.mVerVideoHeight = verVideoHeight;
            return this;
        }

        @Deprecated
        public Builder setVerVideoWidth(int verVideoWidth) {
            this.mVerVideoWidth = verVideoWidth;
            return this;
        }

        public Builder setVideoType(VideoType type) {
            this.mVideoType = type;
            return this;
        }

        public Builder setVideoTitle(String videoTitle) {
            this.mVideoTitle = videoTitle;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.mPackageName = packageName;
            return this;
        }


        public Provider build() {
            return new Provider(this);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAppKey);
        dest.writeString(this.mAppSecret);
        dest.writeInt(this.mHorVideoWidth);
        dest.writeInt(this.mHorVideoHeight);
        dest.writeInt(this.mVerVideoHeight);
        dest.writeInt(this.mVerVideoWidth);
        dest.writeInt(this.mVideoType.getId());
        dest.writeString(this.mVideoTitle);
        dest.writeString(this.mVideoPath);
        dest.writeString(this.mPlatformId);
        dest.writeInt(this.mDirection.getId());
        dest.writeInt(this.mVerticalSmallVideoHeight);
        dest.writeInt(this.mVerticalSmallVideoWidth);
        dest.writeString(this.mCustomUDID);
        dest.writeString(this.mVideoCategory);
        dest.writeString(this.mExtendJSONString);
    }

    protected Provider(Parcel in) {
        this.mAppKey = in.readString();
        this.mAppSecret = in.readString();
        this.mHorVideoWidth = in.readInt();
        this.mHorVideoHeight = in.readInt();
        this.mVerVideoHeight = in.readInt();
        this.mVerVideoWidth = in.readInt();
        this.mVideoType = VideoType.getStatusById(in.readInt());
        this.mVideoTitle = in.readString();
        this.mVideoPath = in.readString();
        this.mPlatformId = in.readString();
        this.mDirection = ScreenStatus.getStatusById(in.readInt());
        this.mVerticalSmallVideoHeight = in.readInt();
        this.mVerticalSmallVideoWidth = in.readInt();
        this.mCustomUDID = in.readString();
        this.mVideoCategory = in.readString();
        this.mExtendJSONString = in.readString();
    }

    public static final Parcelable.Creator<Provider> CREATOR = new Parcelable.Creator<Provider>() {
        @Override
        public Provider createFromParcel(Parcel source) {
            return new Provider(source);
        }

        @Override
        public Provider[] newArray(int size) {
            return new Provider[size];
        }
    };
}
