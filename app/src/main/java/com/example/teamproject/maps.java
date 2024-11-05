package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private DatabaseReference location_data;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        // Firebase Database Reference 초기화
        location_data = FirebaseDatabase.getInstance().getReference();

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // SupportMapFragment 초기화
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // LocationRequest 초기화
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(10000)
                .setWaitForAccurateLocation(true)
                .build();

        // LocationCallback 설정
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        // 위치 정보 저장 및 지도 업데이트
                        saveLocation(location);
                        updateMapLocation(location);
                    }
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 위치 권한 체크 및 위치 업데이트 시작
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // 권한 요청
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void getCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void saveLocation(Location location) {
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy MM dd_HH:mm:ss");
        }
        String dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = now.format(formatter);
        }

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());

        // Firebase Realtime Database에 위치 데이터 저장
        location_data.child("Patient's Location").child(dateTime).setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Location saved successfully");
                    checkAndDeleteOldestData(); // 데이터 삭제 확인
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save location", e));
    }

    private void checkAndDeleteOldestData() {
        location_data.child("Patient's Location").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataCount = (int) snapshot.getChildrenCount();
                int maxDataLimit = 50; // 데이터 한도 설정

                if (dataCount > maxDataLimit) {
                    // 가장 오래된 데이터 삭제
                    String oldestKey = null;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (oldestKey == null || data.getKey().compareTo(oldestKey) < 0) {
                            oldestKey = data.getKey();
                        }
                    }
                    if (oldestKey != null) {
                        location_data.child("Patient's Location").child(oldestKey).removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Oldest location data deleted successfully"))
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete oldest location data", e));
                    }
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read location data", error.toException());
            }
        });
    }


    private void updateMapLocation(Location location) {
        // 지도의 위치 업데이트 및 마커 추가
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치"));

        // 현재 위치를 텍스트로 표시
        Toast.makeText(this, "현재 위치: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 위치 업데이트 중지
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}