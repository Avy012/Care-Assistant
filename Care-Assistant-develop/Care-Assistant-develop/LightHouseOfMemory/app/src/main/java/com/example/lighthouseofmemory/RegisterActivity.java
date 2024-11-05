package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.POST;



public class RegisterActivity extends AppCompatActivity {



    private TextInputEditText editTextName, editTextId, editTextPassword;
    private Button registerButton;
    FirebaseAuth mAuth;



//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), GpsActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // 회원가입 레이아웃

        mAuth = FirebaseAuth.getInstance();
        editTextId = findViewById(R.id.email_Input);
        editTextPassword = findViewById(R.id.PW_Input);
        registerButton = findViewById(R.id.Register_b);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = editTextId.getText().toString();
                password = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "회원가입 성공",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "회원가입 실패.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

//        registerButton.setOnClickListener(this::registerUser);
    }

//    private void registerUser(View view) {
//        String name = editTextName.getText().toString().trim();
//        String id = editTextId.getText().toString().trim();
//        String password = editTextPassword.getText().toString().trim();
//
//        // MongoDB에 전송
//        User user = new User(name, id, password);
//        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
//        Call<User> call = apiService.registerUser(user);
//
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
//                    // 회원가입 후 MainActivity로 이동
//                    finish();
//                } else {
//                    Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//                public void onFailure(Call<User> call, Throwable t) {
//                Log.e("RegisterActivity", "Error: " + t.getMessage());
//                Toast.makeText(RegisterActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}