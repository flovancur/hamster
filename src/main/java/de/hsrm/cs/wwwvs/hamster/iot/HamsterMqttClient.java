package de.hsrm.cs.wwwvs.hamster.iot;

import org.eclipse.paho.client.mqttv3.*;

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
