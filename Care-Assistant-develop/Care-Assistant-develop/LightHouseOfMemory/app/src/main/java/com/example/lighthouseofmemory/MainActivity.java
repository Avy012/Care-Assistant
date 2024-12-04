package com.example.lighthouseofmemory;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;

import android.location.Location;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private boolean requestingLocationUpdates = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // BottomNavigationView 초기화
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Button loginButton;
        ImageButton settingButton;
        loginButton = findViewById(R.id.Login_button);
        settingButton = findViewById(R.id.Setting_b);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 설정 화면으로 이동
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        // 네비게이션 바의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_gps) {
                    startActivity(new Intent(MainActivity.this, maps.class));
                    return true;
                } else if (itemId == R.id.navigation_calender) {
                    startActivity(new Intent(MainActivity.this, CalendarActivity.class));
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


//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        // 위치 업데이트 처리
//                    }
//                }
//            }
//        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button shareBtn = findViewById(R.id.ShareBtn);

        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        createLocationRequest();
                        startLocationUpdates();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        createLocationRequest();
                        startLocationUpdates();
                    }
                }
        );

        shareBtn.setOnClickListener(v -> {
            requestLocationPermission();
            Intent intent = new Intent(MainActivity.this, maps.class);
            startActivity(intent);
        });
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(1000)
                .setWaitForAccurateLocation(true)
                .build();
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            requestingLocationUpdates = true;
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationPermission() {
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        requestingLocationUpdates = false;
    }


}
