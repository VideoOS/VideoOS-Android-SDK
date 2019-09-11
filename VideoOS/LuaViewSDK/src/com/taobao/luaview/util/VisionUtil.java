package com.taobao.luaview.util;

import android.util.Pair;

import static cn.com.venvy.App.getContext;

/**
 * Created by Lucas on 2019/8/30.
 */
public class VisionUtil {

    /**
     * 返回视联网容器宽高
     * @return
     */
    public static Pair<Float, Float> getVisionProgramSize() {
        int screenHeight = AndroidUtil.getScreenHeight(getContext());
        int screenWidth = AndroidUtil.getScreenWidth(getContext());


        float height = Math.min(screenWidth,screenHeight) - DimenUtil.dpiToPx(44f);
        float width = Math.min(screenWidth,screenHeight) / 375.0f * 230;


        return new Pair<>(DimenUtil.pxToDpi(width),DimenUtil.pxToDpi(height));
    }
}
