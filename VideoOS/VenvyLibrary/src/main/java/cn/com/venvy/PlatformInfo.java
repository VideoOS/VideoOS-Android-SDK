package cn.com.venvy;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.VideoType;

/**
 * Created by yanjiangbo on 2017/5/2.
 */

public class PlatformInfo implements Parcelable {

    private final String mThirdPlatformId;
    private final String mServiceVersion;
    private final String mVideoId;
    private final String mIdentity;
    private final int videoWidth;
    private final int videoHeight;
    private final int verVideoWidth;
    private final int verVideoHeight;
    private final String mAppKey;
    private final String mAppSecret;
    private ScreenStatus mDirection;
    private VideoType mVideoType;
    private final String mVideoCategory;
    private final String mExtendJSONString;
    private final String mCustomerPackageName;
    private final String mFileProviderAuth;
    private final int notificationIcon;

    private PlatformInfo(Builder builder) {
        mThirdPlatformId = builder.mThirdPlatformId;
        mServiceVersion = builder.mServiceVersion;
        mVideoId = builder.mVideoId;
        mIdentity = builder.mIdentity;
        videoHeight = builder.videoHeight;
        videoWidth = builder.videoWidth;
        verVideoHeight = builder.verVideoHeight;
        verVideoWidth = builder.verVideoWidth;
        mAppKey = builder.mAppKey;
        mAppSecret = builder.mAppSecret;
        mDirection = builder.mInitDirection == null ? ScreenStatus.SMALL_VERTICAL : builder.mInitDirection;
        mVideoType = builder.mVideoType == null ? VideoType.VIDEOOS : builder.mVideoType;
        mVideoCategory = builder.videoCategory;
        mExtendJSONString = builder.extendJsonString;
        mCustomerPackageName = builder.mCustomerPackageName;
        mFileProviderAuth = builder.fileProviderAuth;
        notificationIcon = builder.notificationIcon;
    }

    public void updateDirection(ScreenStatus status) {
        if (status != null) {
            this.mDirection = status;
        }
    }

    public String getExtendJSONString() {
        return mExtendJSONString;
    }

    public String getVideoCategory() {
        return mVideoCategory;
    }

    public String getThirdPlatformId() {
        return mThirdPlatformId;
    }

    public ScreenStatus getInitDirection() {
        return mDirection;
    }

    public VideoType getVideoType() {
        return mVideoType;
    }

    public String getServiceVersion() {
        return mServiceVersion;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVerVideoHeight() {
        return verVideoHeight;
    }

    public int getVerVideoWidth() {
        return verVideoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public String getIdentity() {
        return mIdentity;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public String getAppSecret() {
        return mAppSecret;
    }

    public String getCustomerPackageName() {
        return mCustomerPackageName;
    }

    public String getFileProviderAuth() {
        return mFileProviderAuth;
    }

    public int getNotificationIcon() {
        return notificationIcon;
    }

    public static class Builder {

        private String mThirdPlatformId;
        private String mServiceVersion;
        private String mVideoId;
        private String mIdentity;
        private int videoWidth;
        private int videoHeight;
        private int verVideoWidth;
        private int verVideoHeight;
        private String mAppKey;
        private String mAppSecret;
        private ScreenStatus mInitDirection;
        private VideoType mVideoType;
        private String videoCategory;
        private String extendJsonString;
        private String mCustomerPackageName;
        private String fileProviderAuth;
        private int notificationIcon;

        public Builder setCustomerPackageName(String mCustomerPackageName) {
            this.mCustomerPackageName = mCustomerPackageName;
            return this;
        }

        public Builder setExtendJSONString(String extendJsonString) {
            this.extendJsonString = extendJsonString;
            return this;
        }

        public Builder setVideoCategory(String category) {
            this.videoCategory = category;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.mAppKey = appKey;
            return this;
        }
        public Builder setAppSecret(String appSecret) {
            this.mAppSecret = appSecret;
            return this;
        }
        //初始化屏幕大小及方向
        public Builder setInitDirection(ScreenStatus status) {
            this.mInitDirection = status;
            return this;
        }

        public Builder setVideoType(VideoType videoType) {
            this.mVideoType = videoType;
            return this;
        }

        /**
         * 设置对接平台标识
         */
        public Builder setThirdPlatform(String thirdPlatformId) {
            this.mThirdPlatformId = thirdPlatformId;
            return this;
        }

        public Builder setIdentity(String identity) {
            this.mIdentity = identity;
            return this;
        }

        @Deprecated
        public Builder setVideoHeight(int videoHeight) {
            this.videoHeight = videoHeight;
            return this;
        }

        @Deprecated
        public Builder setVideoWidth(int videoWidth) {
            this.videoWidth = videoWidth;
            return this;
        }

        @Deprecated
        public Builder setVerVideoHeight(int videoHeight) {
            this.verVideoHeight = videoHeight;
            return this;
        }

        @Deprecated
        public Builder setVerVideoWidth(int videoWidth) {
            this.verVideoWidth = videoWidth;
            return this;
        }


        /**
         * 设置service后台版本号
         */
        public Builder setServiceVersion(String serviceVersion) {
            this.mServiceVersion = serviceVersion;
            return this;
        }

        public Builder setVideoId(String videoId) {
            this.mVideoId = videoId;
            return this;
        }

        public Builder setFileProviderAuth(String fileProviderAuth) {
            this.fileProviderAuth = fileProviderAuth;
            return this;
        }

        public Builder setNotificationIcon(int notificationIcon) {
            this.notificationIcon = notificationIcon;
            return this;
        }

        public PlatformInfo builder() {
            return new PlatformInfo(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mThirdPlatformId);
        dest.writeString(this.mServiceVersion);
        dest.writeString(this.mVideoId);
        dest.writeString(this.mIdentity);
        dest.writeInt(this.videoWidth);
        dest.writeInt(this.videoHeight);
        dest.writeInt(this.verVideoWidth);
        dest.writeInt(this.verVideoHeight);
        dest.writeString(this.mAppKey);
        dest.writeString(this.mAppSecret);
        dest.writeInt(this.mDirection == null ? -1 : this.mDirection.getId());
        dest.writeInt(this.mVideoType == null ? 0 : this.mVideoType.getId());
        dest.writeString(this.mVideoCategory);
        dest.writeString(this.mExtendJSONString);
        dest.writeString(this.mCustomerPackageName);
        dest.writeString(this.mFileProviderAuth);
        dest.writeInt(this.notificationIcon);
    }

    protected PlatformInfo(Parcel in) {
        this.mThirdPlatformId = in.readString();
        this.mServiceVersion = in.readString();
        this.mVideoId = in.readString();
        this.mIdentity = in.readString();
        this.videoWidth = in.readInt();
        this.videoHeight = in.readInt();
        this.verVideoWidth = in.readInt();
        this.verVideoHeight = in.readInt();
        this.mAppKey = in.readString();
        this.mAppSecret = in.readString();
        int tmpMInitDirection = in.readInt();
        this.mDirection = tmpMInitDirection == -1 ? ScreenStatus.SMALL_VERTICAL : ScreenStatus.getStatusById(tmpMInitDirection);
        this.mVideoType = tmpMInitDirection == -1 ? VideoType.VIDEOOS : VideoType.getStatusById(tmpMInitDirection);
        this.mVideoCategory = in.readString();
        this.mExtendJSONString = in.readString();
        this.mCustomerPackageName = in.readString();
        this.mFileProviderAuth = in.readString();
        this.notificationIcon = in.readInt();
    }

    public static final Parcelable.Creator<PlatformInfo> CREATOR = new Parcelable.Creator<PlatformInfo>() {
        @Override
        public PlatformInfo createFromParcel(Parcel source) {
            return new PlatformInfo(source);
        }

        @Override
        public PlatformInfo[] newArray(int size) {
            return new PlatformInfo[size];
        }
    };
}
