package com.example.lighthouseofmemory;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {

    private TextView dateTextView;
    private EditText schedule_detail;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        dateTextView = findViewById(R.id.dateTextView);
        schedule_detail = findViewById(R.id.schedule_detail);
        saveButton = findViewById(R.id.saveButton);

        String selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        dateTextView.setText(selectedDate); // 선택된 날짜 표시

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 여기에 일정 저장 기능 구현
                String schedule_de = schedule_detail.getText().toString();
                // 필요시 데이터베이스나 SharedPreferences에 저장
            }
        });
    }
}
