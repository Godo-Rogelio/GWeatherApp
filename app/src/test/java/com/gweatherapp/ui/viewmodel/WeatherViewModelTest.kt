package com.gweatherapp.ui.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gweatherapp.data.model.WeatherResponse
import com.gweatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var application: Application

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // 1. Mock both SharedPreferences AND its nested Editor instance
        val mockSharedPrefs = mock<android.content.SharedPreferences>()
        val mockEditor = mock<android.content.SharedPreferences.Editor>()

        // 2. Chain them together so sharedPrefs.edit() returns your mock editor
        whenever(mockSharedPrefs.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putString(any(), any())).thenReturn(mockEditor) // Allows chaining .apply() safely

        whenever(application.getSharedPreferences(any(), any())).thenReturn(mockSharedPrefs)

        viewModel = WeatherViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadWeather_success_updatesUiStateToSuccess() = runTest {
        val mockResponse = mock<WeatherResponse>()
        whenever(repository.fetchWeather(any(), any())).thenReturn(mockResponse)

        viewModel.loadWeather("Las Piñas", "your_api_key_here")
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedState = WeatherUiState.Success(mockResponse)
        Assert.assertEquals(expectedState, viewModel.weatherState.value)
    }

    @Test
    fun loadWeather_failure_updatesUiStateToError() = runTest {
        val errorMessage = "Network Error"
        whenever(repository.fetchWeather(any(), any())).thenThrow(RuntimeException(errorMessage))

        viewModel.loadWeather("Invalid City", "your_api_key_here")
        testDispatcher.scheduler.advanceUntilIdle()

        assert(viewModel.weatherState.value is WeatherUiState.Error)
        val actualError = (viewModel.weatherState.value as WeatherUiState.Error).errorMsg
        Assert.assertEquals(errorMessage, actualError)
    }
}