package cn.com.venvy.common.track;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arthur on 2017/5/9.
 */

public class TrackParams { //曝光
    public static final String CAT_EXPOSURE = "12";
    //点击
    public static final String CAT_CLICK = "9";
    //关闭
    public static final String CAT_CLOSE = "20";
    //Hover
    public static final String CAT_HOVER = "21";
    //拖拽
    public static final String CAT_DRAG = "31";
    //播放
    public static final String CAT_VIDEO_PLAY = "4";
    //暂停
    public static final String CAT_VIDEO_PAUSE = "17";
    //视频关闭
    public static final String CAT_VIDEO_CLOSE = "19";

    private String mCat;
    private String mSsId;
    private String mObjectId;
    private String mVideoId;
    private String mChannelId;
    private String mBrandId;
    private String mInfoId;
    private List<String> mTargetIds = new ArrayList<>();

    public String getCat() {
        return mCat;
    }

    public void setCat(@NonNull String cat) {
        this.mCat = cat;
    }

    public String getSsId() {
        return mSsId;
    }

    public void setSsId(@Nullable String ssId) {
        this.mSsId = ssId;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(@NonNull String objectId) {
        this.mObjectId = objectId;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.mVideoId = videoId;
    }

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(@NonNull String channelId) {
        this.mChannelId = channelId;
    }

    public String getBrandId() {
        return mBrandId;
    }

    public void setBrandId(@Nullable String brandId) {
        this.mBrandId = brandId;
    }

    public String getInfoId() {
        return mInfoId;
    }

    public void setInfoId(@Nullable String infoId) {
        this.mInfoId = infoId;
    }

    public String getTargetIds() {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0, size = mTargetIds.size(); i < size; i++) {
            builder.append(mTargetIds.get(i));
            if (i != size - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public void addTargetId(@Nullable String targetId) {
        mTargetIds.add(targetId);
    }

    /**
     * 检测cat是否有效
     */
    public boolean isCatEffective() {
        return !TextUtils.isEmpty(mCat) && (
                CAT_EXPOSURE.equals(mCat) || CAT_CLICK.equals(mCat) ||
                        CAT_CLOSE.equals(mCat) || CAT_HOVER.equals(mCat) ||
                        CAT_DRAG.equals(mCat) || CAT_VIDEO_PLAY.equals(mCat) ||
                        CAT_VIDEO_PAUSE.equals(mCat) || CAT_VIDEO_CLOSE.equals(mCat)
        );
    }

    public boolean isVideoIdEffective() {
        return !TextUtils.isEmpty(mVideoId);
    }

    public boolean isChannelIdEffective() {
        return !TextUtils.isEmpty(mChannelId);
    }

    public boolean isBrandIdEffective() {
        return !TextUtils.isEmpty(mBrandId);
    }

    public boolean isInfoEffective() {
        return !TextUtils.isEmpty(mChannelId);
    }

    public boolean isTargetIdEffective() {
        return !mTargetIds.isEmpty();
    }


    public boolean isSsIdEffective() {
        return !TextUtils.isEmpty(mSsId);
    }

    public boolean isObjectIdEffective() {
        return !TextUtils.isEmpty(mObjectId);
    }

    /**
     * 判断除了catId其余参数是否必要
     * 当cat = 12,9,20,21,31的时候其余参数都是必须的
     *
     * @return
     */
    public boolean isOtherParamsNecessaryExceptCat() {
        return !TextUtils.isEmpty(mCat) && (
                CAT_EXPOSURE.equals(mCat) || CAT_CLICK.equals(mCat) ||
                        CAT_CLOSE.equals(mCat) || CAT_HOVER.equals(mCat) ||
                        CAT_DRAG.equals(mCat)
        );
    }

    @Override
    public String toString() {
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("\"mCat\":\"");
        jsonBuilder.append(mCat);
        jsonBuilder.append("\",\"mSsId\":\"");
        jsonBuilder.append(mSsId);
        jsonBuilder.append("\",\"mObjectId\":");
        jsonBuilder.append(mObjectId);
        jsonBuilder.append(",\"mInfoId\":");
        jsonBuilder.append(mInfoId);
        jsonBuilder.append(",\"mVideoId\":");
        jsonBuilder.append(mVideoId);
        jsonBuilder.append(",\"mChannelId\":");
        jsonBuilder.append(mChannelId);
        jsonBuilder.append(",\"mBrandId\":");
        jsonBuilder.append(mBrandId);
        jsonBuilder.append(",\"mTargetIds\":\"");
        jsonBuilder.append(getTargetIds());
        jsonBuilder.append("\"");
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
