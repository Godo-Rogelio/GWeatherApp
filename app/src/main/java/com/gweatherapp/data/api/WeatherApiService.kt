package com.gweatherapp.data.api

import com.gweatherapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("units") units: String = "metric", // Enforces Celsius conversions automatically
        @Query("appid") apiKey: String
    ): WeatherResponse
}