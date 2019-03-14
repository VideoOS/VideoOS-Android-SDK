package cn.com.venvy.common.interf;

import java.util.Set;

import cn.com.venvy.common.bean.SocketConnectItem;
import cn.com.venvy.common.bean.SocketUserInfo;

/**
 * Created by yanjiangbo on 2018/4/26.
 */

public interface ISocketConnect {

    void startConnect(SocketUserInfo info, Set<SocketConnectItem> socketConnectItems);

    void stopConnect(Set<SocketConnectItem> socketConnectItems);

    void destroyConnect();
}
