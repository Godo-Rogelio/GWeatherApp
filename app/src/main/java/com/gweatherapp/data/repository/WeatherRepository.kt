package com.gweatherapp.data.repository

import com.gweatherapp.data.api.WeatherApiService
import com.gweatherapp.data.model.WeatherResponse

class WeatherRepository(private val apiService: WeatherApiService) {
    suspend fun fetchWeather(location: String, apiKey: String): WeatherResponse {
        return apiService.getCurrentWeather(location = location, apiKey = apiKey)
    }
}