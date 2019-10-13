package com.example.weatherapiretrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather?")
    Call<WebApi> getCurrentWeatherData(@Query("q") String cityName, @Query("APPID") String app_id);
}
