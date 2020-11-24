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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.ros.android.view.RosImageView;

import sensor_msgs.CompressedImage;

/**
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class FullAutonomationActivity extends Activity {
	private RosImageView<CompressedImage> cameraView;
	private Button backButton;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	//static EStopPublisher estop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//setDashboardResource(R.id.top_bar);
		//setMainWindowResource(R.layout.fullautomation);
		//gma
		// setDefaultAppName("Safety_systems");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullautomation);
        //setContentView(R.layout.mode_selector);
		//cameraView = (RosImageView<CompressedImage>) findViewById(R.id.image);
        //cameraView.setMessageType(CompressedImage._TYPE);
        //cameraView.setMessageToBitmapCallable(new BitmapFromCompressedImage());
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
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(selectColor());
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
	}

}
