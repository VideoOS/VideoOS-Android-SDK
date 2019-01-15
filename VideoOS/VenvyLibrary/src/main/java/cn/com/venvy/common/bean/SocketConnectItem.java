package cn.com.venvy.common.bean;

/**
 * Created by yanjiangbo on 2018/4/26.
 */

public class SocketConnectItem {

    public String topic;
    public int qos;
    public Object target;

    public SocketConnectItem(String topic, int qos, Object target) {
        this.topic = topic;
        this.qos = qos;
        this.target = target;
    }
}
