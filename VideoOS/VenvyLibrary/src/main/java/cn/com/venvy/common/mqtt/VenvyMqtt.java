package cn.com.venvy.common.mqtt;

import java.util.Set;

import cn.com.venvy.common.bean.SocketConnectItem;
import cn.com.venvy.common.bean.SocketUserInfo;
import cn.com.venvy.common.interf.ISocketConnect;

/**
 * Created by yanjiangbo on 2018/4/26.
 */

public class VenvyMqtt implements ISocketConnect {

    @Override
    public void startConnect(SocketUserInfo info, Set<SocketConnectItem> socketConnectItems) {
        VenvyMqttClientHelper.getInstance(info).subscribeAndConnect(socketConnectItems);
    }

    @Override
    public void stopConnect(Set<SocketConnectItem> socketConnectItems) {
        VenvyMqttClientHelper.getInstance(null).removeTopics(socketConnectItems);
    }

    @Override
    public void destroyConnect() {
        VenvyMqttClientHelper.getInstance(null).destroy();
    }
}
