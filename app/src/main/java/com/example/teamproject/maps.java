package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class maps extends FragmentActivity implements OnMapReadyCallback {
    // 반경 m 단위
    private float radius;

    // 환자의 기준 위치
    double patientBaseLat;
    double patientBaseLong;

    // 반경 이탈 여부
    private boolean isWithinRange;

    private String dateTime;
    private LocalDateTime now;
    private DateTimeFormatter formatter;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private DatabaseReference location_data;
    private Marker currentLoca_Mark; // 환자의 현재 위치 마커
    private Marker baseLoca_Mark; // 환자의 기준 위치 마커
    private Circle rangeCircle; // 반경 원 저장할 변수
    private String randomPath; // 랜덤 pathString 저장할 변수

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public double getPatientBaseLat() {
        return patientBaseLat;
    }

    public void setPatientBaseLat(double patientBaseLat) {
        this.patientBaseLat = patientBaseLat;
    }

    public double getPatientBaseLong() {
        return patientBaseLong;
    }

    public void setPatientBaseLong(double patientBaseLong) {
        this.patientBaseLong = patientBaseLong;
    }

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
                        setFirstR();
                        loadDataFromFirebase();
                        updateMapLocation(location);
                        MapBaseLocation();
                    }
                }
            }
        };

        randomPath = generateRandomCode();

        Button setRBtn = findViewById(R.id.radiusSetBtn);
        Button setBtn = findViewById(R.id.setBtn);
        EditText radiusEdit = findViewById(R.id.radiusInput);

        setBtn.setVisibility(View.INVISIBLE);
        setBtn.setClickable(false);
        radiusEdit.setVisibility(View.INVISIBLE);
        radiusEdit.setClickable(false);

        setRBtn.setOnClickListener(v -> {
            setBtn.setVisibility(View.VISIBLE);
            setBtn.setClickable(true);
            radiusEdit.setVisibility(View.VISIBLE);
            radiusEdit.setClickable(true);
            radiusEdit.setText("");
        });

        radiusEdit.setOnClickListener(v -> {
            radiusEdit.setTextColor(Color.BLACK);
        });

        setBtn.setOnClickListener(v -> {
            setBtn.setVisibility(View.INVISIBLE);
            setBtn.setClickable(false);
            radiusEdit.setVisibility(View.INVISIBLE);
            radiusEdit.setClickable(false);
            editRadius();

            startLocationUpdates();
            MapBaseLocation();
        });
    }

    private void setFirstLoca() {
        loadDataFromFirebase();
    }

    private void setFirstR() {
        setRadius(10);
    }

    private void editRadius() {
        EditText radiusEdit = findViewById(R.id.radiusInput);
        float entry = Float.parseFloat(String.valueOf(radiusEdit.getText()));
        setRadius(entry);
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

    public static String generateRandomCode() {
        // 랜덤 객체 생성
        Random random = new Random();

        // 영어 4자리 생성 (대문자 A-Z)
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            letters.append(letter);
        }

        // 숫자 4자리 생성 (0-9)
        StringBuilder numbers = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int digit = random.nextInt(10);
            numbers.append(digit);
        }

        // 영어와 숫자를 결합
        return letters.toString() + numbers.toString();
    }

    @SuppressLint("SetTextI18n")
    private void saveLocation(Location location) {
        now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }
        formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy MM dd_HH:mm:ss");
        }
        dateTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTime = now.format(formatter);
        }

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());

        isWithinRange = LocationUtils.isWithinRadius(getPatientBaseLat(), getPatientBaseLong(), location.getLatitude(), location.getLongitude(), getRadius());
        Log.d("기준 위치", "위도"+getPatientBaseLat() +" 경도"+getPatientBaseLong());

        Button reportBtn  = findViewById(R.id.reportBtn);
        reportBtn.setVisibility(View.INVISIBLE);
        reportBtn.setClickable(false);

        // Firebase Realtime Database에 위치 데이터 저장
        location_data.child(randomPath).child(dateTime).setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Location saved successfully");
                    checkAndDeleteOldestData(); // 데이터 삭제 확인
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save location", e));

        if (!isWithinRange) {
            Log.d("반경 설정", "설정 반경 벗어남");
            reportBtn.setVisibility(View.VISIBLE);
            reportBtn.setClickable(true);

            reportBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL); // 전화 앱만 열기
                intent.setData(Uri.parse("tel:119")); // 전화번호 설정
                startActivity(intent);
            });
        }
        else {
            Log.d("반경 설정", "반경 내 존재");
            reportBtn.setVisibility(View.INVISIBLE);
            reportBtn.setClickable(false);
        }
    }

    private void loadDataFromFirebase() {
        // Firebase 경로 지정 및 데이터 읽기 (가장 처음 데이터 가져오기)
        location_data.child(randomPath)
                .orderByKey() // 키를 기준으로 정렬 (키가 날짜)
                .limitToFirst(1) // 첫 번째 데이터만 가져옴
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());

                        } else {
                            DataSnapshot resultSnapshot = task.getResult();
                            if (resultSnapshot.exists()) {
                                // 가장 처음 데이터 읽기
                                for (DataSnapshot snapshot : resultSnapshot.getChildren()) {
                                    String dateValue = snapshot.getKey(); // 키 값 (날짜)
                                    Log.d("firebase", "First Date Key: " + dateValue);

                                    // latitude와 longitude 읽기
                                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                                    setPatientBaseLat(latitude);
                                    setPatientBaseLong(longitude);


                                    if (latitude != null && longitude != null) {
                                        Log.d("firebase", "First Latitude: " + latitude);
                                        Log.d("firebase", "First Longitude: " + longitude);

                                    } else {
                                        Log.e("firebase", "Missing latitude or longitude");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void checkAndDeleteOldestData() {
        location_data.child(randomPath).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataCount = (int) snapshot.getChildrenCount();
                int maxDataLimit = 30; // 데이터 한도 설정

                if (dataCount > maxDataLimit) {
                    // 모든 키를 수집하여 정렬
                    List<String> keys = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        keys.add(data.getKey());
                    }
                    Collections.sort(keys); // 키를 오름차순으로 정렬

                    if (keys.size() > 1) {
                        // 두 번째 키를 삭제
                        String secondOldestKey = keys.get(1);
                        location_data.child(randomPath).child(secondOldestKey).removeValue()
                                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Second oldest location data deleted successfully"))
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete second oldest location data", e));
                    } else {
                        Log.d("Firebase", "Not enough data to delete second oldest.");
                    }
                }
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read location data", error.toException());
            }
        });
    }


    private void MapBaseLocation() {
//        if(mMap != null) {
        System.out.println(getPatientBaseLat() + getPatientBaseLong());
            LatLng basedLatLng = new LatLng(getPatientBaseLat(), getPatientBaseLong());

            // 기준점 중심 반경 원이 이미 있는 경우 제거
            if(rangeCircle != null) {
                rangeCircle.remove();
            }

            // 새로운 기준 마커 추가
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(basedLatLng)
                    .title("기준 위치")
                    .snippet ("기준 위도 "+ getPatientBaseLat() +"기준 경도 "+ getPatientBaseLong());

            baseLoca_Mark = mMap.addMarker(markerOptions);

            // 새 반경 원 추가
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(getPatientBaseLat(), getPatientBaseLong())) // 기준점
                    .radius(getRadius()) // 반경 (m 단위)
                    .strokeColor(Color.RED) // 테두리 색
                    .strokeWidth(5) // 테두리 두께
                    .fillColor(0x44FF6666) // 원 내부 색
                    .clickable(false);

            rangeCircle = mMap.addCircle(circleOptions);

            TextView setting_rad = findViewById(R.id.radius);
            setting_rad.setText(String.format("%.2f", getRadius()));
        }
//    }

    private void updateMapLocation(Location location) {
//        if (mMap != null) {
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
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    private void setupLocationCallback() {
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

    private void startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 위치 업데이트 중지
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}

