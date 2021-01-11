package com.mayoral.android_apps.mqtt_teleop;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyMqttClient {
    String clientId;

    //Extends does not work somehow
    MqttAndroidClient client;
    MqttConnectOptions mqttConnectOptions;
    static MyMqttCallback myMqttCallback;

    String chost = "10.0.0.24";
    int cport = 1883;


    String getHost(){ return  chost;};
    int getPort(){ return cport;};
    void setHost(String h){chost = h;};
    void setPort(int p){cport =p;};

    public void publishString(){
        if (!client.isConnected()){
            return;
        }
        String topic = "grass/safety_request";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            MqttMessage message = new MqttMessage();
            //message.setRetained(true);
            message.setPayload(payload.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grasx|
        // s/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public void publishCommand(String type, String command_id, String command){
        if (!client.isConnected()){
            return;
        }
        String topic = "grass/safety_request";
        //String payload = "the payload";
        //double number = 10.0;
        JSONObject jsonmessage = new JSONObject();
        JSONObject jsonvalues = new JSONObject();

        byte[] encodedPayload = new byte[0];
        try {
            jsonmessage.put("type", type);
            jsonvalues.put(command_id, command);
            //jsonvalues.put("double", number);
            jsonmessage.put("commands", jsonvalues);
            MqttMessage message = new MqttMessage();
            message.setPayload(jsonmessage.toString().getBytes());
            client.publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }


    public void publishEStop(boolean state){
        if (!client.isConnected()){
            return;
        }
        String topic = "grass/safety_request";
        //String payload = "the payload";
        //double number = 10.0;
        JSONObject jsonmessage = new JSONObject();
        JSONObject jsonvalues = new JSONObject();

        byte[] encodedPayload = new byte[0];
        try {
            jsonmessage.put("type", "Estop");
            jsonvalues.put("boolean",state);
            //jsonvalues.put("double", number);
            jsonmessage.put("commands", jsonvalues);
            MqttMessage message = new MqttMessage();
            message.setPayload(jsonmessage.toString().getBytes());
            client.publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
    }

    public String waitForAnswer(){
        Log.w("esperando", "respuesta esperando");
        while (!myMqttCallback.isMessageReceived()){
        }
        return myMqttCallback.getAnswer();
    }

    public void subscribetoTopic(String topic, Context context) {
        try {
            Log.w("ERROR=", topic + String.valueOf(client.isConnected()));
            client.subscribe(topic, 0, context, new IMqttActionListener() {
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


    public boolean run(Context context, String ipAddress, int port){
        /* Create an MqttConnectOptions object and configure the username and password. */
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);


        String serveruri = "tcp://" + ipAddress+":"+String.valueOf(port);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, serveruri, clientId);

        myMqttCallback = new MyMqttCallback();
        client.setCallback(myMqttCallback);
        Log.w("Mqtt", "trying to connect  " + ipAddress+"  port"+port);

        try {
            IMqttToken connectToken = client.connect(mqttConnectOptions, context, new IMqttActionListener() {
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
            chost = ipAddress;
            cport = port;
        }

        return client.isConnected();
    }

    //TODO implement
    public IMqttToken disconnect() {
        if (client.isConnected() && client != null) {
            try {
                IMqttToken token = client.unsubscribe("grass/safety");
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
