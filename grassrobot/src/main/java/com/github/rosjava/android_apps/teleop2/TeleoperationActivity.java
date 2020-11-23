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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttOutputStream;
import org.ros.android.view.VirtualJoystickView;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;

import sensor_msgs.CompressedImage;

/**
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class TeleoperationActivity extends RosAppActivity {
	private VirtualJoystickView virtualJoystickView;
	private Button backButton;
	static EStopPublisher estop;
	static BluetoothTracker bluetoothtracker;
	private MyMqttClient myMqttClient;

	public TeleoperationActivity() {
		// The RosActivity constructor configures the notification title and ticker messages.
		super("android teleop2", "android teleop2");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setDashboardResource(R.id.top_bar);
		setMainWindowResource(R.layout.teleoperation);
		// setDefaultAppName("Safety_systems");
		super.onCreate(savedInstanceState);
        //setContentView(R.layout.mode_selector);
        virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
	}

	public void pressEStop(View view){
		estop.publish();
		myMqttClient.publishString();
		myMqttClient.publishBoolean();
		if(estop.getState()) {
			view.setBackgroundColor(Color.parseColor("#FF0000"));
		}
		else{
			view.setBackgroundColor(Color.parseColor("#00FF00"));
		}
	}

	public void bluetoothSwitch(View v){
		Switch sw1 = (Switch)(v.findViewById(R.id.switch1));
		bluetoothtracker.publish(sw1.isChecked());
	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {

		super.init(nodeMainExecutor);
		Log.v("Before try", "EFFFFFFFFFFFFFFFFFF");
        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());


		String joyTopic = remaps.get(getString(R.string.joystick_topic));
        String camTopic = remaps.get(getString(R.string.camera_topic));
        String stopTopic = remaps.get("estop_topic");


        NameResolver appNameSpace = getMasterNameSpace();
        joyTopic = appNameSpace.resolve(joyTopic).toString();
        camTopic = appNameSpace.resolve(camTopic).toString();
        stopTopic = appNameSpace.resolve(stopTopic).toString();

        virtualJoystickView.setTopicName(joyTopic);

        estop = new EStopPublisher();
        bluetoothtracker = new BluetoothTracker();

		nodeMainExecutor.execute(virtualJoystickView,
				nodeConfiguration.setNodeName("android/virtual_joystick"));
		nodeMainExecutor.execute(estop, nodeConfiguration.setNodeName("android/estop"));
		nodeMainExecutor.execute(bluetoothtracker, nodeConfiguration.setNodeName("android/ebluetooth"));

		} catch (IOException e) {
            // Socket problem
        }
		myMqttClient = new MyMqttClient();
		myMqttClient.run(getApplicationContext());


	}
}