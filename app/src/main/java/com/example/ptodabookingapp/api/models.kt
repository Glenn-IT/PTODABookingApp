package com.example.ptodabookingapp.api

// User data model
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// Generic API response wrapper
data class ApiResponse<T>(
    val status: String,
    val user: T? = null,
    val message: String? = null
)

// Request models for API calls
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

