package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    // 반경 km 단위
    private double radius = 30;

    // 환자의 기준 위치
    double patientBaseLat = 37.557981;
    double patientBaseLong = 126.951430;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private DatabaseReference location_data;
    private Marker currentLoca_Mark; // 환자의 현재 위치 마커
    private Marker baseLoca_Mark; // 환자의 기준 위치 마커
    private Circle rangeCircle; // 반경 원 저장할 변수

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
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // LocationRequest 초기화
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(1000)
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
                        MapBaseLocation();
                    }
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapsActivity", "Map is ready");
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

    @SuppressLint("SetTextI18n")
    private void saveLocation(Location location) {
        // Firebase 인증된 사용자 가져오기
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = currentUser.getUid();  // 사용자 UID 가져오기

        LocalDateTime now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy MM dd_HH:mm:ss");
        }
        String dateTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTime = now.format(formatter);
        }

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());

        boolean isWithinRange = LocationUtils.isWithinRadius(patientBaseLat, patientBaseLong, location.getLatitude(), location.getLongitude(), radius);

        if (isWithinRange) {
            Log.d("반경 설정", "반경 내 존재");
//            Toast.makeText(getBaseContext(), "반경 내 존재", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("반경 설정", "설정 반경 벗어남");
//            Toast.makeText(getBaseContext(), "설정 반경 벗어남", Toast.LENGTH_LONG).show();
        }

        // Firebase Realtime Database에 위치 데이터 저장
        location_data.child("반경 설정 테스트").child(dateTime).setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Location saved successfully");
                    checkAndDeleteOldestData(); // 데이터 삭제 확인
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save location", e));
    }

    private double setRadius(double radius, Location location) {
        Button setRBtn = findViewById(R.id.radiusSetBtn);
        setRBtn.setOnClickListener(v -> {
            // 반경 설정 버튼 클릭 시, 반경을 입력할 수 있는 plainText가 뜨고, 입력 완료 시 사라지게

        });

        boolean isWithinRange = LocationUtils.isWithinRadius(patientBaseLat, patientBaseLong, location.getLatitude(), location.getLongitude(), radius);
        return radius;
    }

    private void checkAndDeleteOldestData() {
        // pathString을 계정 ID로
        location_data.child("환자 위치 정보").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataCount = (int) snapshot.getChildrenCount();
                int maxDataLimit = 1000; // 데이터 한도 설정

                if (dataCount > maxDataLimit) {
                    // 가장 오래된 데이터 삭제
                    String oldestKey = null;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (oldestKey == null || data.getKey().compareTo(oldestKey) < 0) {
                            oldestKey = data.getKey();
                        }
                    }
                    if (oldestKey != null) {
                        // pathString을 계정 ID로
                        location_data.child("환자 위치 정보").child(oldestKey).removeValue()
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

    private void MapBaseLocation() {
        if(mMap != null) {
            LatLng basedLatLng = new LatLng(patientBaseLat, patientBaseLong);

//            // 기준점 마커 이미 있는 경우 제거
//            if(baseLoca_Mark != null) {
//                baseLoca_Mark.remove();
//            }
//
            // 기준점 중심 반경 원이 이미 있는 경우 제거
            if(rangeCircle != null) {
                rangeCircle.remove();
            }

            // 새로운 기준 마커 추가
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(basedLatLng)
                    .title("기준 위치")
                    .snippet ("기준 위도 "+ patientBaseLat +"기준 경도 "+ patientBaseLong);

            baseLoca_Mark = mMap.addMarker(markerOptions);

            // 새 반경 원 추가
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(patientBaseLat, patientBaseLong)) // 기준점
                    .radius(radius) // 반경 (m 단위)
                    .strokeColor(Color.RED) // 테두리 색
                    .strokeWidth(5) // 테두리 두께
                    .fillColor(0x44FF6666) // 원 내부 색
                    .clickable(false);

            rangeCircle = mMap.addCircle(circleOptions);

            TextView setting_rad = findViewById(R.id.radius);
            setting_rad.setText(String.format("%2f", radius));
        }
    }

    private void updateMapLocation(Location location) {
        if (mMap != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // 기존 마커 제거
            if (currentLoca_Mark != null) {
                currentLoca_Mark.remove();
            }

            // 새로운 마커 추가
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLatLng)
                    .title("현재 위치")
                    .snippet ("위도 "+location.getLatitude()+"경도 "+location.getLongitude());

            currentLoca_Mark = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 19));

            TextView pat_addr = findViewById(R.id.patientAd);
            pat_addr.setText(String.format("위도: %s, 경도: %s", location.getLatitude(), location.getLongitude()));

            // 로그 출력
            Log.d("MapsActivity", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
            System.out.println(location_data.getDatabase());
        }
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