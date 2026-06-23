package com.gweatherapp.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gweatherapp.data.model.WeatherResponse
import com.gweatherapp.ui.viewmodel.WeatherUiState
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

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
            modifier = Modifier.size(120.dp),
            tint = iconColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${data.cityName}, ${data.sys.country}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${data.main.temp}°",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Column {
                        Text(
                            text = "Sunrise",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = timeFormat.format(Date(data.sys.sunrise * 1000)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbTwilight,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Column {
                        Text(
                            text = "Sunset",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = timeFormat.format(Date(data.sys.sunset * 1000)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}