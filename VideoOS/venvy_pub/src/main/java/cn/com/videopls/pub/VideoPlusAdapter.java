package cn.com.videopls.pub;

import android.os.Bundle;

import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.image.IImageLoader;
import cn.com.venvy.common.image.IImageSize;
import cn.com.venvy.common.image.IImageView;
import cn.com.venvy.common.interf.IACRCloud;
import cn.com.venvy.common.interf.IMediaControlListener;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.interf.ISvgaImageView;
import cn.com.venvy.common.interf.IWidgetClickListener;
import cn.com.venvy.common.interf.IWidgetCloseListener;
import cn.com.venvy.common.interf.IWidgetPrepareShowListener;
import cn.com.venvy.common.interf.IWidgetRotationListener;
import cn.com.venvy.common.interf.IWidgetShowListener;
import cn.com.venvy.common.interf.MediaStatus;
import cn.com.venvy.common.interf.OnTagKeyListener;
import cn.com.venvy.common.interf.ScreenStatus;
import cn.com.venvy.common.interf.WedgeListener;
import cn.com.venvy.common.observer.ActivityStatusObserver;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.webview.IVenvyWebView;


/**
 * Created by yanjiangbo on 2017/5/17.
 */

public abstract class VideoPlusAdapter {

    public abstract Provider createProvider();

    public Class<? extends IImageLoader> buildImageLoader() {
        return null;
    }

    public Class<? extends IImageSize> buildImageSize() {
        return null;
    }

    public Class<? extends IRequestConnect> buildConnectProvider() {
        return null;
    }

    public Class<? extends IVenvyWebView> buildWebView() {
        return null;
    }

    public Class<? extends ISvgaImageView> buildSvgaImageView() {
        return null;
    }

    public Class<? extends IImageView> buildImageView() {
        return null;
    }

    public Class<? extends ISocketConnect> buildSocketConnect() {
        return null;
    }

    public Class<? extends IACRCloud> buildACRCloud() {
        return null;
    }

    public OnTagKeyListener buildOttKeyListener() {
        return null;
    }

    public IMediaControlListener buildMediaController() {
        return null;
    }

    public WedgeListener buildWedgeListener() {
        return null;
    }

    public IPlatformLoginInterface buildLoginInterface() {
        return null;
    }

    public IWidgetPrepareShowListener buildWidgetPrepareShowListener() {
        return null;
    }

    public IWidgetShowListener buildWidgetShowListener() {
        return null;
    }

    public IWidgetCloseListener buildWidgetCloseListener() {
        return null;
    }

    public IWidgetClickListener buildWidgetClickListener() {
        return null;
    }

    public abstract IWidgetRotationListener buildWidgetRotationListener();

    public void updateProvider(Provider provider) {
        notifyProviderSetChanged(provider);
    }

    public void onCreate() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_CREATE);
    }

    public void onResume() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_RESUME);
    }

    public void onRestart() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_RESTART);
    }

    public void onPause() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_PAUSE);
    }

    public void onStart() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_START);
    }

    public void onStop() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_STOP);
    }

    public void onDestroy() {
        notifyActivityStatusChanged(ActivityStatusObserver.STATUS_DESTROY);
    }


    private void notifyProviderSetChanged(Provider provider) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("provider", provider);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_DATA_SET_CHANGED, bundle);
    }

    @Deprecated
    public void notifyLiveVerticalScreen(int verticalScreenType) {
        ScreenStatus status = ScreenStatus.getStatusById(verticalScreenType);
        notifyVideoScreenChanged(status);
    }

    private void notifyActivityStatusChanged(int status) {
        Bundle bundle = new Bundle();
        bundle.putInt("activity_status", status);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_ACTIVITY_CHANGED, bundle);
    }

    public void notifyVideoScreenChanged(ScreenStatus status) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VenvyObservableTarget.Constant.CONSTANT_SCREEN_CHANGE, status);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SCREEN_CHANGED, bundle);
    }

    public void notifyMediaStatusChanged(MediaStatus status) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("media_changed", status);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_MEDIA_CHANGED, bundle);
    }

    public void notifyMessageArrived(String topic, String message) {
        String target = VenvyObservableTarget.TAG_ARRIVED_DATA_MESSAGE;
        Bundle bundle = new Bundle();
        bundle.putString("data", message);
        bundle.putString("topic", topic);
        ObservableManager.getDefaultObserable().sendToTarget(target, bundle);
    }

}
