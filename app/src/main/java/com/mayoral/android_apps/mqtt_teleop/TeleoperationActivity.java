package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TeleoperationActivity extends Activity {
	private Button backButton;
	//static EStopPublisher estop;
	Button configButton;

	private TextView angle_view;
	private TextView strength_view;
	private TextView coordinate_view;
	private TextView pwm_value;
	private SeekBar pwm_bar;

	private TextView pwm_value2;
	private SeekBar pwm_bar2;

	JoystickView joystick;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teleoperation);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		angle_view = (TextView) findViewById(R.id.angle_view);
		strength_view = (TextView) findViewById(R.id.strength_view);
		coordinate_view = findViewById(R.id.coordinate_view);
		joystick = (JoystickView) findViewById(R.id.robot_control);

		pwm_value = (TextView) findViewById(R.id.pwm_values);

		pwm_bar = (SeekBar) findViewById(R.id.pwm_seekbar);
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

		pwm_bar2 = (SeekBar) findViewById(R.id.pwm_seekbar2);
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


		joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				int x = joystick.getNormalizedX();
				int y = joystick.getNormalizedY();

				angle_view.setText("ERR");
				coordinate_view.setText("ERR");
				strength_view.setText("ERR");

				int angle_cmd = 0;
				int linear_cmd = 0;

				//if ((135 > angle) && (angle > 45)) {
					angle_view.setText("F" + angle + "°");
					coordinate_view.setText(
							String.format("x%03d:y%03d",
									x,
									y));
					strength_view.setText(strength + "%");
					angle_cmd = angle;
					linear_cmd = strength;
				//}

				/*if ((315 > angle) && (angle > 225)) {
					angle_view.setText("B" + angle + "°");
					coordinate_view.setText(
							String.format("x%03d:y%03d",
									x,
									y));
					strength_view.setText(strength + "%");
					angle_cmd = angle;
					linear_cmd = strength;
				}*/
				angle_cmd = angle;
				linear_cmd = strength;

				String[] commands = {"linear", "Steering"};
				double[] speeds = {linear_cmd, angle_cmd};
				myMqttClient.publishCommand("XMOVE", MyUtils.generateNestedCommandsJSON("BASE",commands, speeds));

			}
		});

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

		myMqttClient.publishCommand("XMode", MyUtils.generateXMODEJSON("Teleoperation","START"));

		configButton = (Button) findViewById(R.id.config_recording);
		configButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), BagSelector.class);
				startActivity(intent);
			}
		});
		Log.e("OOOO", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");


	}

	public void pressEStop(View view){
		//estop.publish();
		if (myMqttClient.client == null) {
			return;
		}
		if (!myMqttClient.client.isConnected()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishEStop(robot_state.estop);
		joystick.setEnabled(!robot_state.estop);
		view.setBackgroundColor(MyUtils.selectColor(robot_state.estop));
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

	public void bluetoothSwitch(View v){
		Switch sw1 = (Switch)(v.findViewById(R.id.switch1));
		//bluetoothtracker.publish(sw1.isChecked());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
		myMqttClient.publishCommand("XMode", MyUtils.generateXMODEJSON("Teleoperation","STOP"));
	}


}
