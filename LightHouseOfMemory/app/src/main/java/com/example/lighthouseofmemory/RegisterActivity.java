package com.example.lighthouseofmemory;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

//import retrofit2.Call;
//import retrofit2.http.Body;
//import retrofit2.http.POST;



public class RegisterActivity extends AppCompatActivity {



    private EditText editTextName, editTextId, editTextPassword;
    private Button registerButton;

    public class User {
        private String name;
        private String id;
        private String password;

        public User(String name, String id, String password) {
            this.name = name;
            this.id = id;
            this.password = password;
        }

        // Getter 메서드 추가
        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // 회원가입 레이아웃

        editTextName = findViewById(R.id.UserName_Inpu);
        editTextId = findViewById(R.id.ID_Input);
        editTextPassword = findViewById(R.id.PW_Input);
        registerButton = findViewById(R.id.Register_b);

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