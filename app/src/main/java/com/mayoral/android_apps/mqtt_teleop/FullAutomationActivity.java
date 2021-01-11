package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FullAutomationActivity extends Activity {

	private Button backButton;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullautomation);
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
		button.setBackgroundColor(MyUtils.selectColor(robot_state.estop));
		myMqttClient.publishCommand("XMode", MyUtils.generateXMODEJSON("FullAutonomous","START"));
	}

	public void pressEStop(View view){
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
		myMqttClient.publishCommand("XMode", MyUtils.generateXMODEJSON("FullAutonomous","STOP"));

	}

}
