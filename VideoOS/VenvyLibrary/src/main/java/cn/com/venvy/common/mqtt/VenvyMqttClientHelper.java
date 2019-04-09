package cn.com.venvy.common.mqtt;

import android.os.Bundle;
import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.common.bean.SocketConnectItem;
import cn.com.venvy.common.bean.SocketUserInfo;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;


/**
 * Created by Arthur on 2017/6/6.
 */

public class VenvyMqttClientHelper {

    private static final String TAG = VenvyMqttClientHelper.class.getName();
    private static final String START_MQTT_TASK = "connect_mqtt";
    //  SecretId
    //  MQTT客户端
    private volatile VenvyMqttClient mClient;
    //  MQTT参数设置
//    private static final String MQTT_KEY = "CSwtMsBf6OXprzjS";
//    private static final String MQTT_HOST = "tcp://post-cn-45908b8cn07.mqtt.aliyuncs.com:1883";
    private volatile Map<String, List<SocketConnectItem>> mTotalTopicFilters;
    private static VenvyMqttClientHelper sMqttClientHelper;
    private String socketKey = "";
    private String socketPassword = "";
    private String serverUrl = "";
    private String clientId = "";

    public static synchronized VenvyMqttClientHelper getInstance(SocketUserInfo info) {
        if (sMqttClientHelper == null) {
            sMqttClientHelper = new VenvyMqttClientHelper(info);
        }

        return sMqttClientHelper;
    }

    private VenvyMqttClientHelper(SocketUserInfo info) {
        if (info == null)
            return;
        String key = info.key;
        if (!TextUtils.isEmpty(key)) {
            socketKey = key;
        }
        String password = info.password;
        if (!TextUtils.isEmpty(password)) {
            socketPassword = password;
        }
        String host = info.host;
        String port = info.port;
        if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)) {
            serverUrl = "tcp://" + host + ":" + port;
        }
        String clientId = info.clientId;
        if (!TextUtils.isEmpty(clientId)) {
            this.clientId = clientId;
        }
    }


    public void subscribeAndConnect(final Set<SocketConnectItem> topicFilters) {
        if (topicFilters == null || topicFilters.size() <= 0) {
            return;
        }
        Map<String, List<SocketConnectItem>> currentMqttMap = mTotalTopicFilters;
        if (currentMqttMap == null) {
            currentMqttMap = new HashMap<>();
        }
        for (SocketConnectItem item : topicFilters) {
            List<SocketConnectItem> list = currentMqttMap.get(item.topic);
            if (list == null) {
                list = new ArrayList<>();
                list.add(item);
                currentMqttMap.put(item.topic, list);
            } else {
                if (list.contains(item)) {
                    continue;
                }
                list.add(item);
                currentMqttMap.put(item.topic, list);
            }
        }

        if (currentMqttMap.size() == 0) {
            return;
        }
        if (mTotalTopicFilters == null || mTotalTopicFilters.size() <= 0 || mTotalTopicFilters.size() != currentMqttMap.size()) {
            subscribeAndConnect(currentMqttMap);
        }
        mTotalTopicFilters = currentMqttMap;
    }

    public void removeTopics(Set<SocketConnectItem> items) {
        try {
            if (items == null || items.size() <= 0) {
                return;
            }
            if (mTotalTopicFilters == null || items.size() <= 0) {
                return;
            }

            List<String> removeTopics = null;
            for (SocketConnectItem item : items) {
                List<SocketConnectItem> list = mTotalTopicFilters.get(item.topic);
                if (list == null) {
                    continue;
                }
                list.remove(item);
                if (list.size() <= 0) {
                    if (removeTopics == null) {
                        removeTopics = new ArrayList<>();
                    }
                    removeTopics.add(item.topic);
                    mTotalTopicFilters.remove(item.topic);
                }
            }
            if (mClient != null && removeTopics != null && removeTopics.size() > 0) {
                String[] removeTopicsArray = new String[removeTopics.size()];
                for (int i = 0; i < removeTopicsArray.length; i++) {
                    removeTopicsArray[i] = removeTopics.get(i);
                }
                mClient.unsubscribe(removeTopicsArray);
            }
            mTotalTopicFilters = null;
        } catch (Exception e) {
            VenvyLog.e(TAG, e);
        }
    }

    public void destroy() {
        try {
            mTotalTopicFilters = null;
            VenvyAsyncTaskUtil.cancel(START_MQTT_TASK);
            recyclerClient();
        } catch (Exception e) {
            VenvyLog.e(TAG, e);
        }
    }


    private void subscribeAndConnect(final Map<String, List<SocketConnectItem>> currentMqttsMap) {
        if (currentMqttsMap == null || currentMqttsMap.size() == 0) {
            return;
        }
        final String[] topics = new String[currentMqttsMap.size()];
        final int[] qos = new int[currentMqttsMap.size()];
        int i = 0;
        for (String key : currentMqttsMap.keySet()) {
            topics[i] = key;
            SocketConnectItem item = currentMqttsMap.get(key).get(0);
            if (item != null) {
                qos[i] = item.qos;
            }
            ++i;
        }
        VenvyAsyncTaskUtil.doAsyncTask(START_MQTT_TASK,
                new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Void>() {
                    @Override
                    public Void doAsyncTask(Void... voids) throws Exception {
                        if (mClient == null) {
                            mClient = initMqttClient();
                        }
                        if (mClient.isConnected()) {
                            mClient.subscribe(topics, qos);
                            return null;
                        }
                        connect(currentMqttsMap);
                        return null;
                    }
                }, null);

    }

    /***
     * 初始化MQTT
     */
    private VenvyMqttClient initMqttClient() throws Exception {
        VenvyMqttClient client = new VenvyMqttClient(serverUrl, !TextUtils.isEmpty(clientId) ? clientId : MacSignature.subClientId(), new MemoryPersistence());// 初始化客户端
        client.setTimeToWait(5000);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        subscribeAndConnect(mTotalTopicFilters);
                    }
                }, 2000);
            }

            @Override
            public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                if (VenvyUIUtil.isOnUIThread()) {
                    if (!TextUtils.isEmpty(topic) && topic.length() >= 2 && topic.endsWith("/")) {
                        dispatchMessage(topic.substring(0, topic.length() - 1), message.toString());
                    } else {
                        dispatchMessage(topic, message.toString());
                    }
                } else {
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(topic) && topic.length() >= 2 && topic.endsWith("/")) {
                                dispatchMessage(topic.substring(0, topic.length() - 1), message.toString());
                            } else {
                                dispatchMessage(topic, message.toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        return client;
    }

    private MqttConnectOptions initMqttOption() {
        // MQTT的连接设置

        MqttConnectOptions connectOption = new MqttConnectOptions();// MQTT参数
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        connectOption.setCleanSession(false);
        // 设置连接的用户名
        connectOption.setUserName(socketKey);
        connectOption.setServerURIs(new String[]{serverUrl});
        // 设置连接的密码
        connectOption.setPassword(socketPassword.toCharArray());
        // 设置超时时间 单位为秒
        connectOption.setConnectionTimeout(15);
        // 设置会话心跳时间 单位为秒 服务器会每隔2*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        connectOption.setKeepAliveInterval(40);
//        connectOption.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        return connectOption;
    }


    private void connect(final Map<String, List<SocketConnectItem>> items) throws Exception {
        if (mClient == null) {
            return;
        }
        mClient.connect(initMqttOption(), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                subscribeAndConnect(items);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                try {
                    if (!VenvyUIUtil.isOnUIThread()) {
                        Thread.sleep(2000);
                        subscribeAndConnect(items);
                    } else {
                        VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
                            @Override
                            public void run() {
                                subscribeAndConnect(items);
                            }
                        }, 2000);
                    }
                } catch (Exception e) {
                    VenvyLog.e(TAG, e);
                }
            }
        });
    }

    private void dispatchMessage(String topic, String message) {
        if (TextUtils.isEmpty(topic)) {
            return;
        }
        String target = VenvyObservableTarget.TAG_ARRIVED_DATA_MESSAGE;
        Bundle bundle = new Bundle();
        bundle.putString("data", message);
        bundle.putString("topic", topic);
        ObservableManager.getDefaultObserable().sendToTarget(target, bundle);
    }


    private void recyclerClient() throws Exception {
        if (mClient != null) {
            mClient.setCallback(null);
            mClient.disconnect();
            mClient.close();
        }
        mClient = null;
    }
}
