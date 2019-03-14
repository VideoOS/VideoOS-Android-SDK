package cn.com.videopls.pub.os;

import android.content.Context;
import android.util.AttributeSet;

import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;

/**
 * Created by yanjiangbo on 2017/5/17.
 */

public class VideoOsView extends VideoPlusView {

    public VideoOsView(Context context) {
        super(context);
    }

    public VideoOsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoOsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoPlusController initVideoPlusController() {
        return new VideoOsController(this);
    }
}
