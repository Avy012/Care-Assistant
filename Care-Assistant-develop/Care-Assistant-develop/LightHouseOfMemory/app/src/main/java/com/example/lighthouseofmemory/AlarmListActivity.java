package com.example.lighthouseofmemory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ArrayList<Alarm> alarmList;
    private ImageButton Back_b;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(v -> onBackPressed());
        clearButton = findViewById(R.id.clearButton); // 지우기 버튼

        alarmListView = findViewById(R.id.alarmListView);

        // R데이터 저장
        SharedPreferences sharedPreferences = getSharedPreferences("TriggeredAlarms", MODE_PRIVATE);
        String json = sharedPreferences.getString("alarm_list", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Alarm>>() {}.getType();
        alarmList = gson.fromJson(json, listType);


        Log.d("AlarmListActivity", "Loaded alarms: " + alarmList);

        // 포맷변경
        ArrayList<String> displayList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            if (alarm != null) {
                String logMessage = alarm.getTitle() + ", " +
                        android.text.format.DateFormat.format("MM.dd, hh:mm a", alarm.getTimestamp());
                if (alarm.getAmount() > 0) {
                    logMessage += ", " + alarm.getAmount() + " ml";
                }
                displayList.add(logMessage);
            }
        }


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        alarmListView.setAdapter(adapter);

        clearButton.setOnClickListener(v -> {
            // 리스트 지움
            alarmList.clear();
            displayList.clear();

            // 데이터 업뎃
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("alarm_list", "[]"); // Save an empty list
            editor.apply();

            // 적용
            adapter.notifyDataSetChanged();

            Log.d("AlarmListActivity", "Alarm list cleared.");
        });




    }
}

