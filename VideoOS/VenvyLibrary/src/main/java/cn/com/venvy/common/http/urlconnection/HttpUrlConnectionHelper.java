package cn.com.venvy.common.http.urlconnection;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.http.urlconnection.tool.HttpHeaderParser;
import cn.com.venvy.common.http.urlconnection.tool.PoolingByteArrayOutputStream;
import cn.com.venvy.common.utils.VenvyIOUtils;

/**
 * Created by mac on 18/3/23.
 */

public class HttpUrlConnectionHelper implements IRequestConnect {

    public final SSLSocketFactory mSslSocketFactory;
    private final int DEFAULT_CONNECT_TIME_OUT = 20 * 1000;//s
    public int mConnectTimeOut = DEFAULT_CONNECT_TIME_OUT;

    private final int DEFAULT_READ_TIME_OUT = 20 * 1000;//s
    public int mReadTimeOut = DEFAULT_READ_TIME_OUT;


    public final Dispatcher dispatcher;

    public HttpUrlConnectionHelper() {
        this(null);
    }

    public HttpUrlConnectionHelper(SSLSocketFactory sslSocketFactory) {
        mSslSocketFactory = sslSocketFactory;
        dispatcher = new Dispatcher();
    }

    public void setConnectTimeOut(int connectTimeOut) {
        if (connectTimeOut > 0) {
            mConnectTimeOut = connectTimeOut;
        }
    }

    public void setReadTimeOut(int readTimeOut) {
        if (readTimeOut > 0) {
            mReadTimeOut = readTimeOut;
        }
    }


    @Override
    public void connect(Request request, IRequestHandler handler) {
        dispatcher.executeAsync(new AsyncCall(request, this, handler));
    }

    @Override
    public IResponse syncConnect(Request request) {
        SyncCall syncCall = new SyncCall(request, this);
        dispatcher.executeSync(syncCall);
        IResponse response = null;
        try {
            response = syncCall.executeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dispatcher.remove(syncCall);
        }

        return response;
    }

    @Override
    public boolean abort(int requestId) {
        dispatcher.removeAsyncCallByID(requestId);
        dispatcher.removeSyncCallByID(requestId);
        return true;
    }

    @Override
    public boolean abortAll() {
        dispatcher.cancelAll();
        return true;
    }


    public static class RealResponse implements IResponse {
        private final int mStatusCode;
        private final Map<String, List<String>> mHeaders;
        private final int mContentLength;
        private final InputStream mInputStream;
        private String mParsed;

        public RealResponse(int statusCode, Map<String, List<String>> headers,
                            int contentLength, InputStream content) {
            mStatusCode = statusCode;
            mHeaders = headers;
            mContentLength = contentLength;
            mInputStream = content;
        }


        @Override
        public Map<String, List<String>> getResponseHeaders() {
            return mHeaders;
        }

        @Override
        public String getResult() {
            if (mParsed != null) {
                return mParsed;
            }
            byte[] data = inputStreamToBytes();
            try {
                mParsed = new String(data, HttpHeaderParser.parseCharset(HttpHeaderParser.convertHeaders(mHeaders)));
            } catch (UnsupportedEncodingException e) {
                mParsed = new String(data);
            }

            return mParsed;
        }

        @Override
        public InputStream getByteStream() {
            return mInputStream;
        }

        @Override
        public long getContentLength() {
            return mContentLength;
        }

        @Override
        public boolean isSuccess() {
            return mStatusCode >= 200 && mStatusCode < 300;
        }

        @Override
        public int getHttpCode() {
            return mStatusCode;
        }

        private byte[] inputStreamToBytes() {
            PoolingByteArrayOutputStream bytes =
                    new PoolingByteArrayOutputStream((int) getContentLength());
            byte[] buffer = null;
            try {
                if (mInputStream == null) {
                    return new byte[0];
                }
                buffer = bytes.getBuf(1024);
                int count;
                while ((count = mInputStream.read(buffer)) != -1) {
                    bytes.write(buffer, 0, count);
                }
                return bytes.toByteArray();
            } catch (Exception e) {
                return new byte[0];
            } finally {
                VenvyIOUtils.close(mInputStream);
                bytes.returnBuf(buffer);
                VenvyIOUtils.close(bytes);
            }
        }

    }

}
