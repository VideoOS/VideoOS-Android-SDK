package cn.com.venvy.common.http.base;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.common.priority.Priority;
import cn.com.venvy.common.utils.VenvyIDHelper;

/**
 * Created by yanjiangbo on 2017/4/26.
 */

public abstract class Request {


    public static final int RETRY_COUNT = 1;
    private Priority mPriority = Priority.NORMAL;
    public int mRequestId;
    public String url;
    public Map<String, String> mParams;
    public Map<String, String> mHeaders;
    public RequestType mRequestType;
    public RequestCacheType mCacheType;
    public boolean needCache = false;
    public boolean needRetry = true;
    public boolean needReport = false;
    public int mRetryCount = 0;
    public boolean isEncrypted;
    public String cacheKey = "";

    public Request(String url, RequestType method, InputStream input,
                   RequestCacheType defaultCacheType,
                   Map<String, String> headers, Map<String, String> params) {
        this.url = url;
        this.mParams = params;
        this.mCacheType = defaultCacheType;
        this.mRequestType = method;
        this.mHeaders = headers;
        this.mRequestId = VenvyIDHelper.getInstance().getId();
    }

    public Request() {
    }

    public void setHeaders(HashMap<String, String> headers) {
        mHeaders = headers;
    }

    public void setParams(HashMap<String, String> params) {
        mParams = params;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestCacheType(RequestCacheType type) {
        mCacheType = type;
    }


    public void needRetry(boolean needRetry) {
        this.needRetry = needRetry;
        this.mRetryCount = RETRY_COUNT;
    }

    public void needReport(boolean needReport) {
        this.needReport = needReport;
    }

    public void needCache(boolean needCache) {
        this.needCache = needCache;
        if (needCache) {
            cacheKey = System.currentTimeMillis() + "";
        }
    }

    public void needEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    public Priority getPriority() {
        return mPriority;
    }
}
