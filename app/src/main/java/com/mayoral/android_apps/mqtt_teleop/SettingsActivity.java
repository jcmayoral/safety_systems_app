package com.mayoral.android_apps.mqtt_teleop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

public class SettingsActivity extends Activity {

    MyMqttClient myMqttClient;
    Button backButton;
    ToggleButton ConnStatusButton;
    String ipAddress;
    int port;
    EditText ipaddressText;
    EditText portText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setMyMqttClient(myMqttClient);
                onBackPressed();
                //finish();
            }
        });

        ipaddressText = (EditText)findViewById(R.id.ip_address);
        portText = (EditText)findViewById(R.id.port);


        /*
        if (savedInstanceState != null){
            ipaddressText.setText(savedInstanceState.getString("ip"));
            portText.setText(String.valueOf(savedInstanceState.getInt("port")));
        }
        */

        ipaddressText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0)
                    ipAddress = s.toString();
            }
        });

        portText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() != 0)
                    port = Integer.valueOf(s.toString());

            }
        });

        myMqttClient = MainActivity.getMyMqttClient();
        ipaddressText.setText(myMqttClient.getHost());
        portText.setText(String.valueOf(myMqttClient.getPort()));

        ConnStatusButton = findViewById(R.id.togglebutton);
        ConnStatusButton.setBackgroundColor(selectColor(1));
        Log.e("Create", "aaaa");
    }

    public void onClickConnect(View view) {
        boolean result = myMqttClient.run(getApplicationContext(), ipAddress, port);
        Log.e("settings", String.valueOf(result));
        int val = result? 1 : 0;
        ConnStatusButton.setBackgroundColor(selectColor(val));
    }

    int selectColor(int status){
        switch (status){
            case 0:   return Color.parseColor("#00FF00");
            //case 1:   return Color.parseColor("#00FF00");
            case 1:   return Color.parseColor("#FF0000");
            default:   return Color.parseColor("#FF0000");

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ipaddressText.setText(myMqttClient.getHost());
        portText.setText(String.valueOf(myMqttClient.getPort()));
        Log.e("onresume", "resumming settings");

    }

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("save", "onsaved instance");
        outState.putString("ip", ipAddress);
        outState.putInt("port", port);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("restore", "on restore instance");
        ipaddressText.setText(savedInstanceState.getString("ip"));
        portText.setText(String.valueOf(savedInstanceState.getInt("port")));
    }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
