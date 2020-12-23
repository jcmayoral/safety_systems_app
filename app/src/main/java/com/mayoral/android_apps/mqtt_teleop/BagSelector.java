package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class BagSelector extends Activity {
    private ListView listView;
    private Button backButton;
    private static  String[] GENRES = new String[] {
            "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
            "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_selection);
        listView =(ListView)findViewById(R.id.list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.topic_layout, GENRES);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
                if (listView.isItemChecked(position)){
                    listView.setItemChecked(position, false);
                }
                else{
                    listView.setItemChecked(position, true);
                }
                Log.e("Selector", String.valueOf(position));
            }
        });

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
