package com.gweatherapp.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode.asStateFlow()

    fun toggleAuthMode() {
        _isRegisterMode.value = !_isRegisterMode.value
        _uiState.value = AuthUiState.Idle
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
            return
        }

        if (sharedPrefs.contains(username)) {
            _uiState.value = AuthUiState.Error("Username already exists")
            return
        }

        sharedPrefs.edit().putString(username, password).apply()
        _uiState.value = AuthUiState.Idle
        _isRegisterMode.value = false
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
            return
        }

        val savedPassword = sharedPrefs.getString(username, null)
        if (savedPassword == null || savedPassword != password) {
            _uiState.value = AuthUiState.Error("Invalid username or password")
            return
        }

        _uiState.value = AuthUiState.Success
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}