
package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SemiAutomationActivity extends Activity {
	private Button backButton;
	//static EStopPublisher estop;
	RobotState robot_state;
	MyMqttClient myMqttClient;
	static BluetoothTracker bluetoothtracker;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.semiautomation);
	   	backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

		myMqttClient = MainActivity.getMyMqttClient();
		robot_state = MainActivity.getState();
		myMqttClient.publishCommand("XMode", "START", "SemiAutonomous");

		Button button = (Button) findViewById(R.id.button);
		button.setBackgroundColor(selectColor());
	}

	public void pressEStop(View view) {
		if (!myMqttClient.client.isConnected()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishString();
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(selectColor());
	}

	int selectColor(){
		if (robot_state.estop){
			return Color.parseColor("#00FF00");
		}
		return Color.parseColor("#FF0000");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
		myMqttClient.publishCommand("XMode", "STOP", "SemiAutonomous");
	}
}
