/*
 * Copyright (C) 2013 OSRF.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends Activity {
    private Button settingsButton;
    private static RobotState robot_state;
    private static MyMqttClient myMqttClient;
    public static boolean answer_ready;
    public static PopupWindow popupWindow;

    public static void setMessageReceived(boolean flag){
        answer_ready = flag;
        Log.e("mqtt","update message received to "+ flag);
    }
    public  static boolean isMessageReceived(){
        //Log.e("mqtt","return " + answer_ready);
        return answer_ready;
    }

    public static RobotState getState(){
        return robot_state;
    }

    public static MyMqttClient getMyMqttClient(){
        return myMqttClient;
    }

    public static void setMyMqttClient(MyMqttClient mqttclient){ myMqttClient = mqttclient;}

    public static void setState(RobotState state){
        robot_state = state;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight);
        setContentView(R.layout.mode_selector);
        answer_ready = false;

        //myMqttClient = new MyMqttClient();

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = settingsApp(view);
                startActivity(intent);
            }
        });

    }

    public Intent settingsApp(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        return intent;
    }


    public void teleOpMode(View view){
        Intent intent = new Intent(this, TeleoperationActivity.class);
        startActivity(intent);
    }

    public void semiAutomationMode(View view){
        Intent intent = new Intent(this, SemiAutomationActivity.class);
        startActivity(intent);
    }

    public void fullAutomationMode(View view){
        Intent intent = new Intent(this, FullAutomationActivity.class);
        startActivity(intent);
    }
}
