package cn.com.venvy.common.media.view;

import android.content.Context;

import cn.com.venvy.common.media.HttpProxyCacheServer;

/**
 * Created by yanjiangbo on 2018/5/3.
 */

public class HttpProxyCacheServerFactory {

    private HttpProxyCacheServer proxyCacheServer;

    private static HttpProxyCacheServerFactory serverFactory;

    public static synchronized HttpProxyCacheServerFactory getInstance() {
        if (serverFactory == null) {
            serverFactory = new HttpProxyCacheServerFactory();
        }
        return serverFactory;
    }

    public HttpProxyCacheServer getProxy(Context context) {
        return proxyCacheServer != null ? proxyCacheServer : (proxyCacheServer = newProxy(context));
    }

    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 1024)// 设置缓存最大限制
                .maxCacheFilesCount(10)
                .build();
    }
}
