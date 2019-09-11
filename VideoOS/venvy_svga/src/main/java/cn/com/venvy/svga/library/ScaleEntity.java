package cn.com.venvy.svga.library;

import android.widget.ImageView;

/**
 * Created by yanjiangbo on 2018/3/27.
 * Done
 */

public class ScaleEntity {
    public float tranFx = 0.0f;
    public float tranFy = 0.0f;
    public float scaleFx = 1.0f;
    public float scaleFy = 1.0f;
    public float ratio = 1.0f;
    public boolean ratioX = false;

    public void resetVar() {
        tranFx = 0.0f;
        tranFy = 0.0f;
        scaleFx = 1.0f;
        scaleFy = 1.0f;
        ratio = 1.0f;
        ratioX = false;
    }

    public void performScaleType(float canvasWidth, float canvasHeight, float videoWidth, float videoHeight, ImageView.ScaleType scaleType) {
        if (canvasWidth == 0.0f || canvasHeight == 0.0f || videoWidth == 0.0f || videoHeight == 0.0f) {
            return;
        }
        resetVar();
        float canW_vidW_f = (canvasWidth - videoWidth) / 2.0f;
        float canH_vidH_f = (canvasHeight - videoHeight) / 2.0f;

        float videoRatio = videoWidth / videoHeight;
        float canvasRatio = canvasWidth / canvasHeight;

        float canH_d_vidH = canvasHeight / videoHeight;
        float canW_d_vidW = canvasWidth / videoWidth;
        switch (scaleType) {
            case CENTER:
                tranFx = canW_vidW_f;
                tranFy = canH_vidH_f;
                break;

            case CENTER_CROP:
                if (videoRatio > canvasRatio) {
                    ratio = canH_d_vidH;
                    ratioX = false;
                    scaleFx = canH_d_vidH;
                    scaleFy = canH_d_vidH;
                    tranFx = (canvasWidth - videoWidth * (canH_d_vidH)) / 2.0f;
                } else {
                    ratio = canW_d_vidW;
                    ratioX = true;
                    scaleFx = canW_d_vidW;
                    scaleFy = canW_d_vidW;
                    tranFy = (canvasHeight - videoHeight * (canW_d_vidW)) / 2.0f;
                }
                break;

            case CENTER_INSIDE:
                if (videoWidth < canvasWidth && videoHeight < canvasHeight) {
                    tranFx = canW_vidW_f;
                    tranFy = canH_vidH_f;
                } else {
                    if (videoRatio > canvasRatio) {
                        ratio = canW_d_vidW;
                        ratioX = true;
                        scaleFx = canW_d_vidW;
                        scaleFy = canW_d_vidW;
                        tranFy = (canvasHeight - videoHeight * (canW_d_vidW)) / 2.0f;

                    } else {
                        ratio = canH_d_vidH;
                        ratioX = false;
                        scaleFx = canH_d_vidH;
                        scaleFy = canH_d_vidH;
                        tranFx = (canvasWidth - videoWidth * (canH_d_vidH)) / 2.0f;
                    }
                }
                break;

            case FIT_CENTER:
                if (videoRatio > canvasRatio) {
                    ratio = canW_d_vidW;
                    ratioX = true;
                    scaleFx = canW_d_vidW;
                    scaleFy = canW_d_vidW;
                    tranFy = (canvasHeight - videoHeight * (canW_d_vidW)) / 2.0f;
                } else {
                    ratio = canH_d_vidH;
                    ratioX = false;
                    scaleFx = canH_d_vidH;
                    scaleFy = canH_d_vidH;
                    tranFx = (canvasWidth - videoWidth * (canH_d_vidH)) / 2.0f;
                }

                break;
            case FIT_START:
                if (videoRatio > canvasRatio) {
                    ratio = canW_d_vidW;
                    ratioX = true;
                    scaleFx = canW_d_vidW;
                    scaleFy = canW_d_vidW;
                } else {
                    ratio = canH_d_vidH;
                    ratioX = false;
                    scaleFx = canH_d_vidH;
                    scaleFy = canH_d_vidH;
                }
                break;

            case FIT_XY:
                ratio = Math.max(canW_d_vidW, canH_d_vidH);
                ratioX = canW_d_vidW > canH_d_vidH;
                scaleFx = canW_d_vidW;
                scaleFy = canH_d_vidH;
                break;
            case FIT_END:
                if (videoRatio > canvasRatio) {
                    ratio = canW_d_vidW;
                    ratioX = true;
                    scaleFx = canW_d_vidW;
                    scaleFy = canW_d_vidW;
                    tranFy = canvasHeight - videoHeight * (canW_d_vidW);
                } else {
                    ratio = canH_d_vidH;
                    ratioX = false;
                    scaleFx = canH_d_vidH;
                    scaleFy = canH_d_vidH;
                    tranFx = canvasWidth - videoWidth * (canH_d_vidH);
                }
                break;
            default:
                ratio = canW_d_vidW;
                ratioX = true;
                scaleFx = canW_d_vidW;
                scaleFy = canW_d_vidW;
                break;
        }
    }
}
