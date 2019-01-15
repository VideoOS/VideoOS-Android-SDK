package cn.com.venvy.common.http.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.com.venvy.App;
import cn.com.venvy.Config;
import cn.com.venvy.IgnoreHttps;
import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.bean.PlatformUserInfo;
import cn.com.venvy.common.exception.HttpException;
import cn.com.venvy.common.interf.IPlatformLoginInterface;
import cn.com.venvy.common.interf.Method;
import cn.com.venvy.common.priority.HttpPriorityTask;
import cn.com.venvy.common.priority.PriorityTaskDispatch;
import cn.com.venvy.common.priority.base.PriorityTask;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyDeviceUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;


/**
 * Created by yanjiangbo on 2017/4/27.
 */

public class BaseRequestConnect {

    private static final String AES_KEY = "8lgK5fr5yatOfHio";
    private static final String AES_IV = "lx7eZhVoBEnKXELF";
    private static final String SERVER_KEY = "data";
    private static final String TAG = "BaseRequestConnect";

    private static final String SDK_VERSION = "sdk-version";
    private static final String USER_AGENT = "user-agent";
    private static final String UD_ID = "udid";
    private static final String IDENTITY = "identity";
    private static final String APP_KEY = "appkey";
    private static final String IP = "ip";
    private static final String VIDEO_ID = "video_id";
    private static final String NETWORK = "network";
    private static final String PLATFORM_ID = "3rd-platform-id";
    private static final String LANGUAGE = "lang";
    private static final String ENCODING = "Accept-Encoding";
    private static final String PLATFORM_TOKEN = "user_token";

    private HashMap<String, String> mDefaultHeaders;
    private Platform mPlatform;
    private PriorityTaskDispatch mPriorityDispatch;
    private IRequestConnect mRequestConnect;
    private Method<? extends HttpExtraPlugin> mHttpResponsePlugin;
    private RequestConnectStatus connectStatus = RequestConnectStatus.NULL;

    public BaseRequestConnect(@NonNull Platform platform, IRequestConnect iRequestConnect) {
        init(platform);
        mRequestConnect = iRequestConnect;
    }

    private void init(@NonNull Platform platform) {
        try {
            this.mPlatform = platform;
            mDefaultHeaders = buildDefaultHeaders(platform);
            mPriorityDispatch = new PriorityTaskDispatch();

        } catch (Exception e) {
            VenvyLog.e(TAG, e);
        }
    }


    /**
     * 设置response 判定结果plugin.
     */
    public void setHttpResponsePlugin(Method<? extends HttpExtraPlugin> plugin) {
        this.mHttpResponsePlugin = plugin;
    }

    /**
     * 异步请求方法
     */
    public void connect(Request request, final IRequestHandler handler) {

        try {
            checkRequestValid(request, mRequestConnect, mPlatform);
            request = deployRequestByHeaders(mPlatform, request);
            if (request.mRequestType == RequestType.GET || request.mRequestType == RequestType.DELETE) {
                request = buildUrlWithParams(request);
            } else {
                request = buildRequestParams(request);
            }
            VenvyLog.i(TAG, "start Request, Url = " + request.url);
            mRequestConnect.connect(request, new BaseRequestHandler(handler, mHttpResponsePlugin != null ? mHttpResponsePlugin.call() : null));
        } catch (Exception e) {
            if (handler != null) {
                handler.requestError(request, e);
            }
        }
    }

    /**
     * 同步请求方法
     */
    public IResponse syncConnect(Request request) throws Exception {
        checkRequestValid(request, mRequestConnect, mPlatform);
        request = deployRequestByHeaders(mPlatform, request);
        if (request.mRequestType == RequestType.GET || request.mRequestType == RequestType.DELETE) {
            request = buildUrlWithParams(request);
        } else {
            request = buildRequestParams(request);
        }
        VenvyLog.i(TAG, "start Request, Url = " + request.url);
        return mRequestConnect.syncConnect(request);
    }


    public boolean abort(int requestId) {
        List<PriorityTask> runningTasks = mPriorityDispatch.runningPriorityTasks();
        for (PriorityTask priorityTask : runningTasks) {
            if (priorityTask instanceof HttpPriorityTask) {
                HttpPriorityTask httpPriorityTask = (HttpPriorityTask) priorityTask;
                Request taskRequest = httpPriorityTask.getRequest();
                if (taskRequest.mRequestId == requestId) {
                    httpPriorityTask.cancelTask();
                    break;
                }
            }
        }
        return mRequestConnect != null && mRequestConnect.abort(requestId);
    }

    /**
     * cancel单个请求
     */
    public boolean abort(Request request) {
        if (mRequestConnect == null) {
            VenvyLog.w("connect can't be null, please check");
            return false;
        }
        if (request == null) {
            VenvyLog.w("request can't be null, please check");
            return false;
        }
        return abort(request.mRequestId);
    }

    /**
     * cancel一个请求队列里面的所有请求
     */
    public boolean abortAll() {
        if (mRequestConnect == null) {
            return false;
        }
        if (mPriorityDispatch != null) {
            mPriorityDispatch.cancelAll();
        }
        return mRequestConnect.abortAll();
    }

    public RequestConnectStatus getConnectStatus() {
        return connectStatus;
    }


    private class BaseRequestHandler implements IRequestHandler {

        private IRequestHandler mHandler;
        private HttpExtraPlugin mHttpExtraPlugin;

        private BaseRequestHandler(IRequestHandler handler, HttpExtraPlugin httpExtraPlugin) {
            if (handler != null) {
                this.mHandler = handler;
            }
            if (httpExtraPlugin != null) {
                this.mHttpExtraPlugin = httpExtraPlugin;
            }
        }

        @Override
        public void startRequest(Request request) {
            if (mHandler != null) {
                mHandler.startRequest(request);
            }
            connectStatus = RequestConnectStatus.ACTIVE;
        }

        @Override
        public void requestFinish(Request request, IResponse response) {
            try {
                if (!response.isSuccess()) {
                    requestError(request, new HttpException("http error, error code is " + response.getHttpCode()));
                    return;
                }
                String responseString = response.getResult();
                String result;
                if (!TextUtils.isEmpty(responseString) && request.isEncrypted) {
                    result = VenvyAesUtil.decrypt(responseString, AES_KEY, AES_IV);
                } else {
                    result = responseString;
                }
                if (TextUtils.isEmpty(responseString)) {
                    requestError(request, new HttpException("http error, response is null."));
                    return;
                }
                if (mHttpExtraPlugin != null) {
                    mHttpExtraPlugin.setResponseStringResult(result);
                    if (!mHttpExtraPlugin.isSuccess()) {
                        requestError(request, new HttpException("http error, response is invaild data, and response result is " + response.getResult()));
                        return;
                    }
                }
                if (mHandler != null) {
                    mHandler.requestFinish(request, response);
                }
                connectStatus = RequestConnectStatus.IDLE;
            } catch (Exception e) {
                requestError(request, e);
            }
        }

        @Override
        public void requestError(final Request request, @Nullable Exception e) {

            startReportErrorLog(request, e);
            try {
                if (request.needRetry && request.mRetryCount > 1) {
                    request.mRetryCount = request.mRetryCount - 1;
                    if (VenvyUIUtil.isOnUIThread()) {
                        VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                            @Override
                            public void run() {
                                connect(request, mHandler);
                            }
                        }, 3000 * (Request.RETRY_COUNT - request.mRetryCount));
                    } else {
                        Thread.sleep(3000 * (Request.RETRY_COUNT - request.mRetryCount));
                        connect(request, mHandler);
                    }
                    return;
                }
            } catch (Exception ex) {
                // 重试异常忽略掉，直接回调业务标识出错就好。
                VenvyLog.e(TAG, ex);
            }
            if (mHandler != null) {
                mHandler.requestError(request, e);
            }
            connectStatus = RequestConnectStatus.IDLE;
        }

        @Override
        public void requestProgress(Request request, int progress) {
            if (mHandler != null) {
                mHandler.requestProgress(request, progress);
            }
        }
    }

    private void checkRequestValid(Request request, IRequestConnect connect, Platform platform) throws Exception {
        if (connect == null) {
            VenvyLog.w("connect can't be null, please check");
            throw new HttpException("connect can't be null, please check");
        }
        if (platform == null) {
            VenvyLog.w("platform can't be null, please check");
            throw new HttpException("platform can't be null, please check");
        }
        if (request == null) {
            VenvyLog.w("request can't be null, please check");
            throw new HttpException("request can't be null, please check");
        }
        if (TextUtils.isEmpty(request.url) || TextUtils.isEmpty(request.url.trim())) {
            VenvyLog.w("request url can't be null, please check");
            throw new HttpException("request url can't be null, please check");
        }
        request.url = parseUrl(request.url);
        if (TextUtils.isEmpty(request.url)) {
            VenvyLog.w("request url is invaild, please check");
            throw new HttpException("request url is invaild, please check");
        }
        if (!VenvyDeviceUtil.isNetworkAvailable(App.getContext())) {
            VenvyLog.w("network is unvaild, please check");
            throw new HttpException("network is unvaild, please check");
        }
    }


    private Request deployRequestByHeaders(Platform platform, Request request) {

        if (VenvyUIUtil.isOnUIThread()) {
            //如果是主线程的调用，每次都处理下头部信息
            mDefaultHeaders = updateDefaultHeaders(platform, mDefaultHeaders);
        }
        if (mDefaultHeaders != null) {
            Map<String, String> headers = request.mHeaders;
            if (headers != null) {
                headers.putAll(mDefaultHeaders);
            } else {
                headers = mDefaultHeaders;
            }
            request.mHeaders = headers;
        }
        return request;
    }


    private Request buildRequestParams(@NonNull Request request) {
        Map<String, String> params = request.mParams;
        if (params == null || params.size() <= 0 || !request.isEncrypted) {
            return request;
        }
        StringBuilder paramsBuild = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                paramsBuild.append(entry.getKey());
                paramsBuild.append("=");
                paramsBuild.append(entry.getValue());
                paramsBuild.append("&");
            }
        }
        if (params.size() > 0) {
            paramsBuild.delete(paramsBuild.length() - 1, paramsBuild.length());
        }
        request.mParams.clear();
        request.mParams.put(SERVER_KEY, VenvyAesUtil.encrypt(AES_KEY, AES_IV, paramsBuild.toString()));
        request.mParams.put("isEncrypted", String.valueOf(true));
        return request;
    }


    private Request buildUrlWithParams(@NonNull Request request) {
        Map<String, String> params = request.mParams;
        if (params == null || params.size() <= 0) {
            return request;
        }
        String url = request.url;
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        }
        StringBuilder paramsBuild = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                paramsBuild.append(entry.getKey());
                paramsBuild.append("=");
                paramsBuild.append(entry.getValue());
                paramsBuild.append("&");
            }
        }
        if (params.size() > 0) {
            paramsBuild.delete(paramsBuild.length() - 1, paramsBuild.length());
        }
        if (request.isEncrypted) {
            sb.append(SERVER_KEY).append("=").append(VenvyAesUtil.encrypt(AES_KEY, AES_IV, paramsBuild.toString()));
            sb.append("&");
            sb.append("isEncrypted");
            sb.append("=");
            sb.append(String.valueOf(true));
        } else {
            sb.append(paramsBuild.toString());
        }
        request.url = sb.toString();
        request.mParams = null;
        return request;
    }


    private HashMap<String, String> buildDefaultHeaders(Platform platform) {
        HashMap<String, String> headers = new HashMap<>();
        PlatformInfo platformInfo = platform.getPlatformInfo();
        Context context = App.getContext();
        if (context != null) {
            String uuIdStr = "";
            IPlatformLoginInterface loginInterface = platform.getPlatformLoginInterface();
            if (loginInterface != null) {
                PlatformUserInfo platformUserInfo = loginInterface.getLoginUser();
                String userToken = platformUserInfo == null ? null : platformUserInfo.getUserToken();
                if (!TextUtils.isEmpty(userToken)) {
                    headers.put(PLATFORM_TOKEN, platformUserInfo.getUserToken());
                }
                if (!TextUtils.isEmpty(platformUserInfo != null ? platformUserInfo.getCustomerDeviceId() : null)) {
                    uuIdStr = platformUserInfo.getCustomerDeviceId();
                }
            }
            if (TextUtils.isEmpty(uuIdStr)) {
                if (platform.getPlatformInfo() != null && TextUtils.isEmpty(platform.getPlatformInfo().getIdentity())) {
                    uuIdStr = platform.getPlatformInfo().getIdentity();
                } else {
                    UUID uuid = VenvyDeviceUtil.getDeviceUuid(context);
                    if (uuid != null) {
                        uuIdStr = uuid.toString();
                    }
                }
            }
            headers.put(UD_ID, VenvyDeviceUtil.getAndroidID(context));
            headers.put(IDENTITY, uuIdStr != null ? uuIdStr : "");
            String ip = VenvyDeviceUtil.getLocalIPAddress();
            if (!TextUtils.isEmpty(ip)) {
                headers.put(IP, ip);
            }
            headers.put(ENCODING, "identity");
            headers.put(NETWORK, VenvyDeviceUtil.getNetWorkName(context));
            headers.put(LANGUAGE, VenvyDeviceUtil.getLanguage(context));
            headers.put(USER_AGENT, VenvyDeviceUtil.getUserAgent(context));
            headers.put(SDK_VERSION, Config.SDK_VERSION != null ? Config.SDK_VERSION : "");

        }
        if (platformInfo != null) {
            String platformId = platformInfo.getThirdPlatformId();
            if (!TextUtils.isEmpty(platformId)) {
                headers.put(PLATFORM_ID, platformId);
            }
            String appKey = platformInfo.getThirdPlatformId();
            if (!TextUtils.isEmpty(appKey)) {
                headers.put(APP_KEY, appKey);
            }
            String videoID = platformInfo.getVideoId();
            if (!TextUtils.isEmpty(videoID)) {
                headers.put(VIDEO_ID, videoID);
            }
        }
        return headers;
    }

    private HashMap<String, String> updateDefaultHeaders(Platform platform, HashMap<String, String> defaultHeaders) {
        if (defaultHeaders == null) {
            return buildDefaultHeaders(platform);
        }
        Context context = App.getContext();
        defaultHeaders.put(NETWORK, VenvyDeviceUtil.getNetWorkName(context));
        defaultHeaders.put(IP, VenvyDeviceUtil.getLocalIPAddress());
        IPlatformLoginInterface loginInterface = platform.getPlatformLoginInterface();
        if (loginInterface != null) {
            PlatformUserInfo platformUserInfo = loginInterface.getLoginUser();
            String userToken = platformUserInfo == null ? null : platformUserInfo.getUserToken();
            if (!TextUtils.isEmpty(userToken)) {
                defaultHeaders.put(PLATFORM_TOKEN, platformUserInfo.getUserToken());
            }
            if (!TextUtils.isEmpty(platformUserInfo != null ? platformUserInfo.getCustomerDeviceId() : null)) {
                defaultHeaders.put(UD_ID, platformUserInfo.getCustomerDeviceId());
            }
        }
        return defaultHeaders;
    }

    public static String parseUrl(@NonNull String url) {
        try {
            url = IgnoreHttps.ignore(url);
            URL urlParse = new URL(url);
            String host = urlParse.getHost();
            if (TextUtils.isEmpty(host)) {
                return null;
            }
            if (ParseUrl.urlMap.containsKey(host)) {
                return url.replaceFirst(urlParse.getHost(), ParseUrl.urlMap.get(host).getCurrentUrl());
            } else {
                VenvyLog.w("Request url not contain test url, please check!");
            }
        } catch (Exception e) {
            VenvyLog.e(BaseRequestConnect.class.getName(), e);
        }
        return url;
    }

    private void startReportErrorLog(Request request, Exception e) {
        boolean needReport = request.needReport;
        if (!needReport) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (request.mRetryCount > 1) {
            builder.append("[http request failed by retry], retryNum is ").append((Request.RETRY_COUNT - request.mRetryCount)).append(", url == ").append(request.url);
        } else {
            builder.append("[http request failed], url == ").append(request.url);
        }
        builder.append("\\n ");
        Map<String, String> params = request.mParams;
        if (params != null && params.size() > 0) {
            builder.append("params is ");
            for (Map.Entry<String, String> entry : request.mParams.entrySet()) {
                if (entry.getValue() != null) {
                    builder.append(entry.getKey());
                    builder.append("=");
                    builder.append(entry.getValue());
                    builder.append("&");
                }
            }
            builder.append("\\n ");
        }
        if (e != null) {
            if (!TextUtils.isEmpty(e.toString())) {
                builder.append("Cause by: ").append(e.toString());
                builder.append("\\n ");
            }
            StackTraceElement[] element = e.getStackTrace();
            if (element != null) {
                for (StackTraceElement i : element) {
                    builder.append(i.toString());
                    builder.append("\\n ");
                }
            }
        }
    }

}