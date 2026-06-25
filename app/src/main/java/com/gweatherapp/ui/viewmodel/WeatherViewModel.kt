package com.gweatherapp.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gweatherapp.data.model.WeatherResponse
import com.gweatherapp.data.repository.WeatherRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Wrapper model to bind the API response to its precise local fetch time
data class HistoryEntry(
    val weatherData: WeatherResponse,
    val fetchedAtTimestamp: Long // Stores System.currentTimeMillis()
)

class WeatherViewModel(
    application: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    // Changed the type from List<WeatherResponse> to List<HistoryEntry>
    private val _historyList = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val historyList: StateFlow<List<HistoryEntry>> = _historyList

    init {
        loadHistoryFromCache()
    }

    fun loadWeather(location: String, apiKey: String) {
        if (apiKey.isBlank()) {
            _weatherState.value = WeatherUiState.Error("Missing API Key context configurations.")
            return
        }

        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val result = repository.fetchWeather(location, apiKey)
                _weatherState.value = WeatherUiState.Success(result)

                // Pair the data with the exact current timestamp
                val newEntry = HistoryEntry(weatherData = result, fetchedAtTimestamp = System.currentTimeMillis())
                val updatedList = listOf(newEntry) + _historyList.value

                _historyList.value = updatedList
                saveHistoryToCache(updatedList)

            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.localizedMessage ?: "Unknown Network Error.")
            }
        }
    }

    private fun saveHistoryToCache(history: List<HistoryEntry>) {
        val jsonString = gson.toJson(history)
        sharedPrefs.edit().putString("history_key", jsonString).apply()
    }

    private fun loadHistoryFromCache() {
        val jsonString = sharedPrefs.getString("history_key", null)
        if (!jsonString.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<HistoryEntry>>() {}.type
                val cachedList: List<HistoryEntry> = gson.fromJson(jsonString, type)

                // Extra safety step: Filter out any corrupted null elements parsed from old structures
                _historyList.value = cachedList.filter { it?.weatherData != null }
            } catch (e: Exception) {
                // If the data structure mismatches, clear the corrupted cache and start fresh
                sharedPrefs.edit().remove("history_key").apply()
                _historyList.value = emptyList()
            }
        }
    }
}