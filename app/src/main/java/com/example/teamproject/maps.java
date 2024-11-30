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
import java.util.HashMap;
import java.util.Map;

public class maps extends FragmentActivity implements OnMapReadyCallback {
    // 반경 설정값을 저장할 배열
    private float[] radius_list;

    // 반경 m 단위
    private float radius;

    //37.582657, 127.010054

    // 환자의 기준 위치
//    double patientBaseLat;
//    double patientBaseLong;

    // 반경 이탈 여부
    private boolean isWithinRange;

    private static DatabaseReference mDatabase;

    private String dateTime;
    private LocalDateTime now;
    private DateTimeFormatter formatter;

    // 가장 초기 위치가 측정된 시간을 저장할 변수
    private String fisrtDateTime;

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

        // Firebase 데이터베이스 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Firebase에서 데이터 가져오기
//        loadDateFromFirebase();

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            now = LocalDateTime.now();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            formatter = DateTimeFormatter.ofPattern("yyyy MM dd_HH:mm:ss");
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            fisrtDateTime = now.format(formatter);
                        }
                        saveLocation(location);
                        updateMapLocation(location);
                        MapBaseLocation();
                    }
                }
            }
        };

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
        });

        radiusEdit.setOnClickListener(v -> {
            radiusEdit.setText("");
            radiusEdit.setTextColor(Color.BLACK);
        });

        setBtn.setOnClickListener(v -> {
            setBtn.setVisibility(View.INVISIBLE);
            setBtn.setClickable(false);
            radiusEdit.setVisibility(View.INVISIBLE);
            radiusEdit.setClickable(false);

            radius = setRadius();

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

            MapBaseLocation();
        });

    }

    static class LoadingFB {
        public double patientBaseLat;
        public double patientBaseLong;

        public double loadLatFromFirebase() {
            // Firebase 경로 지정 및 데이터 읽기 (가장 처음 데이터 가져오기)
            mDatabase.child("반경 설정 테스트")
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
                                        patientBaseLat = snapshot.child("latitude").getValue(Double.class);
                                        Log.d("위도", String.valueOf(patientBaseLat)); // 데이터 제대로 읽어와짐
                                        }
                                    }
                                }
                            }
                    });
            return patientBaseLat;
        }

        public double loadLongFromFirebase() {
            // Firebase 경로 지정 및 데이터 읽기 (가장 처음 데이터 가져오기)
            mDatabase.child("반경 설정 테스트")
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
                                        patientBaseLong = snapshot.child("longitude").getValue(Double.class); // 데이터 제대로 읽어와짐
                                    }
                                }
                            }
                        }
                    });
            return patientBaseLong;
        }
    }


    private float setRadius() {
        EditText radiusEdit = findViewById(R.id.radiusInput);
        radius = Float.parseFloat(String.valueOf(radiusEdit.getText()));

        return radius;
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
        LoadingFB load = new LoadingFB();

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

        isWithinRange = LocationUtils.isWithinRadius(load.loadLatFromFirebase(), load.loadLongFromFirebase(), location.getLatitude(), location.getLongitude(), radius);

        Button reportBtn  = findViewById(R.id.reportBtn);
        reportBtn.setVisibility(View.INVISIBLE);
        reportBtn.setClickable(false);

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

        // Firebase Realtime Database에 위치 데이터 저장
        location_data.child("DB 읽어오기 테스트").child(dateTime).setValue(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Location saved successfully");
                    checkAndDeleteOldestData(); // 데이터 삭제 확인
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to save location", e));
    }

    private void checkAndDeleteOldestData() {
        // pathString을 계정 ID로
        location_data.child("DB 읽어오기 테스트").addListenerForSingleValueEvent(new ValueEventListener() {
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
                        location_data.child("DB 읽어오기 테스트").child(oldestKey).removeValue()
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
        LoadingFB load = new LoadingFB();

        if(mMap != null) {
            LatLng basedLatLng = new LatLng(load.loadLatFromFirebase(), load.loadLongFromFirebase());

            // 기준점 중심 반경 원이 이미 있는 경우 제거
            if(rangeCircle != null) {
                rangeCircle.remove();
            }

            // 새로운 기준 마커 추가
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(basedLatLng)
                    .title("기준 위치")
                    .snippet ("기준 위도 "+ load.loadLatFromFirebase() +"기준 경도 "+ load.loadLongFromFirebase());

            baseLoca_Mark = mMap.addMarker(markerOptions);

            // 새 반경 원 추가
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(load.loadLatFromFirebase(), load.loadLongFromFirebase())) // 기준점
                    .radius(radius) // 반경 (m 단위)
                    .strokeColor(Color.RED) // 테두리 색
                    .strokeWidth(5) // 테두리 두께
                    .fillColor(0x44FF6666) // 원 내부 색
                    .clickable(false);

            rangeCircle = mMap.addCircle(circleOptions);

            TextView setting_rad = findViewById(R.id.radius);
            setting_rad.setText(String.format("%.2f", radius));
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