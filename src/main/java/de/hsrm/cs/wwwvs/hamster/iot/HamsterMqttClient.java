package de.hsrm.cs.wwwvs.hamster.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class HamsterMqttClient {

    private SimulatedHamster _hamster;

    public HamsterMqttClient(SimulatedHamster hamster) {
        _hamster = hamster;
    }

    public void connect(String host, boolean encryptedConnection, boolean authenticateClient) throws Exception {
        // TODO: connect to MQTT broker
        String broker = (encryptedConnection ? "ssl://" : "tcp://") + host;
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sampleClient = new MqttClient(broker, _hamster.getHamsterId(), persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);

        if (authenticateClient) {
            connOpts.setUserName("your_username");
            connOpts.setPassword("your_password".toCharArray());
        }

        System.out.println("Connecting to broker: "+broker);
        sampleClient.connect(connOpts);
        System.out.println("Connected");

    }

    public void eat() {
        // TODO: implement
    }

    public void mate() {
        // TODO: implement
    }

    public void sleep() {
        // TODO: implement
    }

    public void run() {
        // TODO: implement
    }

    public void move(String position) {
        // TODO: implement
    }

    public void disconnect() throws MqttException {
       // TODO: disconnect from broker
    }
}
