package cn.com.videopls.pub.ott;

import android.content.Context;
import android.util.AttributeSet;

import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;

/*
 * Created by yanjiangbo on 2017/5/17.
 */

public class VideoOTTView extends VideoPlusView {

    public VideoOTTView(Context context) {
        super(context);
    }

    public VideoOTTView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoOTTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoPlusController initVideoPlusController() {
        return new VideoOTTController(this);
    }
}
