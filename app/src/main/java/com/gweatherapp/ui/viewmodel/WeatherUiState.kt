package com.gweatherapp.ui.viewmodel

import com.gweatherapp.data.model.WeatherResponse

sealed interface WeatherUiState {
    object Empty : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val data: WeatherResponse) : WeatherUiState
    data class Error(val errorMsg: String) : WeatherUiState
}