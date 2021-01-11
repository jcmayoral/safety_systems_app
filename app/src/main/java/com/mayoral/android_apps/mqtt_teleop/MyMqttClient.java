package com.mayoral.android_apps.mqtt_teleop;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.json.JSONException;
import org.json.JSONObject;

public class MyMqttClient extends  MqttAndroidClient  {
    String clientId;
    //MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;
    static MyMqttCallback myMqttCallback;

    String chost = "10.0.0.24";
    int cport = 1883;

    public MyMqttClient(Context context, String serverURI, String clientId) {
        super(context, serverURI, clientId);
    }


    String getHost(){ return  chost;};
    int getPort(){ return cport;};
    void setHost(String h){chost = h;};
    void setPort(int p){cport =p;};

    public void publishString(){
        if (!isConnected()){
            return;
        }
        String topic = "grass/commands";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            MqttMessage message = new MqttMessage();
            //message.setRetained(true);
            message.setPayload(payload.getBytes());
            publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grasx|
        // s/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public void publishCommand(String type, String command_id, String command){
        if (!isConnected()){
            return;
        }
        String topic = "grass/estop";
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
            publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }


    public void publishEStop(boolean state){
        if (!isConnected()){
            return;
        }
        String topic = "grass/estop";
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
            publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public String waitForAnswer(){
        Log.w("esperando", "respuesta esperando");
        while (!myMqttCallback.isMessageReceived()){
        }
        return myMqttCallback.getAnswer();
    }

    public void subscribeTopic(String topic, Context context) {
        try {
            Log.w("ERROR=", topic + String.valueOf(isConnected()));
            subscribe(topic, 0, context, new IMqttActionListener() {
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
        //clientId = MqttClient.generateClientId();
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        //mqttConnectOptions.setUserName(userName);
        //mqttConnectOptions.setPassword(passWord.toCharArray());

        //ZOTAC
        //mqttAndroidClient = new MqttAndroidClient(context, "tcp://10.230.46.14:1883", clientId);
        //PERSONAL PC
        //mqttAndroidClient = new MqttAndroidClient(context, "tcp://10.230.41.2:1883", clientId);
        //Brekkeveien
        //String serveruri = "tcp://" + ipAddress+":"+String.valueOf(port);

        //setHost(ipAddress);
        //setPort(port);
        //mqttAndroidClient = new MqttAndroidClient(context, serveruri, clientId);
        myMqttCallback = new MyMqttCallback();
        setCallback(myMqttCallback);
        Log.w("Mqtt", "trying to connect  " + ipAddress+"  port"+port);
        try {
            IMqttToken connectToken = connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Mqtt", "connected SUCCESS");
                    Log.w("connected succeed", "connect succeed");
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

        if (isConnected()){
            chost = ipAddress;
            cport = port;
            subscribeTopic("grass/test", context);
        }

        return isConnected();
    }
}
