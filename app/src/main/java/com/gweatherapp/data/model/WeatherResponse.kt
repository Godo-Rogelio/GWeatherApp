package com.gweatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val cityName: String,
    @SerializedName("sys") val sys: Sys,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weatherList: List<WeatherDescription>
)

data class Sys(
    @SerializedName("country") val country: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long
)

data class Main(
    @SerializedName("temp") val temp: Double
)

data class WeatherDescription(
    @SerializedName("main") val mainState: String
)