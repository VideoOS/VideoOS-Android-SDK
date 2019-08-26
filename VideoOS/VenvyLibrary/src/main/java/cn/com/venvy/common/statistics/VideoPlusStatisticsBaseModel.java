package cn.com.venvy.common.statistics;

import android.support.annotation.NonNull;

import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpStatusPlugin;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by videopls on 2019/8/24.
 */

public abstract class VideoPlusStatisticsBaseModel {
    protected final static String TAG = VideoPlusStatisticsBaseModel.class.getName();

    private Platform mPlatform;
    private BaseRequestConnect mRequestConnect;
    private static HttpStatusPlugin sPlugin = new HttpStatusPlugin();
    private Request mCurrentRequest;


    public VideoPlusStatisticsBaseModel(@NonNull Platform platform) {
        BaseRequestConnect connect = RequestFactory.initConnect(platform);
        mPlatform = platform;
        mRequestConnect = connect;
        if (needCheckResponseValid()) {
            mRequestConnect.setHttpResponsePlugin(sPlugin);
        }
    }

    public VideoPlusStatisticsBaseModel(@NonNull Platform platform, @NonNull BaseRequestConnect requestConnect) {
        mPlatform = platform;
        mRequestConnect = requestConnect;
        if (needCheckResponseValid()) {
            mRequestConnect.setHttpResponsePlugin(sPlugin);
        }
    }

    public abstract Request createRequest();

    public abstract IRequestHandler createRequestHandler();

    public boolean needCheckResponseValid() {
        return true;
    }

    public void startRequest() {
        if (mRequestConnect == null) {
            VenvyLog.e(TAG, "connect error, connect can't be null");
            return;
        }
        Request request = createRequest();
        if (request == null) {
            return;
        }
        mRequestConnect.connect(request, createRequestHandler());
        mCurrentRequest = request;
    }

    public void destroy() {
        if (mRequestConnect != null && mCurrentRequest != null) {
            mRequestConnect.abort(mCurrentRequest);
        }
    }

    public Platform getPlatform() {
        return mPlatform;
    }

    public BaseRequestConnect getRequestConnect() {
        return mRequestConnect;
    }
}
