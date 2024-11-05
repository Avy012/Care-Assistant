package com.example.teamproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationSetting extends Fragment {

    private EditText searchEditText;
    private RecyclerView addressRecyclerView;
    private AddressAdapter addressAdapter;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_setting, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        addressRecyclerView = view.findViewById(R.id.addressRecyclerView);
        searchButton = view.findViewById(R.id.searchButton);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter();
        addressRecyclerView.setAdapter(addressAdapter);

        searchButton.setOnClickListener(v -> searchAddress());

        return view;
    }

    private void searchAddress() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            KakaoApiService apiService = RetrofitClient.getInstance().create(KakaoApiService.class);
            Call<KakaoAddressResponse> call = apiService.searchAddress(query);
            call.enqueue(new Callback<KakaoAddressResponse>() {
                @Override
                public void onResponse(Call<KakaoAddressResponse> call, Response<KakaoAddressResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<KakaoAddressResponse.Address> kakaoAddresses = response.body().getDocuments();
                        List<Address> addresses = new ArrayList<>();

                        // KakaoAddressResponse.Address 객체를 Address 객체로 변환
                        for (KakaoAddressResponse.Address kakaoAddress : kakaoAddresses) {
                            Address address = new Address();
                            address.setAddressName(kakaoAddress.getAddressName());
                            address.setRoadAddress(kakaoAddress.getRoadAddress());
                            addresses.add(address);
                        }

                        addressAdapter.updateAddresses(addresses);
                    } else {
                        Toast.makeText(getContext(), "주소 검색 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<KakaoAddressResponse> call, Throwable t) {
                    t.printStackTrace(); // 에러 로그 출력
                    Toast.makeText(getContext(), "주소 검색 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
