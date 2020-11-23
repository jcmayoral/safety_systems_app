package com.github.rosjava.android_apps.teleop2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttOutputStream;

import java.io.UnsupportedEncodingException;

public class MyMqttClient extends Application {
    String clientId;
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;

    public void publishString(){
        String topic = "grass/commands";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            MqttMessage message = new MqttMessage();
            //message.setRetained(true);
            message.setPayload(payload.getBytes());
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public void publishBoolean(){
        String topic = "grass/estop";
        String payload = "the payload";
        Boolean msg = true;
        byte[] encodedPayload = new byte[0];
        try {
            MqttMessage message = new MqttMessage();
            //message.setRetained(true);
            message.setPayload(msg.toString().getBytes());
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public void subscribeTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
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

    public void run(Context context){
        clientId = MqttClient.generateClientId();
        Log.w("AQUI", "AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        /* Create an MqttConnectOptions object and configure the username and password. */
        mqttConnectOptions = new MqttConnectOptions();
        //mqttConnectOptions.setUserName(userName);
        //mqttConnectOptions.setPassword(passWord.toCharArray());
        Log.w("AQUI", "BBBBBBBBBBBBBBBBBBBBBBBBbb");

        mqttAndroidClient = new MqttAndroidClient(context, "tcp://10.230.46.14:1883", clientId);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i("Connected", "connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("message arrived", "topic: " + topic + ", msg: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("delivered", "msg delivered");
            }
        });
        Log.w("AQUI", "CCCCCCCCCCCCCCCCCC");

        /* Establish an MQTT connection */
        try {
            mqttAndroidClient.connect(mqttConnectOptions, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("connected succeed", "connect succeed");

                    subscribeTopic("grass/test");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("connected failed", "connect failed");
                    Log.w("Connected failed", exception.getMessage());
                }
            });

        } catch (MqttException e) {
            Log.w("AQUI", "eeedddddddddddddddddd");

            e.printStackTrace();
        }
        Log.w("AQUI", "BBBBBBBBBBBBBBBBBBBBBBBBbbfinishing");


    }
}
