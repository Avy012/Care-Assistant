package com.example.teamproject;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.PlacesStatusCodes;

import java.util.Arrays;
import java.util.List;

public class LocationSetting extends Fragment {
    private EditText searchEditText;
    private RecyclerView addressRecyclerView;
    private AddressAdapter addressAdapter;
    private Button searchButton;
    private PlacesClient placesClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_setting, container, false);

        // Google Places SDK 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBW6O6DQJvrxJfgc9ShyDin4Ck42iP7Mcg");
        }
        placesClient = Places.createClient(requireContext());

        searchEditText = view.findViewById(R.id.searchEditText);
        addressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        searchButton = view.findViewById(R.id.searchButton);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter();
        addressRecyclerView.setAdapter(addressAdapter);

        searchButton.setOnClickListener(v -> searchAddress());

        return view;
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void searchAddress() {
        // 권한 확인
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 요청
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 권한이 있으면 검색 로직 실행
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Arrays.asList(
                Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS, Place.Field.LOCATION));

        placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {
            Log.d("LocationSetting","검색 알고리즘 진입");
            List<Place> places = null;
//            List<Place> places = response.getPlaces();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // if문 안 쪽으론 들어옴
                Log.d("LocationSetting", "검색 알고리즘 진입2");
                places = response.getPlaceLikelihoods().stream()
                        .map(PlaceLikelihood::getPlace)
                        .toList();

                // 애뮬로 돌리면 구글 본사 주소 나옴 (휴대폰으로 확인해야 함)
                System.out.println(places);
            }
            addressAdapter.updateAddresses(places);
        }).addOnFailureListener((exception) -> {
            Log.e("LocationSetting", "Error fetching places: ", exception);
            Toast.makeText(getContext(), "검색 실패: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                searchAddress(); // 권한이 승인되면 다시 검색 실행
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
