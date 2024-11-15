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

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class CalendarActivity extends AppCompatActivity {

    TextView detailTextView;
    TextView dateTextView;
    ImageButton Back_b;
    ImageButton Setting_b;
    Button Edit_b;
    String selectedDate= "";

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



        Edit_b = findViewById(R.id.edit_b); // Edit 버튼을 누르면 ScheduleActivity로 이동
        // Edit 버튼 클릭 시 ScheduleActivity로 이동
        Edit_b.setOnClickListener(v -> {
            if (selectedDate != null) {
                Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                intent.putExtra("SELECTED_DATE", selectedDate); // 선택된 날짜 전달
                startActivity(intent);
            }
        });

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
            dateTextView.setText(selectedDate); // 선택된 날짜 표시
            loadScheduleForSelectedDate(selectedDate); // selectedDate를 사용
        });

    }
    private void loadScheduleForSelectedDate(String selectedDate) {
        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);

        // 저장된 일정 제목과 상세내용 가져오기
        String scheduleTitle = sharedPreferences.getString(selectedDate + "_title", "No title");
        String scheduleDetail = sharedPreferences.getString(selectedDate + "_detail", "No details");

        // TextView에 설정
        detailTextView.setText(scheduleDetail);
        dateTextView.setText(scheduleTitle);
    }
}