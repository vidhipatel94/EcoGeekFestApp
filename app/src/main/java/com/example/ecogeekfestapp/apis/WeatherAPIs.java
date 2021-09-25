package com.example.ecogeekfestapp.apis;

import com.example.ecogeekfestapp.model.WeatherReport;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIs {
    @GET("data/2.5/weather?appid=ab0ce8abce45d22897e5c0cfdd8d5a67")
    Call<WeatherReport> getWeatherReport(@Query("lat") double lat, @Query("lon") double lon);
}
