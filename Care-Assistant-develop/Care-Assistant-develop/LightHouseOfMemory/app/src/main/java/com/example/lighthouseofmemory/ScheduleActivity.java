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


    private TextView dateTextView,symptomTextView;
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

        Back_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton Setting_b = findViewById(R.id.Setting_b); // 설정화면으로 이동하는 버튼
        Setting_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        colorPickerButton = findViewById(R.id.colorPickerButton);
        colorList = findViewById(R.id.colorList);
        colorList.setVisibility(View.GONE); // Start hidden

        colorPickerButton.setOnClickListener(v -> {
            // Toggle visibility of color list
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



        // saveButton 클릭 리스너에 이 함수를 연결


        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        dateTextView.setText(selectedDate); // 선택된 날짜 표시

        // ScheduleActivity에서 저장 버튼 클릭 시
        saveButton.setOnClickListener(v -> {
            String scheduleTitle = schedule_title.getText().toString().trim();
            String scheduleDetail = schedule_detail.getText().toString().trim();
            String selectDate = dateTextView.getText().toString();

            // 입력값 검증: 제목이나 내용이 비어있을 경우 사용자에게 알림
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
            editor.putString(selectDate + "_title", scheduleTitle);
            editor.putString(selectDate + "_detail", scheduleDetail);
            editor.putInt(selectDate + "_color", selectedColor); // 선택한 색상 저장
            editor.apply();

            // CalendarActivity로 데이터를 전달하기 위한 Intent 생성
            Intent intent = new Intent(ScheduleActivity.this, CalendarActivity.class);
            intent.putExtra("selectedDate", selectDate); // 선택된 날짜
            intent.putExtra("scheduleTitle", scheduleTitle); // 일정 제목
            intent.putExtra("scheduleDetail", scheduleDetail); // 일정 세부 사항
            intent.putExtra("selectedColor", selectedColor); // 선택된 색상

            // CalendarActivity로 이동
            startActivity(intent);
            finish(); // 현재 Activity 종료
        });

        Button symptomButton = findViewById(R.id.Check_symptom);
        // 날짜에 맞는 저장된 증상 불러오기
        String savedSymptoms = loadSymptomsFromPreferences(selectedDate);
        symptomTextView.setText(savedSymptoms);


        symptomButton.setOnClickListener(v -> {
            // 다이얼로그를 생성합니다.
            AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
            builder.setTitle("< 증상 기록 >");

            // 체크박스가 포함된 레이아웃을 생성합니다.
            LinearLayout layout = new LinearLayout(ScheduleActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            // 증상 항목에 대한 체크박스를 생성합니다.
            CheckBox symptom1 = new CheckBox(ScheduleActivity.this);
            symptom1.setText("언어 장애");

            CheckBox symptom2 = new CheckBox(ScheduleActivity.this);
            symptom2.setText("성격 변화");

            CheckBox symptom3 = new CheckBox(ScheduleActivity.this);
            symptom3.setText("인지 장애");

            CheckBox symptom4 = new CheckBox(ScheduleActivity.this);
            symptom4.setText("식사 거부");

            CheckBox symptom5 = new CheckBox(ScheduleActivity.this);
            symptom5.setText("지남력 상실");

            CheckBox symptom6 = new CheckBox(ScheduleActivity.this);
            symptom6.setText("일상 과업 수행 곤란");

            CheckBox symptom7 = new CheckBox(ScheduleActivity.this);
            symptom7.setText("파괴적 행동");

            CheckBox symptom8 = new CheckBox(ScheduleActivity.this);
            symptom8.setText("우울감");
            // 체크박스를 레이아웃에 추가합니다.
            layout.addView(symptom1);
            layout.addView(symptom2);
            layout.addView(symptom3);
            layout.addView(symptom4);
            layout.addView(symptom5);
            layout.addView(symptom6);
            layout.addView(symptom7);
            layout.addView(symptom8);

            // 다이얼로그에 레이아웃을 설정합니다.
            builder.setView(layout);

            // 확인 및 취소 버튼을 설정합니다.
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 확인 버튼을 클릭했을 때 선택된 증상 처리 로직 추가
                    StringBuilder selectedSymptoms = new StringBuilder("< 선택된 증상 >\n ");
                    if (symptom1.isChecked()) selectedSymptoms.append("- 언어 장애\n");
                    if (symptom2.isChecked()) selectedSymptoms.append("- 성격 변화\n");
                    if (symptom3.isChecked()) selectedSymptoms.append("- 인지 장애\n");
                    if (symptom4.isChecked()) selectedSymptoms.append("- 식사 거부\n");
                    if (symptom5.isChecked()) selectedSymptoms.append("- 지남력 상실\n");
                    if (symptom6.isChecked()) selectedSymptoms.append("- 일상 과업 수행 논란\n");
                    if (symptom7.isChecked()) selectedSymptoms.append("- 파괴적 행동\n");
                    if (symptom8.isChecked()) selectedSymptoms.append("- 우울감");
                    symptomTextView.setText(selectedSymptoms.toString());
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // 다이얼로그를 보여줍니다.
            AlertDialog dialog = builder.create();
            dialog.show();
        });


    }
    private void saveSymptomsToPreferences(String selectedDate, String symptoms) {
        // 증상을 SharedPreferences에 저장
        getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                .edit()
                .putString(selectedDate + "_symptoms", symptoms)
                .apply();
    }

    private String loadSymptomsFromPreferences(String selectedDate) {
        // SharedPreferences에서 증상 불러오기
        return getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                .getString(selectedDate + "_symptoms", "");
    }

    private void saveSelectedColorToPreferences(String selectedDate) {
        // 선택한 날짜와 색상을 SharedPreferences에 저장합니다.
        getSharedPreferences("SchedulePreferences", MODE_PRIVATE)
                .edit()
                .putInt(selectedDate + "_color", selectedColor)
                .apply();
    }

    private void setColorSelection(int color) {
        selectedColor = color;
        colorList.setVisibility(View.GONE); // 색상 목록 숨기기

        // 선택한 색상에 따라 버튼 아이콘 변경
        if (color == getResources().getColor(R.color.light_blue)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_blue, 0, 0, 0);
        } else if (color == getResources().getColor(R.color.light_green)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_green, 0, 0, 0);
        } else if (color == getResources().getColor(R.color.light_yellow)) {
            colorPickerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.light_yellow, 0, 0, 0);
        }
    }

    private void saveSchedule() {
        String scheduleTitle = schedule_title.getText().toString();
        String scheduleDetail = schedule_detail.getText().toString();
        String selectedDate = dateTextView.getText().toString();

        // SharedPreferences에 데이터 저장
        SharedPreferences.Editor editor = getSharedPreferences("SchedulePreferences", MODE_PRIVATE).edit();
        editor.putString(selectedDate + "_title", scheduleTitle);
        editor.putString(selectedDate + "_detail", scheduleDetail);
        editor.putInt(selectedDate + "_color", selectedColor);
        editor.apply();

        // CalendarActivity로 데이터를 전달하기 위한 Intent 생성
        Intent intent = new Intent(ScheduleActivity.this, CalendarActivity.class);
        intent.putExtra("selectedDate", selectedDate); // 선택된 날짜
        intent.putExtra("scheduleTitle", scheduleTitle); // 일정 제목
        intent.putExtra("scheduleDetail", scheduleDetail); // 일정 세부 사항
        intent.putExtra("selectedColor", selectedColor); // 선택된 색상

        // CalendarActivity로 이동
        startActivity(intent);
        finish(); // 현재 Activity 종료
    }

}

