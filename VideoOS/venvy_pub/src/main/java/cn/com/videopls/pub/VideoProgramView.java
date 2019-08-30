package cn.com.videopls.pub;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by Lucas on 2019/7/30.
 */

public class VideoProgramView<T extends VideoPlusController> extends FrameLayout {


    private T controller;


    public VideoProgramView(Context context) {
        super(context);
        init();
    }

    public VideoProgramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (controller != null) {
            controller.destroy();
        }
    }


    private void init() {
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        controller = (T) new VideoPlusController(this) {

        };
    }

    public void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        if (controller != null) {
            controller.navigation(uri, params, callback);
        }
    }

    public void closeInfoView() {
        if (controller != null) {
            controller.closeInfoView();
        }
    }

    public void removeTopView() {
        if (controller != null) {
            controller.removeTopChild();
        }
    }


    public void setVideoOSAdapter(VideoPlusAdapter adapter) {
        if (controller != null) {
            controller.setAdapter(adapter);
        }
    }


    public void start() {
        if (controller != null) {
            controller.start();
        }
    }

    public void stop() {
        if (controller != null) {
            controller.stop();
        }
    }

    public void startService(ServiceType serviceType, HashMap<String, String> params, IServiceCallback callback) {
        if (controller != null) {
            controller.startService(serviceType, params, callback);
        }
    }

    public void reResumeService(ServiceType serviceType) {
        if (controller != null) {
            controller.reResumeService(serviceType);
        }
    }

    public void pauseService(ServiceType serviceType) {
        if (controller != null) {
            controller.pauseService(serviceType);
        }
    }

    public void stopService(ServiceType serviceType) {
        if (controller != null) {
            controller.stopService(serviceType);
        }
    }

    public void startVision(String appletId, String data, int type,boolean isH5Type,final IRouterCallback callback) {
        if (controller != null) {
            controller.startVisionProgram(appletId, data, type,isH5Type,callback);
        }
    }

    /**
     * 筛出所有的luaview
     *
     * @return
     */
    public int getAllOfLuaView() {
        int count = 0;
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            if (child instanceof VideoOSLuaView) {
                count += 1;
            }
        }
        return count;
    }
}
