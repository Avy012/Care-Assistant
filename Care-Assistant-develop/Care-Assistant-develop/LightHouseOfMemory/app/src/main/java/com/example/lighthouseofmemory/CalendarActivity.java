package com.example.lighthouseofmemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    TextView detailTextView;
    TextView dateTextView;
    ImageButton Back_b;
    ImageButton Setting_b;
    CalendarView calendarView;
    Button Edit_b;
    String selectedDate;

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
                Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
                dateTextView.setText(selectedDate); // 선택된 날짜 표시

                // SharedPreferences에서 저장된 일정 데이터와 색상 불러오기
                SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);
                String scheduleTitle = sharedPreferences.getString(selectedDate + "_title", "No Title");
                String scheduleDetail = sharedPreferences.getString(selectedDate + "_detail", "No Details");
                int dateColor = sharedPreferences.getInt(selectedDate + "_color", Color.BLACK); // 기본 색상 설정

                detailTextView.setText("제목: " + scheduleTitle + "\n상세내용: " + scheduleDetail);
                dateTextView.setTextColor(dateColor); // 선택된 날짜 텍스트에 색상 적용
            }
        });

        Edit_b = findViewById(R.id.edit_b); // Edit 버튼을 누르면 ScheduleActivity로 이동
        Edit_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                    intent.putExtra("SELECTED_DATE", selectedDate); // 선택된 날짜 전달
                    startActivity(intent);
                }
            }
        });
    }
}