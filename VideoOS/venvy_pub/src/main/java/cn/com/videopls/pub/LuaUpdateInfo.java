package cn.com.videopls.pub;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.luaj.vm2.ast.Str;

/**
 * Created by yanjiangbo on 2017/5/16.
 */

public class LuaUpdateInfo implements Parcelable {

    private final String mVersion;
    private final String mDownloadUrl;
    private final String mManifestJson;
    private LuaUpdateInfo(Builder builder) {
        this.mVersion = builder.mVersion;
        this.mDownloadUrl = builder.mDownloadUrl;
        this.mManifestJson=builder.mManifestJson;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getManifestJson() {
        return mManifestJson;
    }

    public static class Builder {
        private String mVersion;
        private String mDownloadUrl;
        private String mManifestJson;

        public Builder setVersion(String version) {
            if (!TextUtils.isEmpty(version)) {
                this.mVersion = version;
            }
            return this;
        }

        public Builder setDownloadUrl(String downloadUrl) {
            if (!TextUtils.isEmpty(downloadUrl)) {
                this.mDownloadUrl = downloadUrl;
            }
            return this;
        }
        public Builder setManifestJson(String manifestJson) {
            if (!TextUtils.isEmpty(manifestJson)) {
                this.mManifestJson = manifestJson;
            }
            return this;
        }

        public LuaUpdateInfo build() {
            return new LuaUpdateInfo(this);
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mVersion);
        dest.writeString(this.mDownloadUrl);
        dest.writeString(this.mManifestJson);
    }

    protected LuaUpdateInfo(Parcel in) {
        this.mVersion = in.readString();
        this.mDownloadUrl = in.readString();
        this.mManifestJson=in.readString();
    }

    public static final Parcelable.Creator<LuaUpdateInfo> CREATOR = new Parcelable.Creator<LuaUpdateInfo>() {
        @Override
        public LuaUpdateInfo createFromParcel(Parcel source) {
            return new LuaUpdateInfo(source);
        }

        @Override
        public LuaUpdateInfo[] newArray(int size) {
            return new LuaUpdateInfo[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(mVersion) ? super.hashCode() : mVersion.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return super.equals(obj);
        }
        if (!(obj instanceof ServiceQueryAdsInfo)) {
            return super.equals(obj);
        }
        LuaUpdateInfo luaUpdateInfo = (LuaUpdateInfo) obj;
        return TextUtils.equals(mVersion, luaUpdateInfo.getVersion());
    }
}
