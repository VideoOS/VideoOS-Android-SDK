package cn.com.videopls.pub;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.venvy.common.router.IRouterCallback;

/**
 * Created by yanjiangbo on 2017/5/17.
 */

public abstract class VideoPlusView<T extends VideoPlusController> extends FrameLayout {


    // A 类小程序
    protected VideoProgramView programViewA;

    // B 类小程序
    protected VideoProgramTypeBView programViewB;

    private VideoPlusViewHelper plusViewHelper;

    public VideoPlusView(Context context) {
        super(context);
        init();
    }

    public VideoPlusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoPlusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        plusViewHelper = new VideoPlusViewHelper(this);

        programViewA = createTypeAProgram();
        programViewB = createTypeBProgram();
        addView(programViewA);
        addView(programViewB);
    }

    /**
     * 生成A类小程序容器
     *
     * @return
     */
    private VideoProgramView createTypeAProgram() {
        VideoProgramView mainProgram = new VideoProgramView(getContext());
        return mainProgram;
    }


    /**
     * 生成B类小程序容器
     *
     * @return
     */
    private VideoProgramTypeBView createTypeBProgram() {
        VideoProgramTypeBView bProgram = new VideoProgramTypeBView(getContext());
        return bProgram;
    }


    public void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        if (programViewA != null) {
            programViewA.navigation(uri, params, callback);
        }
    }

    public void closeInfoView() {
        if (programViewA != null) {
            programViewA.closeInfoView();
        }
    }

    public void setVideoOSAdapter(VideoPlusAdapter adapter) {
        if (programViewA != null) {
            programViewA.setVideoOSAdapter(adapter);
        }
    }

    public abstract T initVideoPlusController();

    public void start() {
        if (programViewA != null) {
            programViewA.start();
        }
    }

    public void stop() {
        if (programViewA != null) {
            programViewA.stop();
        }
    }

    public void startPlanB() {
        if (programViewA != null) {
            programViewB.start();
        }
    }

    public void startService(ServiceType serviceType, HashMap<String, String> params, IServiceCallback callback) {
        if (programViewA != null) {
            programViewA.startService(serviceType, params, callback);
        }
    }

    public void reResumeService(ServiceType serviceType) {

        if (programViewA != null) {
            programViewA.reResumeService(serviceType);
        }
    }

    public void pauseService(ServiceType serviceType) {
        if (programViewA != null) {
            programViewA.pauseService(serviceType);
        }
    }

    public void stopService(ServiceType serviceType) {
        if (programViewA != null) {
            programViewA.stopService(serviceType);
        }
    }
}
