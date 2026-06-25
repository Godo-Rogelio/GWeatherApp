package com.gweatherapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.gweatherapp.data.api.WeatherApiService
import com.gweatherapp.data.repository.WeatherRepository
import com.gweatherapp.ui.theme.GWeatherAppTheme
import com.gweatherapp.ui.viewmodel.AuthViewModel
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.gweatherapp.BuildConfig
import java.util.Locale

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
                        val context = LocalContext.current

                        var isLoggedIn by remember {
                            mutableStateOf(sharedPrefs.getBoolean("is_logged_in", false))
                        }

                        var detectedCity by remember { mutableStateOf("Manila") }

                        val locationPermissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()
                        ) { isGranted ->
                            if (isGranted) {
                                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                try {
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                            val cityName = addresses?.firstOrNull()?.locality
                                            if (!cityName.isNullOrEmpty()) {
                                                detectedCity = cityName
                                            }
                                        }
                                    }
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        LaunchedEffect(isLoggedIn) {
                            if (isLoggedIn) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                        if (location != null) {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                            val cityName = addresses?.firstOrNull()?.locality
                                            if (!cityName.isNullOrEmpty()) {
                                                detectedCity = cityName
                                            }
                                        }
                                    }
                                } else {
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }
                        }

                        if (!isLoggedIn) {
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
                                city = detectedCity,
                                contentPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}