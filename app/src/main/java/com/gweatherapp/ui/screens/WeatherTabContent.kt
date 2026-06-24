package com.gweatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gweatherapp.data.model.WeatherResponse
import com.gweatherapp.ui.viewmodel.WeatherUiState
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.gweatherapp.R

@Composable
fun WeatherTabContent(viewModel: WeatherViewModel) {
    val uiState by viewModel.weatherState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is WeatherUiState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            is WeatherUiState.Success -> {
                val weatherData = (uiState as WeatherUiState.Success).data
                WeatherDisplayCard(data = weatherData)
            }
            is WeatherUiState.Error -> {
                Text(
                    text = "Something went wrong: ${(uiState as WeatherUiState.Error).errorMsg}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> Text("Please refresh or verify configuration properties.")
        }
    }
}

@Composable
private fun WeatherDisplayCard(data: WeatherResponse) {
    val currentTimeSec = System.currentTimeMillis() / 1000
    val isNightTime = currentTimeSec < data.sys.sunrise || currentTimeSec >= data.sys.sunset
    val mainCondition = data.weatherList.firstOrNull()?.mainState ?: "Clear"

    val iconVector = when {
        isNightTime -> Icons.Filled.Nightlight
        mainCondition.contains("Rain", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.WbSunny
    }

    val iconColor = when {
        isNightTime -> MaterialTheme.colorScheme.primary
        mainCondition.contains("Rain", ignoreCase = true) -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.tertiary
    }

    val backgroundDrawableRes = if (isNightTime) {
        R.drawable.evening_bg
    } else {
        R.drawable.morning_bg
    }

    val contentColor = if (isNightTime) Color.White else Color.Black

    val gradientBrush = if (isNightTime) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F2027).copy(alpha = 0.6f),
                Color(0xFF203A43).copy(alpha = 0.4f),
                Color(0xFF2C5364).copy(alpha = 0.7f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF741AAC).copy(alpha = 0.3f),
                Color(0xFFB47BE2).copy(alpha = 0.2f),
                Color(0xFFE4FAFF).copy(alpha = 0.6f)
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundDrawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(160.dp),
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${data.cityName}, ${data.sys.country}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${data.main.temp}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNightTime) {
                        Color.White.copy(alpha = 0.15f)
                    } else {
                        Color.Black.copy(alpha = 0.08f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val fullDateFormat = SimpleDateFormat("yyyy-MM-dd · hh:mm a", Locale.getDefault())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Column {
                                Text(
                                    text = "Sunrise:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = contentColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = timeFormat.format(Date(data.sys.sunrise * 1000)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(contentColor.copy(alpha = 0.2f))
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbTwilight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column {
                                Text(
                                    text = "Sunset:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = contentColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = timeFormat.format(Date(data.sys.sunset * 1000)),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fetched: ${fullDateFormat.format(Date())}",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}