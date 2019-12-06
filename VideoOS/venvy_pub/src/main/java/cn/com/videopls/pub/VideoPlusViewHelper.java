package cn.com.videopls.pub;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.HashMap;

import cn.com.venvy.common.interf.RotateStatus;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by Lucas on 2019/8/2.
 */
public class VideoPlusViewHelper implements VenvyObserver {

    private VideoPlusView videoPlusView;

    public VideoPlusViewHelper(VideoPlusView videoPlusView) {
        this.videoPlusView = videoPlusView;
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_SCREEN_CHANGED, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_UPDATE_VISION_TITLE, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_H5_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_CLOSE_H5_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_LAUNCH_DESKTOP_PROGRAM, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_TOP_LEVEL, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_CLEAR_ALL_VISION_PROGRAM, this);
    }


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, final Bundle bundle) {
        switch (tag) {
            case VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM: {

                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        // 创建一个视联网小程序
                        if (bundle != null) {
                            String appletsId = bundle.getString(VenvyObservableTarget.KEY_APPLETS_ID);
                            int orientationType = Integer.parseInt(bundle.getString(VenvyObservableTarget.KEY_ORIENTATION_TYPE));
                            int appType = Integer.parseInt(bundle.getString(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE));
                            String data = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DATA);
                            if (TextUtils.isEmpty(appletsId)) {
                                VenvyLog.e("try to launch a vision program , but appletsId is null");
                                return;
                            }

                            if (videoPlusView != null) {
                                if (videoPlusView.getAdapter() != null) {
                                    if (videoPlusView.getAdapter().buildWidgetRotationListener() != null) {
                                        videoPlusView.getAdapter().buildWidgetRotationListener().onRotate(orientationType == RotateStatus.TO_LANDSCAPE.getId() ?
                                                RotateStatus.TO_LANDSCAPE : RotateStatus.TO_VERTICAL);
                                    }

                                }
                                if (appType == VenvyObservableTarget.Constant.CONSTANT_APP_TYPE_TOOLS) {
                                    videoPlusView.launchVisionToolsProgram(appletsId,data); // 请求视联网小工具
                                } else {
                                    videoPlusView.launchVisionProgram(appletsId, data, orientationType, appType == VenvyObservableTarget.Constant.CONSTANT_APP_TYPE_H5);
                                }
                            }
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM: {
                // 根据id关闭一个视联网小程序
                if (bundle != null) {
                    String appletsId = bundle.getString(VenvyObservableTarget.KEY_APPLETS_ID);
                    if (TextUtils.isEmpty(appletsId)) {
                        VenvyLog.e("try to close a vision program , but appletsId is null");
                        return;
                    }
                    if (videoPlusView != null) {
                        videoPlusView.closeVisionProgram(appletsId);
                    }
                }
                return;
            }
            case VenvyObservableTarget.TAG_SCREEN_CHANGED: {
                // 当目前展示的是横屏小程序，切横屏的时候销毁掉
                if (videoPlusView != null) {
                    ScreenStatus screenStatus = (ScreenStatus) bundle.getSerializable(VenvyObservableTarget.Constant.CONSTANT_SCREEN_CHANGE);
                    videoPlusView.changeVisionProgramByOrientation(screenStatus == ScreenStatus.LANDSCAPE);
                }

                return;
            }
            case VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_MSG);
                        boolean needRetry = bundle.getBoolean(VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY);
                        String data = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DATA);
                        if (videoPlusView != null) {
                            videoPlusView.showExceptionLogic(msg, needRetry, data);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_UPDATE_VISION_TITLE: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        String title = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_TITLE);
                        boolean nvgShow = bundle.getBoolean(VenvyObservableTarget.Constant.CONSTANT_NVG_SHOW);
                        if (videoPlusView != null) {
                            videoPlusView.setCurrentVisionProgramTitle(title, nvgShow);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_H5_VISION_PROGRAM: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoPlusView != null) {
                            String h5Url = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_H5_URL);
                            String developerUserId = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DEVELOPER_USER_ID);
                            videoPlusView.launchH5VisionProgram(h5Url, developerUserId);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_CLOSE_H5_VISION_PROGRAM: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoPlusView != null) {
                            String appletId = bundle.getString(VenvyObservableTarget.KEY_APPLETS_ID);
                            videoPlusView.closeH5VisionProgram(appletId);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_LAUNCH_DESKTOP_PROGRAM: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoPlusView != null) {
                            String luaName = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_LUA_NAME);
                            String miniAppInfo = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_MINI_APP_INFO);
                            String videoModeType = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_VIDEO_MODE_TYPE);
                            String originData = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DATA);
                            videoPlusView.launchDesktopProgram(luaName, miniAppInfo, videoModeType,originData);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_TOP_LEVEL: {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (videoPlusView != null) {
                            String luaName = bundle.getString("template");
                            String id = bundle.getString("id");
                            HashMap<String, String> data = (HashMap<String, String>) bundle.getSerializable("data");
                            videoPlusView.launchProgramToTopLevel(luaName, id, data);
                        }
                    }
                });
                return;
            }
            case VenvyObservableTarget.TAG_CLEAR_ALL_VISION_PROGRAM:{
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoPlusView != null){
                            videoPlusView.clearAllVisionProgram();
                        }
                    }
                });
                return;
            }
        }
    }


    public void detachedFromWindow() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_SCREEN_CHANGED, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_UPDATE_VISION_TITLE, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_H5_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLOSE_H5_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_LAUNCH_DESKTOP_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_TOP_LEVEL, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLEAR_ALL_VISION_PROGRAM, this);
    }

    public boolean isHorizontal() {
        Configuration configuration = videoPlusView.getContext().getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
