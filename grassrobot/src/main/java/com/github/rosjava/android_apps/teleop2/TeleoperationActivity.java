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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.RosActivity;
import org.ros.android.view.VirtualJoystickView;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TeleoperationActivity extends Activity {
	private VirtualJoystickView virtualJoystickView;
	private Button backButton;
	//static EStopPublisher estop;

	private TextView angle_view;
	private TextView strength_view;
	private TextView coordinate_view;

	JoystickView joystick;
	//static BluetoothTracker bluetoothtracker;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//setDashboardResource(R.id.top_bar);
		//setMainWindowResource(R.layout.teleoperation);
		// setDefaultAppName("Safety_systems");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teleoperation);

		angle_view = (TextView) findViewById(R.id.angle_view);
		strength_view = (TextView) findViewById(R.id.strength_view);
		coordinate_view = findViewById(R.id.coordinate_view);
		joystick = (JoystickView) findViewById(R.id.mqtt_joystick);
		joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				angle_view.setText(angle + "°");
				strength_view.setText(strength + "%");
				coordinate_view.setText(
						String.format("x%03d:y%03d:d%03d",
								joystick.getNormalizedX(),
								joystick.getNormalizedY(),
								joystick.getButtonDirection())
				);
			}
		});

		//virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
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


	}

	int selectColor(){
		if (robot_state.estop){
			return Color.parseColor("#00FF00");
		}
		return Color.parseColor("#FF0000");
	}

	public void pressEStop(View view){
		//estop.publish();
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
	}
}
