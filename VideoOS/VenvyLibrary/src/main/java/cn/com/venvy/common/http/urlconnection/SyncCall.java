package cn.com.venvy.common.http.urlconnection;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;

/**
 * 同步请求
 * Created by mac on 18/3/24.
 */

final class SyncCall {
    public boolean isCancel = false;
    private Request mRequest;
    private HttpUrlConnectionHelper mHttpHelper;

    public SyncCall(Request request, HttpUrlConnectionHelper httpUrlConnectionHelper) {
        mRequest = request;
        mHttpHelper = httpUrlConnectionHelper;
    }

    public Request getRealRequest() {
        return mRequest;
    }

    public int getRequestId() {
        return mRequest.mRequestId;
    }

    public void cancel() {
        isCancel = true;
    }

    protected IResponse executeRequest() throws IOException {
        Request request = getRealRequest();
        String url = request.url;

        URL parsedUrl = new URL(url);
        if (isCancel) {
            return null;
        }
        HttpURLConnection connection = openConnection(parsedUrl, request);
        if (request.mHeaders != null && !request.mHeaders.isEmpty()) {
            for (String headerName : request.mHeaders.keySet()) {
                connection.addRequestProperty(headerName, request.mHeaders.get(headerName));
            }
        }
        setConnectionParametersForRequest(connection, request);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        return new HttpUrlConnectionHelper.RealResponse(responseCode, connection.getHeaderFields(),
                connection.getContentLength(), inputStreamFromConnection(connection));
    }


    private static InputStream inputStreamFromConnection(HttpURLConnection connection) {
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        return inputStream;
    }


    protected HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());

        return connection;
    }

    private HttpURLConnection openConnection(URL url, Request request) throws IOException {
        HttpURLConnection connection = createConnection(url);

        connection.setConnectTimeout(mHttpHelper.mConnectTimeOut);
        connection.setReadTimeout(mHttpHelper.mReadTimeOut);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        if ("https".equals(url.getProtocol()) && mHttpHelper.mSslSocketFactory != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(mHttpHelper.mSslSocketFactory);
        }

        return connection;
    }

    static void setConnectionParametersForRequest(HttpURLConnection connection,
                                                  Request request) throws IOException {
        switch (request.mRequestType) {

            case GET:
                // Not necessary to set the request method because connection defaults to GET but
                // being explicit here.
                connection.setRequestMethod("GET");
                break;
            case DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request request)
            throws IOException {
        String body = getBodyJson(request);
        if (!TextUtils.isEmpty(body)) {
            addBody(connection, body);
        }
    }

    private static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public static byte[] getBody(Request request) {
        Map<String, String> params = request.mParams;
        if (params != null && params.size() > 0) {
            return encodeParameters(params, "UTF-8");
        }
        return null;
    }

    public static String getBodyJson(Request request) {
        Map<String, String> params = request.mParams;
        if (params != null && params.size() > 0) {
            return new JSONObject(params).toString();
        }
        return null;
    }

    private static void addBody(HttpURLConnection connection, String body)
            throws IOException {
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(body);
        out.close();
    }

}