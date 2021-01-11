package com.mayoral.android_apps.mqtt_teleop;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

public class MyUtils {
    static JSONObject generateSimpleJSON(String key, String value) {
        JSONObject jobject = new JSONObject();
        try {
            jobject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobject;
    }

    static JSONObject generateXMODEJSON(String mode, String action) {
        JSONObject jobject = new JSONObject();
        try {
            jobject.put("MODE", mode);
            jobject.put("ACTION", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobject;
    }

    static JSONObject generateDataJSON(String mode, String data) {
        JSONObject jobject = new JSONObject();
        try {
            jobject.put("ACTION", mode);
            jobject.put("DATA", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobject;
    }

    static int selectColor(boolean flag){
        if (flag){
            return Color.parseColor("#00FF00");
        }
        return Color.parseColor("#FF0000");
    }
}
