package com.mayoral.android_apps.mqtt_teleop;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttCallback implements MqttCallback {
    String answer ="";
    boolean connection_flag = false;
    boolean answer_ready = false;

    public  boolean isMessageReceived(){
        return  answer_ready;
    }

    public void clearMessageFlag(){
        answer_ready = false;
    }
    public  String getAnswer(){
        return answer;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("Connected", "connection lost");
        //connection_flag = false;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e("message arrived", "topic: " + topic + ", msg: " + new String(message.getPayload()));
        connection_flag = true;
        answer = new String(message.getPayload());
        answer_ready = true;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i("delivered", "msg delivered");
        //connection_flag = true;
    }
}
