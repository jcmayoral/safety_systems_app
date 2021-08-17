
package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
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
		myMqttClient.publishCommand("XMode", MyUtils.generateXMsg("SemiAutonomous","START"));

		Button button = (Button) findViewById(R.id.button);
		button.setBackgroundColor(MyUtils.selectColor(robot_state.estop));
	}

	public void pressEStop(View view) {
		if (!myMqttClient.client.isConnected()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(MyUtils.selectColor(robot_state.estop));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
		myMqttClient.publishCommand("XMode", MyUtils.generateXMsg("SemiAutonomous","STOP"));
	}
}
