package com.mayoral.android_apps.mqtt_teleop;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

public class MyMqttClient {
    String clientId;
    final String UPSTREAM_TOPIC = "grass/safety_request";

    //Extends does not work somehow
    MqttAndroidClient client;
    MqttConnectOptions mqttConnectOptions;
    MyMqttCallback myMqttCallback;
    boolean isMessageReady = false;

    String chost = "10.0.0.24";
    int cport = 1883;

    String getHost(){ return  chost;};
    int getPort(){ return cport;};
    void setHost(String h){chost = h;};
    void setPort(int p){cport =p;};

    public void publishCommand(String type, JSONObject command){
        if (!client.isConnected()){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                JSONObject jsonmessage = new JSONObject();
                JSONObject jsonvalues = new JSONObject();

                byte[] encodedPayload = new byte[0];
                try {
                    jsonmessage.put("type", type);
                    //jsonvalues.put(command_id, command);
                    jsonmessage.put("commands", command);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                publishMessage(jsonmessage);
            }
        }).start();
    }

    private void publishMessage(JSONObject jsonmessage){
        Log.e("publishmessage", "publish");
        MqttMessage message = new MqttMessage();
        message.setQos(2);
        message.setRetained(false);
        message.setPayload(jsonmessage.toString().getBytes());
        try {
            client.publish(UPSTREAM_TOPIC, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishEStop(boolean state){
        if (!client.isConnected()){
            return;
        }
        JSONObject jsonmessage = new JSONObject();
        JSONObject jsonvalues = new JSONObject();

        try {
            jsonmessage.put("type", "EStop");
            jsonvalues.put("ACTION",state);
            jsonmessage.put("commands", jsonvalues);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publishMessage(jsonmessage);
    }

    public String waitForAnswer(){
        String answer = myMqttCallback.getAnswer();
        MainActivity.setMessageReceived(false);
        isMessageReady = false;
        return answer;
        // return "NOTHING::NOTHING";
    }

    public void subscribetoTopic(String topic, Context context) {

        try {
            Log.w("ERROR=", topic + String.valueOf(client.isConnected()));
            client.subscribe(topic, 2, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("subscribed success", "subscribed succeed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("subscribed Failed", "subscribed failed");
                }
            });

        } catch (MqttException e) {
            Log.w("AQUI", "DDDDDDDDDDDDDDDDDDDDDDSW");

            e.printStackTrace();
        }
    }


    public boolean connectMqtt(Context context, String ipAddress, int port){
        if (client != null){
            Log.e("connectMQtt", "connexion en progreso");
            return  false;
        }
        /* Create an MqttConnectOptions object and configure the username and password. */
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setKeepAliveInterval(200);

        String serveruri = "tcp://" + ipAddress+":"+String.valueOf(port);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, serveruri, clientId);

        myMqttCallback = new MyMqttCallback();
        client.setCallback(myMqttCallback);
        Log.w("Mqtt", "trying to connect  " + ipAddress+"  port"+port);

        try {
            IMqttToken connectToken = client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Mqtt", "connected SUCCESS");
                    Log.w("connected succeed", "connect succeed");
                    if (client.isConnected()) {
                        subscribetoTopic("grass/safety_callback", null);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt", "error connecting");
                    Log.w("connected failed", "connect failed");
                    Log.w("Connected failed", exception.getMessage());
                }
            });

        } catch (MqttException e) {
                Log.e("e", e.getMessage());
        }

        if (client.isConnected()){
            Log.e("mqtt", "save host and port");
            chost = ipAddress;
            cport = port;
        }

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()){
                    if (MainActivity.isMessageReceived()){
                        Log.i("run", "message received " + MainActivity.isMessageReceived());
                        isMessageReady = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //isMessageReady = false;
                        //MainActivity.setMessageReceived(false);
                    }
                }
            }
        }).start();
        */
        return client.isConnected();
    }

    //TODO implement
    public IMqttToken disconnect() {
        if (client.isConnected() && client != null) {
            try {
                IMqttToken token = client.unsubscribe("grass/safety_callback");
                client.disconnect();
                client.unregisterResources();
                client = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
