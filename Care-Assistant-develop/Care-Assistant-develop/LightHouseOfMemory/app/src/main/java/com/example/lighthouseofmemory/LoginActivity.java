package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText idInput;
    private EditText pwInput;
    private Button loginButton;
    private ImageButton backButton;
    private TextView registerText;
    private Button registerButton;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // 로그인 한 상태면 바로 메인 화면
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // XML 파일의 이름을 확인하세요.

        // UI 요소 초기화
        idInput = findViewById(R.id.email_Input);
        pwInput = findViewById(R.id.PW_Input);
        loginButton = findViewById(R.id.Login_button);
        backButton = findViewById(R.id.Back_b);
        registerText = findViewById(R.id.Register_t);
        registerButton = findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idInput.getText().toString();
                String password = pwInput.getText().toString();

                // 간단한 로그인 검증 (예시)
                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                mAuth.signInWithEmailAndPassword(id, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "로그인 성공",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "로그인 실패",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });

        // 회원가입 버튼 클릭 이벤트
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RegisterActivity로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
