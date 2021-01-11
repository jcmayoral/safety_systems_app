package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
	private TextView cutter_angle;
	private TextView cutter_strength;
	private TextView cutter_coordinate;

	JoystickView joystick;
	JoystickView cutterControl;
	//static BluetoothTracker bluetoothtracker;
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

		joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				int x = joystick.getNormalizedX();
				int y = joystick.getNormalizedY();

				angle_view.setText("ERR");
				coordinate_view.setText("ERR");
				strength_view.setText("ERR");

				if ((135 > angle) && (angle > 45)) {
					angle_view.setText("F" + angle + "°");
					coordinate_view.setText(
							String.format("x%03d:y%03d",
									x,
									y));
					strength_view.setText(strength + "%");
				}

				if ((315 > angle) && (angle > 225)) {
					angle_view.setText("B" + angle + "°");
					coordinate_view.setText(
							String.format("x%03d:y%03d",
									x,
									y));
					strength_view.setText(strength + "%");
				}

			}
		});

		cutter_angle = (TextView) findViewById(R.id.cutter_angle);
		cutter_strength = (TextView) findViewById(R.id.cutter_strenght);
		cutter_coordinate = findViewById(R.id.cutter_coordinate);

		cutterControl = (JoystickView) findViewById(R.id.cutter_control);
		cutterControl.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				cutter_angle.setText(angle + "°");
				cutter_strength.setText(strength + "%");
				cutter_coordinate.setText(
						String.format("x%03d:y%03d",
								cutterControl.getNormalizedX(),
								cutterControl.getNormalizedY())
				);
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
		button.setBackgroundColor(selectColor());

		myMqttClient.publishCommand("XMode", "START", "Teleoperation");

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

	int selectColor(){
		if (robot_state.estop){
			return Color.parseColor("#00FF00");
		}
		return Color.parseColor("#FF0000");
	}

	public void pressEStop(View view){
		//estop.publish();
		if (!myMqttClient.isConnected()){
			return;
		}
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishString();
		myMqttClient.publishEStop(robot_state.estop);
		joystick.setEnabled(!robot_state.estop);
		cutterControl.setEnabled(!robot_state.estop);
		view.setBackgroundColor(selectColor());
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
		myMqttClient.publishCommand("XMode", "STOP" , "Teleoperation");
	}


}
