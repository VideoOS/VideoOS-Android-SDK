package cn.com.venvy.common.bean;

import android.support.annotation.Nullable;

/**
 * 组件信息
 * Created by Arthur on 2017/7/19.
 */

public class WidgetInfo {
    private String mAdId;//点播对应的flowID
    private String mResourceId;//点播对应的nodeID
    @Nullable
    private String mWidgetType;
    private String mWidgetName;
    private String mUrl;
    private WidgetActionType mWidgetActionType;
    //是否需要获取焦点
    private boolean mNeedFocus;

    private WidgetInfo(Builder builder) {
        mAdId = builder.mAdId;
        mResourceId = builder.mResourceId;
        mWidgetType = builder.mWidgetType;
        mWidgetName = builder.mWidgetName;
        mWidgetActionType = builder.mWidgetActionType;
        mUrl = builder.mUrl;
        mNeedFocus = builder.mNeedFocus;
    }


    public String getAdId() {
        return mAdId;
    }

    public String getResourceId() {
        return mResourceId;
    }

    public boolean getNeedFocus() {
        return mNeedFocus;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getWidgetType() {
        return mWidgetType;
    }

    public String getWidgetName() {
        return mWidgetName;
    }

    public WidgetActionType getWidgetActionType() {
        return mWidgetActionType;
    }

    public static final class Builder {
        private String mAdId;
        private String mWidgetType;
        private String mWidgetName;
        private WidgetActionType mWidgetActionType;
        private String mUrl;
        private String mResourceId;
        private boolean mNeedFocus;

        public Builder setAdId(String adId) {
            this.mAdId = adId;
            return this;
        }

        @Deprecated
        public Builder setResourceId(String resourceId) {
            this.mResourceId = resourceId;
            return this;
        }

        @Deprecated
        public Builder setWidgetType(@Nullable String widgetType) {
            this.mWidgetType = widgetType;
            return this;
        }

        public Builder setWidgetName(@Nullable String widgetName) {
            this.mWidgetName = widgetName;
            return this;
        }

        public Builder setWidgetActionType(@Nullable WidgetActionType widgetActionType) {
            this.mWidgetActionType = widgetActionType;
            return this;
        }

        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setNeedFocus(boolean needFocus) {
            this.mNeedFocus = needFocus;
            return this;
        }

        public WidgetInfo build() {
            return new WidgetInfo(this);
        }

    }

    public enum WidgetActionType {
        ACTION_NONE(0),
        ACTION_OPEN_URL(1),
        ACTION_PAUSE_VIDEO(2),
        ACTION_PLAY_VIDEO(3),
        ACTION_GET_ITEM(4);
        private int typeId;

        WidgetActionType(int id) {
            this.typeId = id;
        }

        public static WidgetActionType findTypeById(int id) {
            switch (id) {
                case 0:
                    return ACTION_NONE;
                case 1:
                    return ACTION_OPEN_URL;
                case 2:
                    return ACTION_PAUSE_VIDEO;
                case 3:
                    return ACTION_PLAY_VIDEO;
                case 4:
                    return ACTION_GET_ITEM;
                default:
                    return ACTION_NONE;
            }
        }
    }
}
