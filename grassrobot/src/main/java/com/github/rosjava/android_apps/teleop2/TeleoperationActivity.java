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

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;
import org.ros.android.view.VirtualJoystickView;
import android.util.Log;

import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;

import sensor_msgs.CompressedImage;

/**
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class TeleoperationActivity extends RosAppActivity {
	private RosImageView<CompressedImage> cameraView;
	private VirtualJoystickView virtualJoystickView;
	private Button backButton;
	EStopPublisher estop;

	public TeleoperationActivity() {
		// The RosActivity constructor configures the notification title and ticker messages.
		super("android teleop2", "android teleop2");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setDashboardResource(R.id.top_bar);
		setMainWindowResource(R.layout.teleoperation);
		//gma
		// setDefaultAppName("Safety_systems");
		super.onCreate(savedInstanceState);
        //setContentView(R.layout.mode_selector);
		cameraView = (RosImageView<CompressedImage>) findViewById(R.id.image);
        cameraView.setMessageType(sensor_msgs.CompressedImage._TYPE);
        cameraView.setMessageToBitmapCallable(new BitmapFromCompressedImage());
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
		if(estop.getState()) {
			view.setBackgroundColor(Color.parseColor("#FF0000"));
		}
		else{
			view.setBackgroundColor(Color.parseColor("#00FF00"));
		}
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

		cameraView.setTopicName(camTopic);
        virtualJoystickView.setTopicName(joyTopic);

        estop = new EStopPublisher();
		nodeMainExecutor.execute(cameraView, nodeConfiguration
				.setNodeName("android/camera_view"));
		nodeMainExecutor.execute(virtualJoystickView,
				nodeConfiguration.setNodeName("android/virtual_joystick"));

		nodeMainExecutor.execute(estop, nodeConfiguration.setNodeName("android/estop"));


		} catch (IOException e) {
            // Socket problem
        }

	}
}
