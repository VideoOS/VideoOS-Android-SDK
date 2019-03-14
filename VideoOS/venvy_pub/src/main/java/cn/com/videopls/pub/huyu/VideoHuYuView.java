package cn.com.videopls.pub.huyu;

import android.content.Context;
import android.util.AttributeSet;

import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;
import cn.com.videopls.pub.os.VideoOsView;

/**
 * Create by qinpc on 2017/12/4
 */
@Deprecated
public class VideoHuYuView extends VideoPlusView {

    public VideoHuYuView(Context context) {
        this(context, null);
    }

    public VideoHuYuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoHuYuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoPlusController initVideoPlusController() {
        return new VideoHuYuController(this);
    }

}
