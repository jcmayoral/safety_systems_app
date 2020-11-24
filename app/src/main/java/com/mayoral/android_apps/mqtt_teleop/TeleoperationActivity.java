package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TeleoperationActivity extends Activity {
	private Button backButton;
	//static EStopPublisher estop;

	private TextView angle_view;
	private TextView strength_view;
	private TextView coordinate_view;
	private TextView rangle_view;
	private TextView rstrength_view;
	private TextView rcoordinate_view;

	JoystickView joystick;
	JoystickView rjoystick;
	//static BluetoothTracker bluetoothtracker;
	MyMqttClient myMqttClient;
	RobotState robot_state;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//setDashboardResource(R.id.top_bar);
		//setMainWindowResource(R.layout.teleoperation);
		// setDefaultAppName("Safety_systems");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teleoperation);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


		angle_view = (TextView) findViewById(R.id.angle_view);
		strength_view = (TextView) findViewById(R.id.strength_view);
		coordinate_view = findViewById(R.id.coordinate_view);
		joystick = (JoystickView) findViewById(R.id.mqtt_joystick);
		joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				angle_view.setText(angle + "°");
				strength_view.setText(strength + "%");
				coordinate_view.setText(
						String.format("x%03d:y%03d:d%03d",
								joystick.getNormalizedX(),
								joystick.getNormalizedY(),
								joystick.getButtonDirection())
				);
			}
		});

		rangle_view = (TextView) findViewById(R.id.angle_view2);
		rstrength_view = (TextView) findViewById(R.id.strength_view2);
		rcoordinate_view = findViewById(R.id.coordinate_view2);
		rjoystick = (JoystickView) findViewById(R.id.mqtt_joystick2);
		rjoystick.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				rangle_view.setText(angle + "°");
				rstrength_view.setText(strength + "%");
				rcoordinate_view.setText(
						String.format("x%03d:y%03d:d%03d",
								rjoystick.getNormalizedX(),
								rjoystick.getNormalizedY(),
								rjoystick.getButtonDirection())
				);
			}
		});

		//virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
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
		//estop.publish();
		robot_state.estop = !robot_state.estop;
		myMqttClient.publishString();
		myMqttClient.publishEStop(robot_state.estop);
		view.setBackgroundColor(selectColor());
	}

	public void bluetoothSwitch(View v){
		Switch sw1 = (Switch)(v.findViewById(R.id.switch1));
		//bluetoothtracker.publish(sw1.isChecked());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MainActivity.setState(robot_state);
	}
}
