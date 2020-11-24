package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FullAutonomationActivity extends Activity {

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
