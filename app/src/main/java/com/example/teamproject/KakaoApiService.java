package com.example.teamproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KakaoApiService {
    @GET("v2/local/search/address.json")
    Call<KakaoAddressResponse> searchAddress(@Header("Authorization") String apiKey, @Query("query") String query);
}

