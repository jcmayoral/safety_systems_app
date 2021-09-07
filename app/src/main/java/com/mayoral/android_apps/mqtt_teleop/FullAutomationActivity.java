package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class FullAutomationActivity extends Activity {

	private Button backButton;
	MyMqttClient myMqttClient;
	RobotState robot_state;
	int task_id;
	int type_id;

	private TextView pwm_value;
	private SeekBar pwm_bar;

	private TextView pwm_value2;
	private SeekBar pwm_bar2;

	private Button toggleButton;

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


		//TOOL PWM
		pwm_value = (TextView) findViewById(R.id.pwm_1_values);

		pwm_bar = (SeekBar) findViewById(R.id.pwm_1);
		pwm_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (myMqttClient.client == null) {
					return;
				}
				if (!myMqttClient.client.isConnected()){
					return;
				}

				String[] commands = {"PWM"};
				double[] values = {progress};
				myMqttClient.publishCommand("XMOVE", MyUtils.generateNestedCommandsJSON("TOOL",commands, values));
				pwm_value.setText(String.valueOf(progress));

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});


		pwm_value2 = (TextView) findViewById(R.id.pwm_values2);

		pwm_bar2 = (SeekBar) findViewById(R.id.pwm_2);
		pwm_bar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (myMqttClient.client == null) {
					return;
				}
				if (!myMqttClient.client.isConnected()){
					return;
				}

				String[] commands = {"CONVEYOR"};
				double[] values = {progress};
				myMqttClient.publishCommand("XMOVE", MyUtils.generateNestedCommandsJSON("TOOL",commands, values));
				pwm_value2.setText(String.valueOf(progress));

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		toggleButton = (Button) findViewById(R.id.conveyor_button);
		toggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleConveyor(view);
			}
		});;

	}

	public void toggleConveyor(View view){
		if (myMqttClient.client == null) {
			return;
		}
		if (!myMqttClient.client.isConnected()){
			return;
		}

		robot_state.conveyor_state = !robot_state.conveyor_state;

		Log.e("conveyor", String.valueOf(robot_state.conveyor_state));
		String[] commands = {"toggle"};
		double toggle_value = robot_state.conveyor_state ? 1d : 0d;
		double[] values = {Double.valueOf(toggle_value)};
		myMqttClient.publishCommand("XMOVE", MyUtils.generateNestedCommandsJSON("TOOL",commands, values));
		view.setBackgroundColor(MyUtils.selectColor(robot_state.conveyor_state));
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
