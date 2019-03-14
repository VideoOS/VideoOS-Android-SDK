package cn.com.venvy.common.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Created by yanjiangbo on 2017/6/27.
 */

public class VenvyMqttClient extends MqttClient {

    public VenvyMqttClient(String serverURI, String clientId) throws MqttException {
        super(serverURI, clientId, new MqttDefaultFilePersistence());
    }

    public VenvyMqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws Exception {
        super(serverURI, clientId, persistence);
    }

    public void connect(MqttConnectOptions options, IMqttActionListener listener) throws MqttSecurityException, MqttException {
        this.aClient.connect(options, (Object) null, listener).waitForCompletion(this.getTimeToWait());
    }
}
