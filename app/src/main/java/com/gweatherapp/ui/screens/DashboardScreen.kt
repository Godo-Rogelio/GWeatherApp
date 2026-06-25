package com.gweatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import kotlinx.coroutines.launch
import com.gweatherapp.ui.viewmodel.WeatherViewModel
import com.gweatherapp.ui.viewmodel.WeatherUiState
import com.gweatherapp.R

@Composable
fun DashboardScreen(viewModel: WeatherViewModel, apiKey: String, contentPadding: PaddingValues) {
    val tabTitles = listOf("Current Weather", "Fetch History")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.weatherState.collectAsState()

    val isNightTime = remember(uiState) {
        if (uiState is WeatherUiState.Success) {
            val data = (uiState as WeatherUiState.Success).data
            val currentTimeSec = System.currentTimeMillis() / 1000
            currentTimeSec < data.sys.sunrise || currentTimeSec >= data.sys.sunset
        } else {
            true
        }
    }

    val backgroundDrawableRes = if (isNightTime) R.drawable.evening_bg else R.drawable.morning_bg
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
        // Clean, vibrant blue/warm day tints instead of purples
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF3A7BD5).copy(alpha = 0.25f), // Soft Clear Blue
                Color(0xFF3A6073).copy(alpha = 0.10f), // Very subtle contrast stabilizer
                Color(0xFFFFFFFF).copy(alpha = 0.40f)  // Bright daylight gradient base
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadWeather("Manila", apiKey)
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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .padding(top = contentPadding.calculateTopPadding())
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GWeatherApp",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = Color.Transparent,
                        contentColor = contentColor
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = title, color = contentColor)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding())
                    .fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WeatherTabContent(viewModel)
                    1 -> HistoryTabContent(viewModel)
                }
            }
        }
    }
}