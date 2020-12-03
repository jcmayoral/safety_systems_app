package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FullAutomationActivity extends Activity {

	private Button backButton;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	//static EStopPublisher estop;

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
		button.setBackgroundColor(selectColor());
		myMqttClient.publishCommand("XMode", "START", "FullAutonomous");
	}

	int selectColor(){
		if (robot_state.estop){
			return Color.parseColor("#00FF00");
		}
		return Color.parseColor("#FF0000");
	}

	public void pressEStop(View view){
		if (!myMqttClient.isConnectionDone()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(selectColor());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
		myMqttClient.publishCommand("XMode", "STOP", "FullAutonomous");

	}

}
