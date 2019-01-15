package cn.com.venvy.common.track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.com.venvy.common.utils.VenvyPreferenceHelper;

/**
 * Created by Arthur on 2017/5/9.
 */

public class TrackConfig {
    private Context mContext;

    public static final String TRACK_VIDEO_ID = "VenvyVideoId";
    public static final String TRACK_CHANNEL_ID = "VenvyChannelId";
    public static final String TRACK_BRAND_ID = "VenvyBrandId";
    public static final String TRACK_PREFERENCE_NAME = "tarck-cache";

    public TrackConfig(Context context) {
        mContext = context;
    }


    public void setVideoId(@NonNull String videoId) {
        VenvyPreferenceHelper.putString(mContext, TRACK_PREFERENCE_NAME, TRACK_VIDEO_ID, videoId);
    }

    public String getChannelId() {
        return VenvyPreferenceHelper.getString(mContext, TRACK_PREFERENCE_NAME, TRACK_CHANNEL_ID, "");
    }

    public String getVideoId() {
        return VenvyPreferenceHelper.getString(mContext, TRACK_PREFERENCE_NAME, TRACK_VIDEO_ID, "");
    }

    public void setChannelId(@NonNull String channelId) {
        VenvyPreferenceHelper.putString(mContext, TRACK_PREFERENCE_NAME, TRACK_CHANNEL_ID, channelId);
    }

    public String getBrandId() {
        return VenvyPreferenceHelper.getString(mContext, TRACK_PREFERENCE_NAME, TRACK_BRAND_ID, "");
    }

    public void setBrandId(@NonNull String brandId) {
        VenvyPreferenceHelper.putString(mContext, TRACK_PREFERENCE_NAME, TRACK_BRAND_ID, brandId);
    }

    public boolean isBrandIdEffective() {
        return !TextUtils.isEmpty(getBrandId());
    }

    public boolean isChannelIdEffective() {
        return !TextUtils.isEmpty(getChannelId());
    }

    public boolean isVideoIdEffective() {
        return !TextUtils.isEmpty(getVideoId());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" TrackConfig is {");
        builder.append(" mVideoId : ");
        builder.append(getVideoId());
        builder.append(", mChannelId : ");
        builder.append(getChannelId());
        builder.append(", mBrandId : ");
        builder.append(getBrandId());
        builder.append("}");
        return builder.toString();
    }
}
