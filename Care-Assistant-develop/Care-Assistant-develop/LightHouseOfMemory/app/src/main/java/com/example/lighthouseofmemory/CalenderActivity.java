package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;


public class CalenderActivity extends AppCompatActivity {


    private Button medicineB;
    private Button waterB;
    private ImageButton backButton;
    BottomNavigationView bottomNavigationView;
    View bottomSheetView;

    private void showTimePickerBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.time_picker_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // AM/PM Buttons
        Button amButton = bottomSheetView.findViewById(R.id.buttonAM);
        Button pmButton = bottomSheetView.findViewById(R.id.buttonPM);

        // Day Buttons
        Button mondayButton = bottomSheetView.findViewById(R.id.buttonMonday);
        Button tuesdayButton = bottomSheetView.findViewById(R.id.buttonTuesday);
        // ... Continue for other days

        // Save and Delete Buttons
        Button saveButton = bottomSheetView.findViewById(R.id.buttonSave);
        Button deleteButton = bottomSheetView.findViewById(R.id.buttonDelete);

        final int[] selectedHour = {1};
        final int[] selectedMinute = {0};
        final boolean[] isAM = {true};
        final ArrayList<String> selectedDays = new ArrayList<>();

        // Handle AM/PM selection
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

        // Handle Day Selection
        mondayButton.setOnClickListener(v -> toggleDaySelection("Monday", mondayButton, selectedDays));
        tuesdayButton.setOnClickListener(v -> toggleDaySelection("Tuesday", tuesdayButton, selectedDays));
        // ... Continue for other days

        // Save Button
        saveButton.setOnClickListener(v -> {
            String time = String.format("%02d:%02d %s", selectedHour[0], selectedMinute[0], isAM[0] ? "AM" : "PM");
            Toast.makeText(this, "Saved Alarm: " + time + " on " + selectedDays, Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();

            // Logic to save alarm
            // setAlarm(selectedHour[0], selectedMinute[0], isAM[0], selectedDays);
        });

        // Delete Button
        deleteButton.setOnClickListener(v -> {
            Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_SHORT).show();
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


        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        ListView listView = bottomSheetView.findViewById(R.id.listView);
        Button addButton = bottomSheetView.findViewById(R.id.addButton);



        // 약 추가(+) 버튼
        addButton.setOnClickListener(v -> {
            showTimePickerBottomSheet();
        });

        // 약 알림 설정하기 버튼 눌렀을 때
        medicineB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });




    }
}