package com.mayoral.android_apps.mqtt_teleop;

import static com.mayoral.android_apps.mqtt_teleop.BagSelector.setList;

import android.util.Log;
import android.widget.PopupWindow;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

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
        JSONObject jsonmsg = new JSONObject(message.toString());
        Log.w("message in json", "msg " + jsonmsg.toString());

        answer = jsonmsg.getString("msg");
        String type = jsonmsg.getString("type");
        Log.w("message arrived", "topic: " + topic + ", msg: " + answer + " type " + type);
        MainActivity.setMessageReceived(true);;

        //RETURN LIST TOPIC
        if (type.compareTo("ROSTOPIC")==0) {
            setList(answer);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i("delivered", "msg delivered");
        //connection_flag = true;
    }
}
