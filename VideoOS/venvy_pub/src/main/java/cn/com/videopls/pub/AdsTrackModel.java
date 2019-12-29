package cn.com.videopls.pub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/12/29.
 */
public class AdsTrackModel extends VideoPlusBaseModel {

    private String api;

    public AdsTrackModel(@NonNull Platform platform, String api) {
        super(platform);
        this.api = api;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.get(api);
    }
    @Override
    public boolean needCheckResponseValid() {
        return false;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {

            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (response.isSuccess()) {
                        VenvyLog.d("track success ! ");
                    } else {
                        VenvyLog.e("response was failed ! ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
