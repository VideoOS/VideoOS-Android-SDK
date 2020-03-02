package cn.com.videopls.pub;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.com.venvy.App;
import cn.com.venvy.common.interf.IServiceCallback;
import cn.com.venvy.common.interf.ServiceType;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyVibrateUtil;

import static cn.com.venvy.common.interf.ServiceType.ServiceTypeVideoMode_POP;
import static cn.com.venvy.common.interf.ServiceType.ServiceTypeVideoMode_TAG;

/**
 * Created by yanjiangbo on 2017/5/17.
 */

public abstract class VideoPlusView<T extends VideoPlusController> extends FrameLayout {


    // 顶层小程序容器 4
    protected VideoProgramView programTopLevel;
    // A 类小程序 0，1
    protected VideoProgramView programViewA;

    // B 类小程序 2
    protected VideoProgramTypeBView programViewB;

    // 桌面小程序 3
    protected VideoProgramView programViewDesktop;

    private VideoPlusViewHelper plusViewHelper;

    private VideoPlusAdapter adapter;

    private Pair<Float, Float> videoModeDeskOffset; // 视联网模式桌面偏移量

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
        programTopLevel = new VideoProgramView(getContext());
        programViewA = createTypeAProgram();
        programViewB = createTypeBProgram();
        addView(programViewA);
        addView(programViewB);
        addView(programTopLevel);
        programViewB.setClickable(false);
    }

    /**
     * 生成A类小程序容器
     *
     * @return
     */
    private VideoProgramView createTypeAProgram() {
        return new VideoProgramView(getContext());
    }


    /**
     * 生成B类小程序容器
     *
     * @return
     */
    private VideoProgramTypeBView createTypeBProgram() {
        return new VideoProgramTypeBView(getContext());
    }

    private VideoProgramView createDesktopProgram() {
        return new VideoProgramView(getContext());
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
     * 开始下载第三方广告资源
     */
    public void startDownloadRequest(Bundle bundle){
        if(programViewA != null){
            programViewA.startDownloadAdsRes(bundle);
        }
    }


    /**
     * 展示视联网小程序异常情况
     *
     * @param msg
     * @param needRetry
     */
    public void showExceptionLogic(String msg, boolean needRetry, String data) {
        if (programViewB != null) {
            programViewB.showExceptionLogic(msg, needRetry, data);
        }

    }

    public void setCurrentVisionProgramTitle(String title, boolean nvgShow) {
        if (programViewB != null) {
            programViewB.setCurrentProgramTitle(title, nvgShow);
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

    public void changeVisionProgramByOrientation(boolean isHorizontal) {
        if (programViewB != null) {
            programViewB.onScreenChanged(isHorizontal);
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
        if (programTopLevel != null) {
            programTopLevel.setVideoOSAdapter(adapter);
        }
        if (programViewA != null) {
            programViewA.setVideoOSAdapter(adapter);
        }
        if (programViewB != null) {
            programViewB.setVideoOSAdapter(adapter);
        }
        this.adapter = adapter;
    }

    public VideoPlusAdapter getAdapter() {
        return adapter;
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
    public void launchVisionProgram(@NonNull String appletId, String data, final int orientationType, boolean isH5Type) {
        if (programViewB != null) {
            programViewB.setClickable(true);
            programViewB.start(appletId, data, orientationType, isH5Type);
        }
    }

    /**
     * 拉起一个视联网小工具
     */
    public void launchVisionToolsProgram(String miniAppId, String data,String level) {
        if (programViewA != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put("miniAppId", miniAppId);
            params.put("data", data);
            params.put("level", level);
            programViewA.startService(ServiceType.ServiceTypeVideoTools, params, null);
        }
    }

    /**
     * 顶层加载一个lua程序
     *
     * @param luaName
     * @param id
     */
    public void launchProgramToTopLevel(String luaName, String id, HashMap<String, String> data) {
        Uri uri = Uri.parse("LuaView://topLuaView?template=" + luaName + "&id=" + id);
        programTopLevel.navigation(uri, data, new IRouterCallback() {
            @Override
            public void arrived() {
            }

            @Override
            public void lost() {
            }
        });
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

    /**
     * 拉起一个H5小程序
     *
     * @param url
     */
    public void launchH5VisionProgram(String url, String developerUserId) {
        if (programViewB != null) {
            programViewB.setClickable(true);
            programViewB.startH5(url, developerUserId);
        }
    }


    public void launchDesktopProgram(String targetName, String miniAppInfo, String videoModeType, String originData) {
        // 桌面存在则不需要重复加载桌面
        if (programViewDesktop != null) return;

        programViewDesktop = createDesktopProgram(); // 3
        if (adapter != null) {
            programViewDesktop.setVideoOSAdapter(adapter);
        }
        addView(programViewDesktop, getChildCount() - 1);// 上层还有
        if (!TextUtils.isEmpty(targetName)) {
            programViewDesktop.setVisibility(VISIBLE);
            JSONObject jsonObject = new JSONObject();
            Uri uri = null;
            try {
                JSONObject miniAppInfoJson = new JSONObject(TextUtils.isEmpty(miniAppInfo) ? "{}" : miniAppInfo);
                uri = Uri.parse("LuaView://desktopLuaView?template=" + targetName + "&miniAppId=" + miniAppInfoJson.getString("miniAppId") + "&id=" + targetName.substring(0, targetName.lastIndexOf(".")));
                jsonObject.put(VenvyObservableTarget.Constant.CONSTANT_MINI_APP_INFO, miniAppInfoJson);//  miniAppInfo
                jsonObject.put(VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_TYPE, videoModeType);//  videoModeType
                if (videoModeDeskOffset != null && (videoModeDeskOffset.first > 0 || videoModeDeskOffset.second > 0)) {
                    // 设置桌面初始偏移量
                    jsonObject.put(VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_X_OFFSET, videoModeDeskOffset.first);
                    jsonObject.put(VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_Y_OFFSET, videoModeDeskOffset.second);
                }
                jsonObject.put(VenvyObservableTarget.Constant.CONSTANT_LABEL_CONF_DATA, new JSONObject(originData));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            HashMap<String, String> finalParams = new HashMap<>();
            finalParams.put("data", jsonObject.toString());
            programViewDesktop.navigation(uri, finalParams, null);
        }

    }

    /**
     * 根据指定ID关闭一个H5小程序
     *
     * @param appletId
     */
    public void closeH5VisionProgram(String appletId) {
        if (programViewB != null) {
            programViewB.closeH5(appletId);
        }
    }


    public void startService(ServiceType serviceType, HashMap<String, String> params, IServiceCallback callback) {
        startService(serviceType, params, new Pair<>(0.0f, 0.0f), callback);

    }

    public void startService(ServiceType serviceType, HashMap<String, String> params, Pair<Float, Float> videoModeDeskOffset, IServiceCallback callback) {
        this.videoModeDeskOffset = videoModeDeskOffset;
        switch (serviceType) {
            case ServiceTypeFrontVideo:
            case ServiceTypeLaterVideo:
            case ServiceTypePauseAd:// 前后贴，暂停贴处于顶层视图
                if (programTopLevel != null) {
                    programTopLevel.startService(serviceType, params, callback);
                }
                break;
            default:
                // 震动一下
//                VenvyVibrateUtil.vibrate(getContext(), 500);
                if (programViewA != null) {
                    programViewA.startService(serviceType, params, callback);
                }
                break;
        }
    }

    public void reResumeService(ServiceType serviceType) {

        if (programViewA != null) {
            programViewA.reResumeService(serviceType);
        }

        if (programTopLevel != null) {
            programTopLevel.reResumeService(serviceType);
        }
    }

    public void pauseService(ServiceType serviceType) {
        if (programViewA != null) {
            programViewA.pauseService(serviceType);
        }
        if (programTopLevel != null) {
            programTopLevel.pauseService(serviceType);
        }
    }

    public void stopService(ServiceType serviceType) {

        switch (serviceType) {
            case ServiceTypeFrontVideo:
            case ServiceTypeLaterVideo:
            case ServiceTypePauseAd:// 前后贴，暂停贴处于顶层视图
                if (programTopLevel != null) {
                    programTopLevel.stopService(serviceType);
                }
                break;
            default:
                if (programViewA != null) {
                    programViewA.stopService(serviceType);
                }
                break;
        }

        if (serviceType == ServiceTypeVideoMode_POP || serviceType == ServiceTypeVideoMode_TAG) {
            // 如果是关闭视联网模式，则移除视联网桌面
            if (programViewDesktop != null) {
                programViewDesktop.setVisibility(GONE);
                removeView(programViewDesktop);
                programViewDesktop = null;
            }
        }
    }

    /**
     * 开发者模式开关
     *
     * @param isDevMode
     */
    public void setDevMode(boolean isDevMode) {
        App.setIsDevMode(isDevMode);
    }
}
