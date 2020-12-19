package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class CommonActivity extends Activity {
    private Button backButton;
    MyMqttClient myMqttClient;
    RobotState robot_state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setDashboardResource(R.id.top_bar);
        //setMainWindowResource(R.layout.teleoperation);
        // setDefaultAppName("Safety_systems");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teleoperation);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        myMqttClient = MainActivity.getMyMqttClient();
        robot_state = MainActivity.getState();
        Button button = (Button) findViewById(R.id.button);
        button.setBackgroundColor(selectColor());

        myMqttClient.publishCommand("XMode", "START", "Teleoperation");

    }

    int selectColor(){
        if (robot_state.estop){
            return Color.parseColor("#00FF00");
        }
        return Color.parseColor("#FF0000");
    }

    public void pressEStop(View view){
        //estop.publish();
        if (!myMqttClient.isConnectionDone()){
            return;
        }
        robot_state.estop = !robot_state.estop;
        myMqttClient.publishString();
        myMqttClient.publishEStop(robot_state.estop);
        view.setBackgroundColor(selectColor());
    }

    public void bluetoothSwitch(View v){
        Switch sw1 = (Switch)(v.findViewById(R.id.switch1));
        //bluetoothtracker.publish(sw1.isChecked());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.setState(robot_state);
        myMqttClient.publishCommand("XMode", "STOP" , "Teleoperation");
    }


}
