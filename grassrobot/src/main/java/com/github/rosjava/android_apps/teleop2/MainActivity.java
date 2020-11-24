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

package com.github.rosjava.android_apps.teleop2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends Activity {

    private static RobotState robot_state;
    private static MyMqttClient myMqttClient;


    public static RobotState getState(){
        return robot_state;
    }

    public static MyMqttClient getMyMqttClient(){
        return myMqttClient;
    }

    public static void setState(RobotState state){
        robot_state = state;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight);
        setContentView(R.layout.mode_selector);
        myMqttClient = new MyMqttClient();
        myMqttClient.run(getApplicationContext());

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
        Intent intent = new Intent(this, FullAutonomationActivity.class);
        startActivity(intent);
    }
}
