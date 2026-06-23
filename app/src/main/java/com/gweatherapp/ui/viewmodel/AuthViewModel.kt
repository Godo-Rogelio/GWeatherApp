package com.gweatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(username: String, password: String) {
        if (username.isNotBlank() && password.length >= 6) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Error("Invalid credentials. Password must be >= 6 chars.")
        }
    }

    fun register(username: String, password: String) {
        signIn(username, password) // Basic exercise simulation logic
    }
}