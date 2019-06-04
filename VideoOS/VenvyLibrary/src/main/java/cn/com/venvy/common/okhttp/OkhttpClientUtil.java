package cn.com.venvy.common.okhttp;

import android.content.Context;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.venvy.common.http.HttpSSLParams;
import cn.com.venvy.common.utils.VenvyIOUtils;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;

/**
 * Created by mac on 18/2/7.
 */

public class OkhttpClientUtil {
    public static final int TIME_OUT = 20;
    private static OkHttpClient client;

    public static OkHttpClient getClient(Context context) {
        if (client == null) {
            synchronized (OkhttpClientUtil.class) {
                if (client == null) {
                    HttpSSLParams.SSLParams sslParams = initSSLParams(context);

                    client = new OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                            .dispatcher(new Dispatcher(new ThreadPoolExecutor(0, 100, 60, TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false))))
                            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                            .build();
                }
            }
        }
        return client;
    }

    private static HttpSSLParams.SSLParams initSSLParams(Context context) {
        return HttpSSLParams.getSslSocketFactory(new InputStream[]{VenvyIOUtils.open(context, "videojj.com.cer")},
                null,
                null);
    }
}
