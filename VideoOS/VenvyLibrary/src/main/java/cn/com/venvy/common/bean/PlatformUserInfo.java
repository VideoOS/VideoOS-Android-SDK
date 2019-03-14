package cn.com.venvy.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import cn.com.venvy.common.utils.VenvyLog;

/**
 * Create by qinpc on 2017/9/12
 */
public class PlatformUserInfo implements Parcelable {

    public enum UserType {
        Consumer,
        Anchor
    }

    @Nullable
    public String uid;

    @Nullable
    private String userName;

    @Nullable
    private String nickName;

    @Nullable
    public String userToken;

    @Nullable
    public String phoneNum;

    public String streamId;

    public UserType userType = UserType.Consumer;

    public String customerDeviceId;

    public String extendJSONString;

    @Deprecated
    public String roomId;

    @Deprecated
    public String platformId;

    @Deprecated
    public boolean isUserApp = true;

    @Deprecated
    public boolean isAnchor;

    @Deprecated
    public boolean isPush;

    @Deprecated
    public boolean isPortraitFullScreen;

    public String getCustomerDeviceId() {
        return customerDeviceId;
    }

    public void setCustomerDeviceId(String uttId) {
        this.customerDeviceId = uttId;
    }

    @Nullable
    public String getUid() {
        return uid;
    }

    public void setUid(@Nullable String uid) {
        this.uid = uid;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    public void setExtendJSONString(String extendJSONString) {
        this.extendJSONString = extendJSONString;
    }

    public String getExtendJSONString() {
        return extendJSONString;
    }

    @Nullable
    public String getNickName() {
        return nickName;
    }

    public void setNickName(@Nullable String nickName) {
        this.nickName = nickName;
    }

    @Nullable
    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(@Nullable String userToken) {
        this.userToken = userToken;
    }

    @Nullable
    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(@Nullable String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
        if (userType == UserType.Consumer) {
            isUserApp = true;
            isAnchor = false;
        } else {
            isAnchor = true;
            isUserApp = false;
        }
    }

    public UserType getUserType() {
        return userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.userName);
        dest.writeString(this.nickName);
        dest.writeString(this.userToken);
        dest.writeString(this.phoneNum);
        dest.writeString(this.roomId);
        dest.writeString(this.platformId);
        dest.writeString(this.streamId);
        dest.writeByte(this.isUserApp ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAnchor ? (byte) 1 : (byte) 0);
        dest.writeString(this.customerDeviceId);
        dest.writeString(this.extendJSONString);
    }

    public PlatformUserInfo() {

    }

    public PlatformUserInfo(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        uid = jsonObject.optString("uid");
        userName = jsonObject.optString("userName");
        nickName = jsonObject.optString("nickName");
        userToken = jsonObject.optString("userToken");
        phoneNum = jsonObject.optString("phoneNum");
        userType = jsonObject.optInt("userType", 0) == 0 ? UserType.Consumer : UserType.Anchor;
        customerDeviceId = jsonObject.optString("customerDeviceId");
        isAnchor = userType == UserType.Anchor;
        isUserApp = userType == UserType.Consumer;
        streamId = jsonObject.optString("streamId");
        extendJSONString = jsonObject.optString("extendJSONString");
    }

    protected PlatformUserInfo(Parcel in) {
        this.uid = in.readString();
        this.userName = in.readString();
        this.nickName = in.readString();
        this.userToken = in.readString();
        this.phoneNum = in.readString();
        this.roomId = in.readString();
        this.platformId = in.readString();
        this.streamId = in.readString();
        this.isUserApp = in.readByte() != 0;
        this.isAnchor = in.readByte() != 0;
        this.customerDeviceId = in.readString();
        this.extendJSONString = in.readString();
    }

    public static final Parcelable.Creator<PlatformUserInfo> CREATOR = new Parcelable.Creator<PlatformUserInfo>() {
        @Override
        public PlatformUserInfo createFromParcel(Parcel source) {
            return new PlatformUserInfo(source);
        }

        @Override
        public PlatformUserInfo[] newArray(int size) {
            return new PlatformUserInfo[size];
        }
    };

    @Override
    public String toString() {
        try {
            JSONObject result = new JSONObject();
            result.put("uid", getUid());
            result.put("userName", getUserName());
            result.put("nickName", getNickName());
            result.put("userToken", getUserToken());
            result.put("phoneNum", getPhoneNum());
            result.put("userType", userType == UserType.Consumer ? 0 : 1);
            result.put("streamId", streamId);
            result.put("customerDeviceId", TextUtils.isEmpty(customerDeviceId) ? "" : customerDeviceId);
            result.put("extendJSONString", extendJSONString);
            return result.toString();
        } catch (Exception e) {
            VenvyLog.e(PlatformUserInfo.class.getName(), e);
        }
        return null;
    }
}
