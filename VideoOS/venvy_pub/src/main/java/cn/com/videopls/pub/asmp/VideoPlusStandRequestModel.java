package cn.com.videopls.pub.asmp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.com.venvy.Platform;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.videopls.pub.VideoPlusBaseModel;

/**
 * Create by bolo on 2019-07-16
 */
public class VideoPlusStandRequestModel extends VideoPlusBaseModel {

    public VideoPlusStandRequestModel(@NonNull Platform platform) {
        super(platform);
    }

    @Override
    public Request createRequest() {
        return null;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {

            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {

            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }
}
