package com.example.lighthouseofmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 네비게이션 바의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_gps:
                        startActivity(new Intent(MainActivity.this, GpsActivity.class));
                        return true;
                    case R.id.navigation_calender:
                        startActivity(new Intent(MainActivity.this, CalenderActivity.class));
                        return true;
                    case R.id.Login_b:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        return true;
                }
                return false;
            }
        });

        // WindowInsets 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
