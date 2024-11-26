package com.example.lighthouseofmemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {

    private HashMap<String, Integer> dateColorMap = new HashMap<>();
    TextView detailTextView;
    TextView dateTextView;
    ImageButton Back_b;
    ImageButton Setting_b;
    Button Edit_b,Delete_b;
    String selectedDate= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender2);

        detailTextView = findViewById(R.id.detailTextView);
        dateTextView = findViewById(R.id.dateTextView);

        Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(intent);
        });

        Setting_b = findViewById(R.id.Setting_b); // 설정화면으로 이동하는 버튼
        Setting_b.setOnClickListener(view -> {
            Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        Edit_b = findViewById(R.id.edit_b); // Edit 버튼을 누르면 ScheduleActivity로 이동
        Edit_b.setOnClickListener(v -> {
            if (selectedDate != null) {
                Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                intent.putExtra("SELECTED_DATE", selectedDate); // 선택된 날짜 전달
                startActivity(intent);
            }
        });


        Delete_b = findViewById(R.id.delete_b); // Delete 버튼을 눌러 데이터 삭제
        Delete_b.setOnClickListener(v -> {
            deleteScheduleForSelectedDate(selectedDate);
        });

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionColor(Color.parseColor("#65558F"));

        // 현재 날짜 가져오기
        CalendarDay today = CalendarDay.today();
        selectedDate = today.getYear() + "-" + (today.getMonth() + 1) + "-" + today.getDay();

        loadSavedColors();

        // 데코레이터를 업데이트
        updateCalendarDecorators();


        // 현재 날짜를 선택된 상태로 설정
        calendarView.setDateSelected(today, true);

        // 현재 날짜 표시
        dateTextView.setText(selectedDate);
        loadScheduleForSelectedDate(selectedDate);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
            dateTextView.setText(selectedDate); // 선택된 날짜 표시
            loadScheduleForSelectedDate(selectedDate); // selectedDate를 사용
        });
    }
    // 저장된 색상 데이터를 불러오는 메서드
    private void loadSavedColors() {
        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);

        // 저장된 날짜-색상 데이터를 HashMap으로 불러오기
        for (String key : sharedPreferences.getAll().keySet()) {
            if (key.endsWith("_color")) { // 색상 키 확인
                String date = key.replace("_color", ""); // 날짜 추출
                int color = sharedPreferences.getInt(key, Color.TRANSPARENT); // 색상 값 불러오기
                dateColorMap.put(date, color);
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String updatedDate = intent.getStringExtra("selectedDate");
        int updatedColor = intent.getIntExtra("selectedColor", Color.TRANSPARENT);

        if (updatedDate != null) {
            dateColorMap.put(updatedDate, updatedColor);
            updateCalendarDecorators();
        }
    }
    private void updateCalendarDecorators() {
        MaterialCalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.removeDecorators(); // 이전 데코레이터 제거

        // 데코레이터 추가
        for (String date : dateColorMap.keySet()) {
            int color = dateColorMap.get(date);

            calendarView.addDecorator(new DayViewDecorator() {
                @Override
                public boolean shouldDecorate(CalendarDay day) {
                    // 날짜 문자열 형식으로 변환
                    String dayString = day.getYear() + "-" + (day.getMonth() + 1) + "-" + day.getDay();
                    return date.equals(dayString);
                }

                @Override
                public void decorate(DayViewFacade view) {
                    // 동그란 배경 추가
                    view.setSelectionDrawable(createCircleDrawable(color));
                }
            });
        }
    }

    // 동그란 배경 drawable 생성
    private Drawable createCircleDrawable(int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        drawable.setIntrinsicWidth(50);  // 크기 조정 (필요시 변경)
        drawable.setIntrinsicHeight(50);
        return drawable;
    }

    private void deleteScheduleForSelectedDate(String selectedDate) {
        if (selectedDate == null || selectedDate.isEmpty()) return;

        SharedPreferences sharedPreferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 선택된 날짜와 관련된 일정 데이터 삭제
        editor.remove(selectedDate + "_title");
        editor.remove(selectedDate + "_detail");
        editor.remove(selectedDate + "_color");

        // 선택된 날짜와 관련된 증상 기록 데이터 삭제
        editor.remove(selectedDate + "_symptom"); // 증상 기록 키 (예시)

        editor.apply();

        // HashMap에서도 삭제
        dateColorMap.remove(selectedDate);

        // UI 업데이트
        detailTextView.setText("No details");
        dateTextView.setText("No title");

        updateCalendarDecorators();
    }

}