package com.example.lighthouseofmemory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity {
    private ListView alarmListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> alarmList;
    private ImageButton Back_b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(v -> onBackPressed());

        alarmListView = findViewById(R.id.alarmListView);

        SharedPreferences sharedPreferences = getSharedPreferences("TriggeredAlarms", MODE_PRIVATE);
        String json = sharedPreferences.getString("alarm_list", "[]");  // Default to an empty list if not found

        Log.d("Loading Data", "JSON: " + json);  // Log the JSON string to check if it was saved correctly

        Gson gson = new Gson();

// Deserialize the JSON string into a List<Alarm> object
        Type listType = new TypeToken<ArrayList<Alarm>>() {}.getType();
        ArrayList<Alarm> alarmList = gson.fromJson(json, listType);

// Check the list size
        Log.d("Loaded Data", "List Size: " + alarmList.size());

        if (alarmList.isEmpty()) {
            Log.d("Loaded Data", "List is empty!");
        } else {
            for (Alarm alarm : alarmList) {
                Log.d("Loaded Alarm", "Title: " + alarm.getTitle() + ", Timestamp: " + alarm.getTimestamp());
            }
        }



        //alarmListView.setAdapter(adapter);




    }
}

