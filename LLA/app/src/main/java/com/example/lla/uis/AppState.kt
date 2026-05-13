package com.example.lla.uis

sealed class AppState<out T> {
    data object Idle : AppState<Nothing>()
    data object Loading : AppState<Nothing>()
    data class Success<out T>(val data: T) : AppState<T>()
    data class Error(val message: String) : AppState<Nothing>()
}

