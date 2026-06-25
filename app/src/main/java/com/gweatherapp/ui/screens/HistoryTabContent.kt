package com.gweatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gweatherapp.ui.viewmodel.WeatherUiState
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryTabContent(viewModel: WeatherViewModel) {
    val historyItems by viewModel.historyList.collectAsState()
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

    val contentColor = if (isCurrentAppThemeNight) Color.White else Color.Black

    if (historyItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No lookups cached yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor.copy(alpha = 0.6f)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(historyItems) { item ->
            val fetchedTimeSec = item.fetchedAtTimestamp / 1000
            val isEntryItemNight = fetchedTimeSec < item.weatherData.sys.sunrise || fetchedTimeSec >= item.weatherData.sys.sunset

            val iconVector = if (isEntryItemNight) Icons.Filled.Nightlight else Icons.Default.WbSunny
            val iconColor = if (isEntryItemNight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrentAppThemeNight) {
                        Color.White.copy(alpha = 0.15f)
                    } else {
                        Color.Black.copy(alpha = 0.05f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = iconColor
                        )

                        Column {
                            Text(
                                text = "${item.weatherData.cityName}, ${item.weatherData.sys.country}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            val timeFormat = SimpleDateFormat("yyyy-MM-dd · hh:mm a", Locale.getDefault())
                            val formattedTime = timeFormat.format(Date(item.fetchedAtTimestamp))

                            Text(
                                text = "Fetched: $formattedTime",
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Text(
                        text = "${item.weatherData.main.temp}°",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                }
            }
        }
    }
}