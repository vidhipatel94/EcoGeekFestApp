package com.example.ecogeekfestapp.apis;

import com.example.ecogeekfestapp.model.RainReport;
import com.example.ecogeekfestapp.model.WeatherReport;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherAPIs {
    @GET("data/2.5/weather?appid=ab0ce8abce45d22897e5c0cfdd8d5a67")
    Call<WeatherReport> getWeatherReport(@Query("lat") double lat, @Query("lon") double lon);

    @GET("climateweb/rest/v1/country/annualavg/pr/2020/2039/{country}")
    Call<List<RainReport>> getRainReport(@Path("country") String country);

}
