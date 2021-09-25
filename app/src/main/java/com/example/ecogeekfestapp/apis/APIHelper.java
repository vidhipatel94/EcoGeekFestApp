package com.example.ecogeekfestapp.apis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIHelper {

    public static WeatherAPIs getWeatherAPIs() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WeatherAPIs.class);
    }
}
