package com.gweatherapp.ui.screens

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gweatherapp.data.api.WeatherApiService
import com.gweatherapp.data.repository.WeatherRepository
import com.gweatherapp.ui.theme.GWeatherAppTheme
import com.gweatherapp.ui.viewmodel.AuthViewModel
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.gweatherapp.BuildConfig

class MainActivity : ComponentActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val apiService by lazy { retrofit.create(WeatherApiService::class.java) }
    private val repository by lazy { WeatherRepository(apiService) }

    private val authViewModel: AuthViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WeatherViewModel(application, repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

        setContent {
            GWeatherAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                ) { innerPadding ->

                    Box(modifier = Modifier.fillMaxSize()) {
                        val apiKey = BuildConfig.OPENWEATHER_API_KEY

                        var isLoggedIn by remember {
                            mutableStateOf(sharedPrefs.getBoolean("is_logged_in", false))
                        }

                        if (!isLoggedIn) {
                            // Pass padding down to keep form elements safe from system bars
                            Box(modifier = Modifier.padding(innerPadding)) {
                                AuthScreen(
                                    viewModel = authViewModel,
                                    onAuthSuccess = {
                                        sharedPrefs.edit().putBoolean("is_logged_in", true).apply()
                                        isLoggedIn = true
                                    }
                                )
                            }
                        } else {
                            DashboardScreen(
                                viewModel = weatherViewModel,
                                apiKey = apiKey,
                                contentPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}