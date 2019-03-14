package cn.com.venvy.common.interf;

import android.support.annotation.Nullable;

/**
 * 组件点击事件
 * Created by Arthur on 2017/7/19.
 */
public interface IWidgetClickListener<Info> {
    void onClick(@Nullable Info info);
}
