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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gweatherapp.data.model.WeatherResponse
import com.gweatherapp.ui.viewmodel.WeatherUiState
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherTabContent(viewModel: WeatherViewModel) {
    val uiState by viewModel.weatherState.collectAsState()

    val isCurrentAppThemeNight = remember(uiState) {
        if (uiState is WeatherUiState.Success) {
            val data = (uiState as WeatherUiState.Success).data
            val currentTimeSec = System.currentTimeMillis() / 1000
            currentTimeSec < data.sys.sunrise || currentTimeSec >= data.sys.sunset
        } else {
            true
        }
    }

    val globalContentColor = if (isCurrentAppThemeNight) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is WeatherUiState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            is WeatherUiState.Success -> {
                val weatherData = (uiState as WeatherUiState.Success).data
                WeatherDisplayCard(
                    data = weatherData,
                    isCurrentAppThemeNight = isCurrentAppThemeNight,
                    contentColor = globalContentColor
                )
            }
            is WeatherUiState.Error -> {
                val errorMsg = (uiState as WeatherUiState.Error).errorMsg
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentAppThemeNight) {
                            Color.White.copy(alpha = 0.15f)
                        } else {
                            Color.Black.copy(alpha = 0.08f)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = globalContentColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = globalContentColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Please refresh or verify configuration properties.",
                    color = globalContentColor
                )
            }
        }
    }
}

@Composable
private fun WeatherDisplayCard(
    data: WeatherResponse,
    isCurrentAppThemeNight: Boolean,
    contentColor: Color
) {
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
                containerColor = if (isCurrentAppThemeNight) {
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