package cn.com.venvy.common.interf;

/**
 * Created by mac on 18/4/2.
 */

public interface ISvgaImageView {
    void parse(String url, ISvgaParseCompletion parseCompletion);

    void setCallback(SVGACallback svgaCallback);

    void startAnimation();

    void stopAnimation();

    void stopAnimation(boolean isForce);

    void pauseAnimation();

    void startAnimation(int location, int length, boolean reverse);

    void setLoops(int loops);

    boolean isAnimating();

    void stepToPercentage(Double percentage, boolean andPlay);

    void stepToFrame(int frame, boolean andPlay);

    void setClearsAfterStop(boolean clearsAfterStop);

    void setFillMode(int type);

}
