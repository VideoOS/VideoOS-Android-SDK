package cn.com.videopls.pub.view;

import android.content.Context;
import android.os.Bundle;

import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.processor.annotation.VenvyAutoRun;
import cn.com.venvy.VideoPositionHelper;

/*
 * Created by yanjiangbo on 2018/1/29.
 */

public abstract class VideoOSNativeView extends VideoOSBaseView implements VenvyObserver {

    public VideoOSNativeView(Context context) {
        super(context);
    }

    @VenvyAutoRun
    protected void init() {
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_MEDIA_POSITION_CHANGED, this);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_SCREEN_CHANGED, this);
        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_MEDIA_POSITION_CHANGED, this);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_SCREEN_CHANGED, this);
        super.onDetachedFromWindow();
    }

    protected abstract void initView();

    protected abstract void screenChanged(ScreenStatus status);

    protected abstract void updatePosition(long position);

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_MEDIA_POSITION_CHANGED.equals(tag)) {
            if (bundle != null) {
                long position = bundle.getLong(VideoPositionHelper.KEY_TIME);
                updatePosition(position);
            }
        } else if (VenvyObservableTarget.TAG_SCREEN_CHANGED.equals(tag)) {
            if (bundle != null) {
                Object object = bundle.getSerializable("screen_changed");
                if (object != null && object instanceof ScreenStatus) {
                    ScreenStatus status = (ScreenStatus) object;
                    screenChanged(status);
                }
            }
        }
    }
}
