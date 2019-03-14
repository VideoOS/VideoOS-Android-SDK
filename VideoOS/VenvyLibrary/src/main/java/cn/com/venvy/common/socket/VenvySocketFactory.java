package cn.com.venvy.common.socket;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2018/4/26.
 */

public class VenvySocketFactory {

    public static ISocketConnect getSocketConnect() {
        try {
            Class<? extends ISocketConnect> socketConnect = VenvyRegisterLibsManager.getSocketConnect();
            if (socketConnect == null) {
                return null;
            }
            return socketConnect.newInstance();
        } catch (Exception e) {
            VenvyLog.e(VenvySocketFactory.class.getName(), e);
        }
        return null;
    }
}
