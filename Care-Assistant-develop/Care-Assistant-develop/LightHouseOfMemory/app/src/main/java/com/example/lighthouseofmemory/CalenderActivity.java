package com.example.lighthouseofmemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class CalenderActivity extends AppCompatActivity {


    private Button medicineB;
    private Button waterB;
    private ImageButton backButton;
    BottomNavigationView bottomNavigationView;
    View bottomSheetView;

    // 약 알람 목록
    ArrayList<String> items ;
    ArrayAdapter<String> adapter;



    private void showTimePickerBottomSheet() {
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.time_picker_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button amButton = bottomSheetView.findViewById(R.id.buttonAM);
        Button pmButton = bottomSheetView.findViewById(R.id.buttonPM);

        // 요일
        Button mondayButton = bottomSheetView.findViewById(R.id.buttonMonday);
        Button tuesdayButton = bottomSheetView.findViewById(R.id.buttonTuesday);
        Button wedButton = bottomSheetView.findViewById(R.id.buttonWednesday);
        Button thuButton = bottomSheetView.findViewById(R.id.buttonThursday);
        Button friButton = bottomSheetView.findViewById(R.id.buttonFriday);
        Button satButton = bottomSheetView.findViewById(R.id.buttonSaturday);
        Button sunButton = bottomSheetView.findViewById(R.id.buttonSunday);

        // 저장,삭제
        Button saveButton = bottomSheetView.findViewById(R.id.buttonSave);
        Button deleteButton = bottomSheetView.findViewById(R.id.buttonDelete);

        final int[] selectedHour = {1};
        final int[] selectedMinute = {0};
        final boolean[] isAM = {true};
        final ArrayList<String> selectedDays = new ArrayList<>();

        // 오전오후 클릭
        amButton.setOnClickListener(v -> {
            isAM[0] = true;
            amButton.setEnabled(false);
            pmButton.setEnabled(true);
        });

        pmButton.setOnClickListener(v -> {
            isAM[0] = false;
            pmButton.setEnabled(false);
            amButton.setEnabled(true);
        });

        // 요일 클릭
        mondayButton.setOnClickListener(v -> toggleDaySelection("Monday", mondayButton, selectedDays));
        tuesdayButton.setOnClickListener(v -> toggleDaySelection("Tuesday", tuesdayButton, selectedDays));
        wedButton.setOnClickListener(v -> toggleDaySelection("Wednesday", wedButton, selectedDays));
        thuButton.setOnClickListener(v -> toggleDaySelection("Thursday", thuButton, selectedDays));
        friButton.setOnClickListener(v -> toggleDaySelection("Friday", friButton, selectedDays));
        satButton.setOnClickListener(v -> toggleDaySelection("Saturday", satButton, selectedDays));
        sunButton.setOnClickListener(v -> toggleDaySelection("Sunday", sunButton, selectedDays));

        // 저장
        saveButton.setOnClickListener(v -> {
            String time = String.format("%02d:%02d %s", selectedHour[0], selectedMinute[0], isAM[0] ? "AM" : "PM");
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            String alarmDetails = time + " on " + selectedDays;
            items.add(alarmDetails);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "알람이 저장되었습니다. " , Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();

            SharedPreferences sharedPreferences = getSharedPreferences("Alarms", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(items);
            editor.putString("alarm_list", json);
            editor.apply();



            // Logic to save alarm
            // setAlarm(selectedHour[0], selectedMinute[0], isAM[0], selectedDays);
        });

        // 삭제
        deleteButton.setOnClickListener(v -> {
            Toast.makeText(this, "알람이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
            // Logic to delete alarm
        });

        bottomSheetDialog.show();
    }

    private void toggleDaySelection(String day, Button button, ArrayList<String> selectedDays) {
        if (selectedDays.contains(day)) {
            selectedDays.remove(day);
            button.setAlpha(1.0f); // Normal appearance
        } else {
            selectedDays.add(day);
            button.setAlpha(0.5f); // Dim to indicate selection
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        TextView detailTextView = findViewById(R.id.detailTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        backButton = findViewById(R.id.Back_b);
        medicineB = findViewById(R.id.medicine_button);
        waterB = findViewById(R.id.water_button);

        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_gps) {
                    startActivity(new Intent(CalenderActivity.this, GpsActivity.class));
                    return true;
                }
                return false;
            }

        });

        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        // 설정화면으로 이동하는 버튼
        ImageButton Setting_b = findViewById(R.id.Setting_b);
        Setting_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalenderActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        // Load saved alarms
        SharedPreferences sharedPreferences = getSharedPreferences("Alarms", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("alarm_list", null);

        Log.d("CalenderActivity", "Saved JSON: " + json); // Debug log

        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> loadedItems = gson.fromJson(json, type);
            items.addAll(loadedItems); // Add loaded data to the existing list
        }

        // Update adapter with the loaded data
        adapter.notifyDataSetChanged();

        // Set adapter to the ListView
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button addButton = bottomSheetView.findViewById(R.id.addButton);
        ListView listView = bottomSheetView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //약 알림설정 버튼
        medicineB.setOnClickListener(view -> {
            bottomSheetDialog.show();
        });

        // 추가 버튼 눌렀을 때
        addButton.setOnClickListener(v -> {
            showTimePickerBottomSheet();
        });

        Log.d("CalenderActivity", "Items in ListView: " + items.size());






    }
}