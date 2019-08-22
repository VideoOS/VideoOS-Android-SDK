package cn.com.videopls.pub;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
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


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (plusViewHelper != null) {
            plusViewHelper.detachedFromWindow();
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 手机竖屏

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 手机横屏

        }
    }

    private void init() {
        plusViewHelper = new VideoPlusViewHelper(this);

        programViewA = createTypeAProgram();
        programViewB = createTypeBProgram();
        addView(programViewA);
        addView(programViewB);
        programViewB.setClickable(false);
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

    /**
     * clear所有的视联网小程序
     */
    public void clearAllVisionProgram() {
        if (programViewB != null) {
            programViewB.closeAllProgram();
        }
    }


    /**
     * 展示视联网小程序异常情况
     *
     * @param msg
     * @param needRetry
     */
    public void showExceptionLogic(String msg, boolean needRetry) {
        if (programViewB != null) {
            programViewB.showExceptionLogic(msg, needRetry);
        }

    }

    public void setCurrentVisionProgramTitle(String title) {
        if (programViewB != null) {
            programViewB.setCurrentProgramTitle(title);
        }
    }

    /**
     * clear指定方向的视联网小程序
     *
     * @param orientationType
     */
    public void clearAllVisionProgramByOrientation(int orientationType) {
        if (programViewB != null) {
            programViewB.closeAllProgramByOrientation(orientationType);
        }
    }

    public void changeVisionprogramByOrientation(boolean isHorizontal) {
        if (programViewB != null) {
            programViewB.setVisibility(isHorizontal ? VISIBLE : GONE);
        }
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
        if (programViewB != null) {
            programViewB.setVideoOSAdapter(adapter);
        }
    }

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

    /**
     * 拉起一个视联网小程序
     */
    public void launchVisionProgram(@NonNull String appletId, String data, final int orientationType) {
        if (programViewB != null) {
            programViewB.setClickable(true);
            programViewB.start(appletId, data, orientationType);
        }
    }

    /**
     * 根据指定ID关闭一个视联网小程序
     *
     * @param appletId
     */
    public void closeVisionProgram(String appletId) {
        if (programViewB != null) {
            programViewB.close(appletId);
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
