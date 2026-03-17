package com.example.ptodabookingapp.models

import com.google.gson.annotations.SerializedName

// ===== Authentication Models =====

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val user_type: String,  // "driver" or "passenger"
    val phone: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    val user_type: String  // "driver" or "passenger"
)

data class UserData(
    val id: Long,
    val name: String,
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class AuthResponse(
    val status: String,
    val message: String,
    val user: UserData,
    val user_type: String,
    val is_verified: Boolean = false,
    val token: String? = null
)

// ===== Driver Models =====

data class DriverInfo(
    val name: String,
    val email: String,
    val phone: String? = null,
    val license_number: String? = null,
    val vehicle_type: String? = null,
    val vehicle_plate: String? = null,
    val vehicle_model: String? = null,
    val vehicle_color: String? = null,
    val is_verified: Boolean = false,
    val is_active: Boolean = true,
    val profile_photo: String? = null,
    val vehicle_image: String? = null
)

data class DriverDashboardResponse(
    val status: String,
    val user: UserData,
    val driver_info: DriverInfo
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postal_code: String? = null
)

data class SubmitVerificationRequest(
    val license_number: String,
    val vehicle_type: String,
    val vehicle_plate: String,
    val vehicle_model: String,
    val vehicle_color: String
)

data class UpdateVehicleRequest(
    val vehicle_type: String,
    val vehicle_plate: String,
    val vehicle_model: String,
    val vehicle_color: String
)

data class DriverStatistics(
    val total_rides: Int,
    val total_earnings: Double,
    val rating: Double,
    val active_rides: Int
)

// ===== Generic Response =====

data class ApiResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null
)


