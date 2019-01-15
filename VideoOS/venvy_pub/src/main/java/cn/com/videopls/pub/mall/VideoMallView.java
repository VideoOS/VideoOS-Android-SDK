package cn.com.videopls.pub.mall;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;

import cn.com.venvy.common.utils.VenvyReflectUtil;
import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;
import cn.com.videopls.pub.os.VideoOsView;

/**
 * Created by mac on 17/11/27.
 */

@Deprecated
public class VideoMallView extends VideoPlusView {
    public VideoMallView(Context context) {
        super(context);
    }

    public VideoMallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoMallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoPlusController initVideoPlusController() {
        return new VideoMallController(this);
    }

    public static String getOrderUrl() {
        return (String) VenvyReflectUtil.invokeStatic(VenvyReflectUtil.getClass("cn.com.venvy.keep.MallConfig"),
                "getOrderUrl", null, null);
    }

    public static String getMallUrl() {
        return (String) VenvyReflectUtil.invokeStatic(VenvyReflectUtil.getClass("cn.com.venvy.keep.MallConfig"),
                "getMallUrl", null, null);
    }
}
