package cn.com.venvy.common.interf;

/**
 * Created by videojj_pls on 2019/7/12.
 * 视联网模式事件回调
 */

public interface IServiceCallback {
    void onCompleteForService();

    void onFailToCompleteForService(Throwable throwable);
}
