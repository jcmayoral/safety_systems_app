package com.mayoral.android_apps.mqtt_teleop;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BagSelector extends Activity {
    private ListView listView;
    private Button backButton;
    private Button saveBagButton;
    static public List<Boolean> selectedTopics;
    private static  String[] GENRES = new String[] {
            "Action", "Adventure", "Animation", "Children", "Comedy", "Documentary", "Drama",
            "Foreign", "History", "Independent", "Romance", "Sci-Fi", "Television", "Thriller", "BLABLA"
    };

    MyMqttClient myMqttClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topics_selection);
        listView =(ListView)findViewById(R.id.list);

        myMqttClient = MainActivity.getMyMqttClient();


        /*
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        */

        saveBagButton = (Button) findViewById(R.id.save_bag_button);
        saveBagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result= "";
                Iterator<Boolean> iter = selectedTopics.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    boolean b = (boolean)iter.next();
                    Log.e("sending", "ONSAVEBAGCONFIG CB" + String.valueOf(b));

                    if (b){
                        result = result.concat(":"+ i);
                    }
                    i++;
                    Log.e("partial result", result);

                }
                Log.e("sending", result);
            }
        });

        selectedTopics = new ArrayList<Boolean>();
        initList();
    }

    void initList(){
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.topic_layout, GENRES);
        //Resize List to length of number of topics
        selectedTopics = new ArrayList<Boolean>();
        for (int i =0; i< GENRES.length; i++) {
            selectedTopics.add(false);
        }

        Log.e("INIT", String.valueOf(selectedTopics.size()));

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listView.isItemChecked(position)){
                    listView.setItemChecked(position, true);
                    selectedTopics.set(position,true);
                }
                else{
                    listView.setItemChecked(position, false);
                    selectedTopics.set(position,true);
                }
                Log.e("Selector", String.valueOf(position));
            }
        });

    }
}
