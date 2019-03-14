package cn.com.venvy.common.http.base;

import android.support.annotation.Nullable;

/**
 * Created by yanjiangbo on 2017/4/26.
 */

public interface IRequestHandler {

    void requestFinish(Request request, IResponse response);

    void requestError(Request request, @Nullable Exception e);

    void startRequest(Request request);

    /**
     * 暂时还不支持，先预埋
     */
    void requestProgress(Request request, int progress);
}
