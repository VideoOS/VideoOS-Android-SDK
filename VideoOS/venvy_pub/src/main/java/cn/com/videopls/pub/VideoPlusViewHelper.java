package cn.com.videopls.pub;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.utils.VenvyLog;

import static cn.com.venvy.common.interf.ScreenStatus.LANDSCAPE;

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
    }


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        switch (tag) {
            case VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM: {
                // 创建一个视联网小程序
                if (bundle != null) {
                    String appletsId = bundle.getString(VenvyObservableTarget.KEY_APPLETS_ID);
                    int orientationType = Integer.parseInt(bundle.getString(VenvyObservableTarget.KEY_ORIENTATION_TYPE));
                    String data = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DATA);
                    if (TextUtils.isEmpty(appletsId)) {
                        VenvyLog.e("try to launch a vision program , but appletsId is null");
                        return;
                    }

                    if (videoPlusView != null) {
                        videoPlusView.launchVisionProgram(appletsId, data, orientationType,isHorizontal());
                    }
                }
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
            case VenvyObservableTarget.TAG_SCREEN_CHANGED:{
                // 屏幕改变通知
                if(bundle != null){
                    ScreenStatus screenStatus = (ScreenStatus) bundle.getSerializable(VenvyObservableTarget.Constant.CONSTANT_SCREEN_CHANGE);
                    if (screenStatus == null) {
                        return;
                    }

                    if (videoPlusView != null) {
                        videoPlusView.screenChange(screenStatus == LANDSCAPE);
                    }
                }

                return;
            }
        }
    }


    public void detachedFromWindow() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_SCREEN_CHANGED, this);
    }

    public boolean isHorizontal(){
        Configuration configuration = videoPlusView.getContext().getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
