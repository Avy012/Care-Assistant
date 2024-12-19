package com.example.lighthouseofmemory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText idInput;
    private EditText pwInput;
    private Button loginButton;
    private ImageButton backButton;
    private TextView registerText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XML 파일의 이름을 확인하세요.

        // UI 요소 초기화
        idInput = findViewById(R.id.ID_Input);
        pwInput = findViewById(R.id.PW_Input);
        loginButton = findViewById(R.id.Login_button);
        backButton = findViewById(R.id.Back_b);
        registerText = findViewById(R.id.Register_t);

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idInput.getText().toString();
                String password = pwInput.getText().toString();

                // 간단한 로그인 검증 (예시)
                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 로그인 성공시 다음 액티비티로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                }
            }
        });

        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        // 회원가입 텍스트 클릭 이벤트
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RegisterActivity로 이동 (여기에 이동할 액티비티를 지정하세요)
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
