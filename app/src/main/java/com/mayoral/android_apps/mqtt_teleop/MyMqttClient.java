package com.mayoral.android_apps.mqtt_teleop;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class MyMqttClient {
    String clientId;
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;
    String chost = "10.0.0.24";
    int cport = 1883;
    boolean connection_flag = false;

    String getHost(){ return  chost;};
    int getPort(){ return cport;};
    void setHost(String h){chost = h;};
    void setPort(int p){cport =p;};

    public void publishString(){
        if (!connection_flag){
            return;
        }
        String topic = "grass/safety";
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

    public void publishCommand(String type, String command_id, String command){
        if (!connection_flag){
            return;
        }
        String topic = "grass/safety";
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
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }


    public void publishEStop(boolean state){
        if (!connection_flag){
            return;
        }
        String topic = "grass/safety";
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
            mqttAndroidClient.publish(topic, message);
        } catch (MqttException | JSONException e) {
            e.printStackTrace();
        }
        //mqttAndroidClient.pu.publish("grass/test2", payload=random.normalvariate(30, 0.5), qos=0)
    }

    public void subscribetoTopic(String topic, Context context) {
        try {
            Log.w("AAAAAAAAAAAAAAAA", mqttAndroidClient.getClientId());
            IMqttToken subscribe_token =  mqttAndroidClient.subscribe(topic, 0, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("subscribed success", "subscribed succeed");
                    connection_flag = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("subscribed Failed", "subscribed failed");
                    connection_flag = false;
                }
            });

        } catch (MqttException e) {
            Log.w("AQUI", "DDDDDDDDDDDDDDDDDDDDDDSW");

            e.printStackTrace();
        }
    }

    boolean isConnectionDone(){
        return connection_flag;
    }

    public boolean run(Context context, String ipAddress, int port){
        clientId = MqttClient.generateClientId();
        /* Create an MqttConnectOptions object and configure the username and password. */
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setUserName(userName);
        //mqttConnectOptions.setPassword(passWord.toCharArray());

        //ZOTAC
        //mqttAndroidClient = new MqttAndroidClient(context, "tcp://10.230.46.14:1883", clientId);
        //PERSONAL PC
        //mqttAndroidClient = new MqttAndroidClient(context, "tcp://10.230.41.2:1883", clientId);
        //Brekkeveien
        String serveruri = "tcp://" + ipAddress+":"+String.valueOf(port);

        mqttAndroidClient = new MqttAndroidClient(context, serveruri, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i("Connected", "connection lost");
                connection_flag = false;
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("message arrived", "topic: " + topic + ", msg: " + new String(message.getPayload()));
                connection_flag = true;
                if (mqttAndroidClient.isConnected()){
                    subscribetoTopic("grasstest", context);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("delivered", "msg delivered");
                connection_flag = true;
            }
        });

        Log.w("Mqtt", "before connected");
        try {
            Log.e("Mqtt", "before connectinhg");
            mqttAndroidClient.connect(mqttConnectOptions, context, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Mqtt", "before connected");
                    Log.w("connected succeed", "connect succeed");
                    subscribetoTopic("grass/safety" , null);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt", "error connecting");
                    Log.w("connected failed", "connect failed");
                    Log.w("Connected failed", exception.getMessage());
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

        if (connection_flag){
            chost = ipAddress;
            cport = port;
        }
        return mqttAndroidClient.isConnected();
    }

    //TODO implement
    public void disconnect() {
        if (null != mqttAndroidClient && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.unsubscribe("grass/safety");
                mqttAndroidClient.disconnect();
                mqttAndroidClient.unregisterResources();
                mqttAndroidClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
