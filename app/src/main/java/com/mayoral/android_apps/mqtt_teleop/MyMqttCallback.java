package com.mayoral.android_apps.mqtt_teleop;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttCallback implements MqttCallback {
    static String answer ="ERROR";

    public  static String getAnswer(){
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
        answer = new String(message.getPayload());
        MainActivity.setMessageReceived(true);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i("delivered", "msg delivered");
        //connection_flag = true;
    }
}
