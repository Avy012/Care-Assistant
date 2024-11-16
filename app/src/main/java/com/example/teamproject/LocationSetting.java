package com.example.teamproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationSetting extends Fragment {

    private EditText searchEditText;
    private Button searchBtn;
    private RecyclerView addressRecyclerView;
    private AddressAdapter addressAdapter;
    private AddressCodeDAO addressCodeDAO;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_setting, container, false);

        // UI 요소 초기화
        searchEditText = rootView.findViewById(R.id.searchEditText);
        searchBtn = rootView.findViewById(R.id.searchButton);
        addressRecyclerView = rootView.findViewById(R.id.addressRecyclerView);

        // RecyclerView 설정
        addressAdapter = new AddressAdapter();
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addressRecyclerView.setAdapter(addressAdapter);

        addressCodeDAO = new AddressCodeDAO();  // AddressCodeDAO 초기화

        // 검색 버튼 클릭 시 검색 수행
        searchBtn.setOnClickListener(v -> searchAddress(searchEditText.getText().toString()));

        // shareBtn 클릭 시 우편번호 검색 실행
        searchBtn.setOnClickListener(v -> {
            String inputText = searchEditText.getText().toString();
            if (inputText.isEmpty()) {
                Toast.makeText(getContext(), "주소를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                performZipcodeSearch(inputText);
            }
        });

        return rootView;
    }

    private void searchAddress(String query) {
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void performZipcodeSearch(String dong) {
        // DAO 객체를 통해 우편번호 데이터 조회
        ArrayList<AddressCodeVO> addressList = addressCodeDAO.postfind(dong);

        if (addressList.isEmpty()) {
            Toast.makeText(getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // RecyclerView에 검색 결과 업데이트
        addressAdapter.updateAddresses(addressList);

        Log.d("LocationSetting", "검색된 주소 수: " + addressList.size());
    }
}
