package cn.com.venvy.common.http;

import java.io.InputStream;
import java.util.Map;

import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.http.base.RequestCacheType;
import cn.com.venvy.common.http.base.RequestType;

/**
 * Created by yanjiangbo on 2017/4/26.
 */

public class HttpRequest extends Request {

    private HttpRequest(String url, RequestType method, InputStream input,
                        RequestCacheType defaultCacheType,
                        Map<String, String> headers, Map<String, String> params) {
        super(url, method, input, defaultCacheType, headers, params);
    }

    public static HttpRequest get(String url, Map<String, String> headers, Map<String, String> params,
                                  RequestCacheType defaultCacheType) {

        return new HttpRequest(url, RequestType.GET, null, defaultCacheType, headers, params);
    }

    public static HttpRequest get(String url, Map<String, String> params, RequestCacheType defaultCacheType) {
        return get(url, null, params, defaultCacheType);
    }

    public static HttpRequest get(String url, Map<String, String> headers, Map<String, String> params) {
        return get(url, headers, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest get(String url, Map<String, String> params) {
        return get(url, null, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest get(String url, RequestCacheType defaultCacheType) {
        return get(url, null, null, defaultCacheType);
    }

    public static HttpRequest get(String url) {
        return get(url, RequestCacheType.DEFAULT);
    }

    public static HttpRequest post(String url, Map<String, String> headers, Map<String, String> params,
                                   RequestCacheType defaultCacheType) {
        return new HttpRequest(url, RequestType.POST, null, defaultCacheType, headers, params);
    }

    public static HttpRequest post(String url, Map<String, String> params, RequestCacheType defaultCacheType) {
        return post(url, null, params, defaultCacheType);
    }

    public static HttpRequest post(String url, Map<String, String> headers, Map<String, String> params) {
        return post(url, headers, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest post(String url, Map<String, String> params) {
        return post(url, null, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest put(String url, Map<String, String> headers, Map<String, String> params, RequestCacheType defaultCacheType) {
        return new HttpRequest(url, RequestType.PUT, null, defaultCacheType, headers, params);
    }

    public static HttpRequest put(String url, Map<String, String> params, RequestCacheType defaultCacheType) {
        return put(url, null, params, defaultCacheType);
    }

    public static HttpRequest put(String url, Map<String, String> headers, Map<String, String> params) {
        return put(url, headers, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest put(String url, Map<String, String> params) {
        return put(url, null, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest delete(String url, Map<String, String> headers, Map<String, String> params,
                                     RequestCacheType defaultCacheType) {
        return new HttpRequest(url, RequestType.DELETE, null, defaultCacheType, headers, params);
    }

    public static HttpRequest delete(String url, Map<String, String> params, RequestCacheType defaultCacheType) {
        return delete(url, null, params, defaultCacheType);
    }

    public static HttpRequest delete(String url, Map<String, String> headers, Map<String, String> params) {
        return delete(url, headers, params, RequestCacheType.DEFAULT);
    }

    public static HttpRequest delete(String url, Map<String, String> params) {
        return delete(url, null, params, RequestCacheType.DEFAULT);
    }

    public static String buildUrlWithParams(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }
        }
        if (params.size() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
