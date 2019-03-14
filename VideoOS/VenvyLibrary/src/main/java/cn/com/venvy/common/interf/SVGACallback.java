package cn.com.venvy.common.interf;

/**
 * Created by yanjiangbo on 2018/3/26.
 * Done
 */

public interface SVGACallback {
    void onFinished();

    void onPause();

    void onRepeat();

    void onStep(int paramInt, double paramDouble);
}
