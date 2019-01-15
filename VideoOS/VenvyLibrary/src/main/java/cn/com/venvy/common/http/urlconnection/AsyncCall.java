package cn.com.venvy.common.http.urlconnection;


import java.io.IOException;

import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;

/**
 * 异步请求
 * Created by mac on 18/3/23.
 */

final class AsyncCall implements Runnable {
    private final IRequestHandler responseCallback;
    final SyncCall mSyncCall;
    private HttpUrlConnectionHelper mHttpHelper;

    AsyncCall(Request originalRequest, HttpUrlConnectionHelper httpHelper, IRequestHandler responseCallback) {
        mSyncCall = new SyncCall(originalRequest, httpHelper);
        mHttpHelper = httpHelper;
        this.responseCallback = responseCallback;
    }

    int getRequestId() {
        return mSyncCall.getRequestId();
    }

    public void cancel() {
        mSyncCall.cancel();
    }

    @Override
    public void run() {
        try {
            if (responseCallback != null) {
                responseCallback.startRequest(mSyncCall.getRealRequest());
            }

            if (mSyncCall.isCancel) {
                if (responseCallback != null) {
                    responseCallback.requestFinish(mSyncCall.getRealRequest(), null);
                }
                return;
            }

            IResponse realResponse = mSyncCall.executeRequest();
            if (responseCallback != null) {
                responseCallback.requestFinish(mSyncCall.getRealRequest(), realResponse);
            }
        } catch (IOException e) {
            if (responseCallback != null) {
                responseCallback.requestError(mSyncCall.getRealRequest(), e);
            }
        } finally {
            mHttpHelper.dispatcher.remove(this);
        }
    }

}
