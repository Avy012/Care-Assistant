package com.example.lighthouseofmemory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {

    private TextView dateTextView, symptomTextView;
    private EditText schedule_detail, schedule_title;
    private Button saveButton;
    private Button colorPickerButton;
    private LinearLayout colorList;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        dateTextView = findViewById(R.id.dateTextView);
        schedule_detail = findViewById(R.id.schedule_detail);
        saveButton = findViewById(R.id.saveButton);
        schedule_title = findViewById(R.id.schedule_title);
        symptomTextView = findViewById(R.id.symptomTextView);

        ImageButton Back_b = findViewById(R.id.Back_b); // 뒤로 되돌아가는 버튼
        Back_b.setOnClickListener(v -> finish());

        ImageButton Setting_b = findViewById(R.id.Setting_b); // 설정화면으로 이동하는 버튼
        Setting_b.setOnClickListener(view -> {
            Intent intent = new Intent(ScheduleActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        colorPickerButton = findViewById(R.id.colorPickerButton);
        colorList = findViewById(R.id.colorList);
        colorList.setVisibility(View.GONE); // Start hidden

        colorPickerButton.setOnClickListener(v -> {
            if (colorList.getVisibility() == View.GONE) {
                colorList.setVisibility(View.VISIBLE);
            } else {
                colorList.setVisibility(View.GONE);
            }
        });

        // Assign color selections
        findViewById(R.id.colorOption1).setOnClickListener(v -> setColorSelection(getResources().getColor(R.color.light_blue)));
        findViewById(R.id.colorOption2).setOnClickListener(v -> setColorSelection(getResources().getColor(R.color.light_green)));
        findViewById(R.id.colorOption3).setOnClickListener(v -> setColorSelection(getResources().getColor(R.color.light_yellow)));

        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        dateTextView.setText(selectedDate); // 선택된 날짜 표시

        loadSavedData(selectedDate); // 저장된 데이터 로드

        saveButton.setOnClickListener(v -> {
            saveScheduleData(selectedDate); // 일정 데이터 저장
            navigateToCalendarActivity(selectedDate); // CalendarActivity로 이동
        });

        Button symptomButton = findViewById(R.id.Check_symptom);
        symptomButton.setOnClickListener(v -> showSymptomDialog(selectedDate));
    }

    private void saveScheduleData(String selectedDate) {
        String scheduleTitle = schedule_title.getText().toString().trim();
        String scheduleDetail = schedule_detail.getText().toString().trim();
        String symptoms = symptomTextView.getText().toString();

        // 입력값 검증
        if (scheduleTitle.isEmpty() || scheduleDetail.isEmpty()) {
            new AlertDialog.Builder(ScheduleActivity.this)
                    .setTitle("입력 오류")
                    .setMessage("제목과 세부 내용을 모두 입력해주세요.")
                    .setPositiveButton("확인", null)
                    .show();
            return;
        }

        // SharedPreferences에 데이터 저장
        SharedPreferences.Editor editor = getSharedPreferences("SchedulePreferences", MODE_PRIVATE).edit();
        editor.putString(selectedDate + "_title", scheduleTitle);
        editor.putString(selectedDate + "_detail", scheduleDetail);
        editor.putString(selectedDate + "_symptoms", symptoms);
        editor.putInt(selectedDate + "_color", selectedColor);
        editor.apply();
    }

    private void loadSavedData(String selectedDate) {
        SharedPreferences preferences = getSharedPreferences("SchedulePreferences", MODE_PRIVATE);
        String savedTitle = preferences.getString(selectedDate + "_title", "");
        String savedDetail = preferences.getString(selectedDate + "_detail", "");
        String savedSymptoms = preferences.getString(selectedDate + "_symptoms", "");
        int savedColor = preferences.getInt(selectedDate + "_color", Color.WHITE);

        schedule_title.setText(savedTitle);
        schedule_detail.setText(savedDetail);
        symptomTextView.setText(savedSymptoms);
        selectedColor = savedColor;
        updateColorPickerButtonUI(savedColor);
    }

    private void navigateToCalendarActivity(String selectedDate) {
        Intent intent = new Intent(ScheduleActivity.this, CalendarActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("scheduleTitle", schedule_title.getText().toString());
        intent.putExtra("scheduleDetail", schedule_detail.getText().toString());
        intent.putExtra("selectedColor", selectedColor);
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }

    private void showSymptomDialog(String selectedDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
        builder.setTitle("< 증상 기록 >");

        LinearLayout layout = new LinearLayout(ScheduleActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        CheckBox[] symptoms = {
                createSymptomCheckBox("언어 장애"),
                createSymptomCheckBox("성격 변화"),
                createSymptomCheckBox("인지 장애"),
                createSymptomCheckBox("식사 거부"),
                createSymptomCheckBox("지남력 상실"),
                createSymptomCheckBox("일상 과업 수행 곤란"),
                createSymptomCheckBox("파괴적 행동"),
                createSymptomCheckBox("우울감")
        };

        for (CheckBox symptom : symptoms) layout.addView(symptom);

        builder.setView(layout);
        builder.setPositiveButton("확인", (dialog, which) -> {
            StringBuilder selectedSymptoms = new StringBuilder("< 선택된 증상 >\n ");
            for (CheckBox symptom : symptoms) {
                if (symptom.isChecked()) {
                    selectedSymptoms.append("- ").append(symptom.getText()).append("\n");
                }
            }
            symptomTextView.setText(selectedSymptoms.toString());
            saveSymptomsToPreferences(selectedDate, selectedSymptoms.toString());
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private CheckBox createSymptomCheckBox(String text) {
        CheckBox checkBox = new CheckBox(ScheduleActivity.this);
        checkBox.setText(text);
        return checkBox;
    }

    private void saveSymptomsToPreferences(String selectedDate, String symptoms) {
        getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                .edit()
                .putString(selectedDate + "_symptoms", symptoms)
                .apply();
    }

    private void setColorSelection(int color) {
        selectedColor = color;
        colorList.setVisibility(View.GONE); // 색상 목록 숨기기
        updateColorPickerButtonUI(color);
    }

    private void updateColorPickerButtonUI(int color) {
        if (color == getResources().getColor(R.color.light_blue)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_blue, 0, 0, 0);
        } else if (color == getResources().getColor(R.color.light_green)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_green, 0, 0, 0);
        } else if (color == getResources().getColor(R.color.light_yellow)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_yellow, 0, 0, 0);
        } else {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.white, 0, 0, 0);
        }
    }
}
