package cn.com.videopls.pub;

import android.os.Bundle;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;

/**
 * Created by Lucas on 2019/8/2.
 */
public class VideoPlusViewHelper implements VenvyObserver {

    private VideoPlusView videoPlusView;

    public VideoPlusViewHelper(VideoPlusView videoPlusView) {
        this.videoPlusView = videoPlusView;
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, this);

    }


    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {

    }




    public void detachedFromWindow(){
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, this);
    }
}
