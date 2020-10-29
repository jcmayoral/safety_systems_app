package com.example.grassrobotics.ui.interfaces;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.grassrobotics.R;

import java.util.ArrayList;

public class InterfaceActivity extends AppCompatActivity {
    ArrayList<String> mode_selection = new ArrayList<String>();
    TextView finalText;

    public void selectMode(View View){
        String final_mode_selection = " ";
        for (String Selections: mode_selection){
            final_mode_selection = final_mode_selection + Selections +"\n";
        }
        finalText.setText(final_mode_selection);
        finalText.setEnabled(true);
    }

    public void selectOption(View view){
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.bluetoothcheckbox:
                if(checked) {
                    mode_selection.add("Bluetooth");
                }
                else{
                    mode_selection.remove("Bluetooth");
                }
                break;
            case R.id.full_teleoperation:
                if(checked) {
                    mode_selection.add("Bluetooth");
                }
                else{
                    mode_selection.remove("Bluetooth");
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_interface);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        finalText = (TextView) findViewById(R.id.text_interfaces);
        finalText.setEnabled(false);
    }
}