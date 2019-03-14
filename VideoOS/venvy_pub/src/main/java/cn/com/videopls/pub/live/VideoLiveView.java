package cn.com.videopls.pub.live;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import cn.com.videopls.pub.VideoPlusController;
import cn.com.videopls.pub.VideoPlusView;
import cn.com.videopls.pub.os.VideoOsView;

/**
 * Created by Arthur on 2017/5/21.
 */

@Deprecated
public class VideoLiveView extends VideoPlusView {

    public VideoLiveView(Context context) {
        super(context);
    }

    public VideoLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoLiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoPlusController initVideoPlusController() {
        return new VideoLiveController(this);
    }

}
