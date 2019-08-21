package both.video.venvy.com.appdemo.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import both.video.venvy.com.appdemo.UrlContent;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/8/6.
 */
public class VideoConfigModel extends IConfigModel {
    private VideoConfigCallback configCallback;

    public VideoConfigModel(VideoConfigCallback configCallback) {
        super();
        this.configCallback = configCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.get(UrlContent.URL_VIDEO_INFO);
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                VideoConfigModel.VideoConfigCallback configCallback = getConfigCallback();
                if (!response.isSuccess()) {
                    if (configCallback != null) {
                        configCallback.requestError(new Exception("get video config error"));
                    }
                }
                if (configCallback == null) {
                    return;
                }
                String result = response.getResult();
                if (!TextUtils.isEmpty(result)) {
                    configCallback.requestComplete(result);
                } else {
                    configCallback.requestError(new NullPointerException("get video config NullPointerException"));
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(AppConfigModel.class.getName(), "获取Video配置信息失败 " + (e != null ? e.getMessage() : ""));
                VideoConfigModel.VideoConfigCallback configCallback = getConfigCallback();
                if (configCallback != null) {
                    configCallback.requestError(e);
                }
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int i) {

            }
        };
    }

    @Override
    public void destroy() {
        super.destroy();
        configCallback = null;
    }


    public VideoConfigCallback getConfigCallback() {
        return configCallback;
    }

    public interface VideoConfigCallback {
        void requestComplete(String result);

        void requestError(Throwable t);
    }
}
