package cn.com.videopls.pub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.venvy.common.utils.VenvyResourceUtil;

/**
 * Created by Lucas on 2019/8/30.
 * 视联网小程序容器  H5容器 基类
 */
public class BaseVideoVisionView extends FrameLayout {


    protected View retryContent;
    protected View loadingContent;
    protected View errorContent;


    protected TextView tvRetry;
    protected View rlTitleBar;
    protected ImageView ivBack;
    protected TextView tvTitle;
    protected ImageView ivClose;
    protected TextView tvRetryMsg;
    protected TextView tvErrorMsg;

    protected ImageView circle1, circle2;
    protected AnimatorSet circle1Set, circle2Set;

    public BaseVideoVisionView(Context context) {
        super(context);
        init();
    }

    public BaseVideoVisionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseVideoVisionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BaseVideoVisionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), VenvyResourceUtil.getLayoutId(getContext(),
                "video_base_vision"), this);
        rlTitleBar = findViewById(VenvyResourceUtil.getId(getContext(), "rlTitleBar"));
        loadingContent = findViewById(
                VenvyResourceUtil.getId(getContext(), "loadingContent"));
        retryContent = findViewById(
                VenvyResourceUtil.getId(getContext(), "disConnectWifiContent"));
        errorContent = findViewById(
                VenvyResourceUtil.getId(getContext(), "errorContent"));
        tvRetry = (TextView) findViewById(
                VenvyResourceUtil.getId(getContext(), "tvRetry"));
        ivBack = (ImageView) findViewById(
                VenvyResourceUtil.getId(getContext(), "ivBack"));
        tvTitle = (TextView) findViewById(
                VenvyResourceUtil.getId(getContext(), "tvTitle"));
        ivClose = (ImageView) findViewById(
                VenvyResourceUtil.getId(getContext(), "ivClose"));
        circle1 = (ImageView) findViewById(
                VenvyResourceUtil.getId(getContext(), "circle1"));
        circle2 = (ImageView) findViewById(
                VenvyResourceUtil.getId(getContext(), "circle2"));
        tvRetryMsg = (TextView) findViewById(
                VenvyResourceUtil.getId(getContext(), "tvRetryMsg"));
        tvErrorMsg = (TextView) findViewById(
                VenvyResourceUtil.getId(getContext(), "tvErrorMsg"));


    }

    protected void startLoadingAnimation() {

        if (circle1Set == null) {
            circle1Set = new AnimatorSet();
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(circle1, "ScaleX", 1f, 0.5f, 1f);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(circle1, "ScaleY", 1f, 0.5f, 1f);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator[] items = new ObjectAnimator[]{scaleX, scaleY};
        circle1Set.playTogether(items);
        circle1Set.setDuration(1000).start();


        if (circle2Set == null) {
            circle2Set = new AnimatorSet();
        }
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(circle2, "ScaleX", 0.5f, 1f, 0.5f);
        scaleX2.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(circle2, "ScaleY", 0.5f, 1f, 0.5f);
        scaleY2.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator[] items2 = new ObjectAnimator[]{scaleX2, scaleY2};
        circle2Set.playTogether(items2);
        circle2Set.setDuration(1000).start();

    }

    protected void cancelLoadingAnimation() {
        if (circle1Set != null) {
            circle1Set.cancel();
            circle1Set = null;
        }
        if (circle2Set != null) {
            circle2Set.cancel();
            circle2Set = null;
        }
    }


}
