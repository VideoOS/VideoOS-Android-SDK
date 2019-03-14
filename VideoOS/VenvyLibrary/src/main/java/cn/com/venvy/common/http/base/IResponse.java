package cn.com.venvy.common.http.base;

import android.support.annotation.Nullable;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by yanjiangbo on 2017/4/27.
 */

public interface IResponse {
    @Nullable
    Map<String, List<String>> getResponseHeaders();

    @Nullable
    String getResult();

    @Nullable
    InputStream getByteStream();

    long getContentLength();

    boolean isSuccess();

    int getHttpCode();
}
