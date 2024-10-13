package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 현재 위치 가져오기
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        // 위치 권한 체크 및 위치 정보 요청
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // 현재 위치 가져오기 성공
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            LatLng currentLatLng = new LatLng(latitude, longitude);
//                            float accuracy = location.getAccuracy();
//                            System.out.println("Accuracy: " + accuracy + " meters");
//                            LatLng currentLatLng = new LatLng(37.559883, 126.948865);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20));
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치"));

                            // 현재 위치를 텍스트로 표시 (예시: 토스트 메시지로 출력)
                            Toast.makeText(maps.this, "현재 위치: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                        } else {
                            // 위치를 가져오지 못한 경우
                            Toast.makeText(maps.this, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // 권한 요청
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 권한이 허용되면 위치 가져오기
            getCurrentLocation();
        }
    }
}
