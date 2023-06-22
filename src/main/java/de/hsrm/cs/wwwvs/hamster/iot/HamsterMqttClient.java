package de.hsrm.cs.wwwvs.hamster.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;


import org.xml.sax.SAXException;


public class HamsterMqttClient {

    private SimulatedHamster _hamster;
    public HamsterMqttClient(SimulatedHamster hamster) throws IOException, ParserConfigurationException, SAXException {
        _hamster = hamster;
    }

    String pension ="/pension/";
    String livestock = pension.concat("livestock");
    MqttClient sampleClient;



    MqttCallback hamsterCallback = new MqttCallbackExtended(){
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {

        }
        @Override
        public void connectionLost(Throwable cause) {
            // Wird aufgerufen, wenn die Verbindung verloren geht
        }
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            ByteBuffer pload = ByteBuffer.allocate(4);
            pload.put(message.getPayload());
            pload.rewind();
            int payload = pload.getInt();
            if (topic.contains("fondle")){
                _hamster.fondle(payload);
            } else{
                _hamster.punish(payload);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // Wird aufgerufen, wenn die Zustellung abgeschlossen ist
        }
    };

    public void connect(String host, boolean encryptedConnection, boolean authenticateClient) throws Exception {
        // TODO: connect to MQTT broker
        String broker = encryptedConnection ? "ssl://"+host+":8883" : "tcp://"+host+":1883";
        MemoryPersistence persistence = new MemoryPersistence();
        sampleClient = new MqttClient(broker, authenticateClient ? "fvanc001" : _hamster.getHamsterId(), persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName("hamster");
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        connOpts.setUserName("hamster");

        if (encryptedConnection) {
            SSLSocketFactory factory = TlsUtil.getSocketFactory("./certs/ca.crt",null, null, null);
            connOpts.setSocketFactory(factory);
        }
        if(authenticateClient){
            SSLSocketFactory factory = TlsUtil.getSocketFactory("./certs/ca.crt","./certs/vancura.crt", "./certs/client.key", null);
            connOpts.setSocketFactory(factory);
        }
        System.out.println("Connecting to broker: "+broker);
        sampleClient.setCallback(this.hamsterCallback);
        System.out.println("Connected");
        int qos = 2;
        MqttMessage message = new MqttMessage(_hamster.getHamsterId().getBytes());
        message.setQos(qos);
        sampleClient.connect(connOpts);
        sampleClient.publish(livestock,message);
        try {
            sampleClient.subscribe(pension + "hamster/" + _hamster.getHamsterId() +"/fondle",2);
            sampleClient.subscribe(pension + "hamster/" + _hamster.getHamsterId() +"/punish",2);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

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
        MqttMessage message = new MqttMessage("MATEING".getBytes());
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
        _hamster.stopRunning();
        try {
            int qos = 1;
            MqttMessage message = new MqttMessage(position.getBytes());
            message.setQos(qos);
            message.setRetained(true);
            if (!sampleClient.isConnected()){
                sampleClient.connect();
            } else {
                sampleClient.publish(pension + "hamster/" + _hamster.getHamsterId() + "/position", message);
            }
        } catch (MqttException e){
            System.out.println(e.getMessage());
        }
    }

    public void disconnect() throws MqttException {
        sampleClient.disconnect();
    }
}