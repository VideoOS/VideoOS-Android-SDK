package cn.com.venvy.common.http;

import java.lang.reflect.Constructor;

import cn.com.venvy.Platform;
import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestConnect;
import cn.com.venvy.common.http.urlconnection.HttpUrlConnectionHelper;

/**
 * Created by yanjiangbo on 2017/4/26.
 */

public class RequestFactory {

    public static BaseRequestConnect initConnect(Platform platform) {
        return new BaseRequestConnect(platform, createConnect());
    }

    private static IRequestConnect createConnect() {

        Class<? extends IRequestConnect> cls = VenvyRegisterLibsManager.getConnectLib();
        if (cls == null) {
            return new HttpUrlConnectionHelper();
        }

        IRequestConnect requestConnect = null;
        try {
            Constructor constructor = cls.getDeclaredConstructor(/*Context.class*/);
            requestConnect = (IRequestConnect) constructor.newInstance(new Object[]{});
        } catch (Exception e) {

        }
        if (requestConnect == null) {
            requestConnect = new HttpUrlConnectionHelper();
        }
        return requestConnect;
    }
}
