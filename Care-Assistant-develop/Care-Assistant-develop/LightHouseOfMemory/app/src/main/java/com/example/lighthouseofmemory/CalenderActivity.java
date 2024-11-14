package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalenderActivity extends AppCompatActivity {

    TextView detailTextView;
    TextView dateTextView;
    ImageButton Back_b;
    ImageButton Setting_b;
    CalendarView calendarView;
    Button Edit_b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        detailTextView = findViewById(R.id.detailTextView);
        dateTextView = findViewById(R.id.dateTextView);

        Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Setting_b = findViewById(R.id.Setting_b); // 설정화면으로 이동하는 버튼
        Setting_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalenderActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // CalendarView, 날짜를 클릭하면 일정표에
        calendarView = findViewById(R.id.calendarView);
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

    Edit_b = findViewById(R.id.edit_b); //이 버튼을 누르면 클릭한 날짜에 해당하는 scheduleActivity로 이동


    }
}