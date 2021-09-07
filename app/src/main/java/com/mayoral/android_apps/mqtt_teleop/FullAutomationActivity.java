package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FullAutomationActivity extends Activity {

	private Button backButton;
	MyMqttClient myMqttClient;
	RobotState robot_state;
	int task_id;
	int type_id;

	public enum EXECUTION_TYPE{
		DRY,
		NORMAL
	};

	public enum TASKS{
			CUT,
			COLLECT
	};

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
		myMqttClient.publishCommand("XMode", MyUtils.generateXMsg("FullAutonomous","START"));
	}

	public void pressEStop(View view){
		if (!myMqttClient.client.isConnected()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(MyUtils.selectColor(robot_state.estop));
	}

	public void starRun(View view){
		if (!myMqttClient.client.isConnected()){
			return;
		}
		myMqttClient.publishCommand("XECUTE", MyUtils.generateXMsg("FullAutonomous","START"));
		view.setBackgroundColor(MyUtils.selectColor(true));
	}

	public void stopRun(View view){
		if (!myMqttClient.client.isConnected()){
			return;
		}
		myMqttClient.publishCommand("XECUTE", MyUtils.generateXMsg("FullAutonomous","STOP"));
		view.setBackgroundColor(MyUtils.selectColor(true));
	}

	public void unload(View view){
		if (!myMqttClient.client.isConnected()){
			return;
		}
		myMqttClient.publishCommand("XECUTE", MyUtils.generateXMsg("FullAutonomous","GO_UNLOAD"));
		view.setBackgroundColor(MyUtils.selectColor(true));
	}

	public void resumeRun(View view){
		if (!myMqttClient.client.isConnected()){
			return;
		}
		myMqttClient.publishCommand("XECUTE", MyUtils.generateXMsg("FullAutonomous","RESUME"));
		view.setBackgroundColor(MyUtils.selectColor(true));
	}


	public void startExecution(View view){
		String[] commands = {"TASK", "TYPE"};
		double[] values = {task_id, type_id};
		myMqttClient.publishCommand("XCUTE", MyUtils.generateNestedCommandsJSON("",commands, values));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
		myMqttClient.publishCommand("XMode", MyUtils.generateXMsg("FullAutonomous","STOP"));

	}

}
