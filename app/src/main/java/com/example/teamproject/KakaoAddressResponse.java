package com.example.teamproject;

import java.util.List;

public class KakaoAddressResponse {
    private List<Address> documents;

    public List<Address> getDocuments() {
        return documents;
    }

    public static class Address {
        private String address_name;
        private String road_address;

        public String getAddressName() {
            return address_name;
        }

        public String getRoadAddress() {
            return road_address;
        }

    }
}
