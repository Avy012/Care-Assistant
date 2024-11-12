package com.example.lighthouseofmemory;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;
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


    private ArrayList<Integer> selectedDays = new ArrayList<>();

    // Show a dialog to pick days and time
    private void showDayAndTimePicker() {
        // Array of week days
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        // Create a dialog with checkboxes for each day
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Days");

        boolean[] checkedDays = new boolean[7];
        builder.setMultiChoiceItems(days, checkedDays, (dialog, which, isChecked) -> {
            int calendarDay = which + 1; // Calendar days are 1-indexed
            if (isChecked) {
                selectedDays.add(calendarDay); // Add the selected day
            } else {
                selectedDays.remove(Integer.valueOf(calendarDay)); // Remove if unchecked
            }
        });

        builder.setPositiveButton("Next", (dialog, which) -> {
            // Open a time picker after selecting days
            showTimePicker();
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    // Show time picker
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            // After selecting time, set alarms for each selected day
            for (int dayOfWeek : selectedDays) {
                setWeeklyAlarm(dayOfWeek, hourOfDay, minuteOfHour);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    // Set repeating alarm for a specific day and time
    private void setWeeklyAlarm(int dayOfWeek, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust the calendar if the chosen time has already passed for today
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1); // Schedule for the next week
        }

        // Set a repeating alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        Toast.makeText(this, "Alarm set for " + dayOfWeek + " at " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

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

        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        ListView listView = bottomSheetView.findViewById(R.id.listView);
        Button addButton = bottomSheetView.findViewById(R.id.addButton);

        //알람 리스트
        ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);



        // 약 추가(+) 버튼
        addButton.setOnClickListener(v -> {
            items.add("New Item " + (items.size() + 1));
            adapter.notifyDataSetChanged();

            showDayAndTimePicker();
        });

        // 약 알림 설정하기 버튼 눌렀을 때
        medicineB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });

        // CalendarView 설정
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                dateTextView.setText(selectedDate); // 선택된 날짜 표시

                // 해당 날짜에 저장된 일정 데이터 불러오기
                String scheduleTitle = getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                        .getString(selectedDate + "_title", "No Title");
                String scheduleDetail = getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                        .getString(selectedDate + "_detail", "No Details");

                detailTextView.setText("Title: " + scheduleTitle + "\nDetails: " + scheduleDetail);
                Intent intent = new Intent(CalenderActivity.this, ScheduleActivity.class);
                intent.putExtra("selectedDate", selectedDate); // 날짜를 전달
                startActivity(intent);
            }
        });



    }
}