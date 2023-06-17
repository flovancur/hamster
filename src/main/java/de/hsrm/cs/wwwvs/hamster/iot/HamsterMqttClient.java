package de.hsrm.cs.wwwvs.hamster.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HamsterMqttClient {

    private SimulatedHamster _hamster;
    public HamsterMqttClient(SimulatedHamster hamster) {
        _hamster = hamster;
    }

    String pension ="pension/";
    String livestock = pension.concat("livestock");
    MqttClient sampleClient;

    Map<String, List<String>> idsByPosition = new HashMap<>();

    public void connect(String host, boolean encryptedConnection, boolean authenticateClient) throws Exception {
        // TODO: connect to MQTT broker
        String broker = (encryptedConnection ? "ssl://" : "tcp://") + host;
        MemoryPersistence persistence = new MemoryPersistence();
        sampleClient = new MqttClient(broker, _hamster.getHamsterId(), persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);

        if (authenticateClient) {
            connOpts.setUserName("your_username");
            connOpts.setPassword("your_password".toCharArray());
        }

        System.out.println("Connecting to broker: "+broker);
        sampleClient.connect(connOpts);
        System.out.println("Connected");
        int qos = 2;
        MqttMessage message = new MqttMessage(_hamster.getHamsterId().getBytes());
        message.setQos(qos);
        sampleClient.publish(livestock,message);

        sampleClient.subscribe(pension + "hamster/" + _hamster.getHamsterId() +"/fondle");
        sampleClient.subscribe(pension + "hamster/" + _hamster.getHamsterId() +"/punish");
        sampleClient.subscribe(pension + "hamster/" + _hamster.getHamsterId() +"/position");

        sampleClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Wird aufgerufen, wenn die Verbindung verloren geht
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                int payload = Integer.parseInt(new String(message.getPayload()));
                if (topic.contains("fondle")){
                    _hamster.fondle(payload);
                } else if (topic.contains("punish")) {
                    _hamster.punish(payload);
                } else {
                    handleRooms(topic, message);
                }
            }

            private void handleRooms(String topic, MqttMessage message) throws MqttException {
                String room = new String(message.getPayload());
                String id = topic.replace("/pension/hamster/", "");
                id = id.replace("/position", "");

                idsByPosition.putIfAbsent(room, new ArrayList<>());
                idsByPosition.get(room).add(id);

                String listMessage = String.join(",", idsByPosition.get(room));
                sampleClient.publish("pension/room/" + room, new MqttMessage(listMessage.getBytes()));
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Wird aufgerufen, wenn die Zustellung abgeschlossen ist
            }
        });
    }

    public void eat() throws MqttException {
        // TODO: implement
        _hamster.stopRunning();
        int qos = 1;
        MqttMessage message = new MqttMessage("EATING".getBytes());
        message.setQos(qos);
        sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/state",message);
    }

    public void mate() throws MqttException {
        // TODO: implement
        _hamster.stopRunning();
        int qos = 1;
        MqttMessage message = new MqttMessage("MATING".getBytes());
        message.setQos(qos);
        sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/state",message);
    }

    public void sleep() throws MqttException {
        // TODO: implement
        _hamster.stopRunning();
        int qos = 1;
        MqttMessage message = new MqttMessage("SLEEPING".getBytes());
        message.setQos(qos);
        sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/state",message);
    }

    public void run() throws MqttException {
        // TODO: implement
        _hamster.startRunning();
        int qos = 1;
        MqttMessage message = new MqttMessage("RUNNING".getBytes());
        message.setQos(qos);
        sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/state",message);
        _hamster.setRevolutionCallback(rounds -> {
            MqttMessage wheelMessage = new MqttMessage(String.valueOf(rounds).getBytes());
            message.setQos(2);
            try {
                sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/wheels",wheelMessage);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void move(String position) throws MqttException {
        // TODO: implement

        int qos = 1;
        MqttMessage message = new MqttMessage(position.getBytes());
        message.setQos(qos);
        sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() +"/position",message);

    }

    public void disconnect() throws MqttException {
        // TODO: disconnect from broker

        sampleClient.disconnect();
    }
}
