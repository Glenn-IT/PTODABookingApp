package com.example.ptodabookingapp.api

import com.example.ptodabookingapp.models.AuthResponse
import com.example.ptodabookingapp.models.DriverDashboardResponse
import com.example.ptodabookingapp.models.UpdateProfileRequest
import com.example.ptodabookingapp.models.RegisterRequest
import com.example.ptodabookingapp.models.LoginRequest
import com.example.ptodabookingapp.models.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ===== Authentication Endpoints =====

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    // ===== Driver Endpoints =====

    @GET("driver/dashboard")
    fun getDriverDashboard(
        @Header("Authorization") token: String
    ): Call<DriverDashboardResponse>

    @POST("driver/update-profile")
    fun updateDriverProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<ApiResponse<Any>>

    @Multipart
    @POST("driver/submit-verification")
    fun submitDriverVerification(
        @Header("Authorization") token: String,
        @Part("license_number") licenseNumber: RequestBody,
        @Part("vehicle_type") vehicleType: RequestBody,
        @Part("vehicle_plate") vehiclePlate: RequestBody,
        @Part("vehicle_model") vehicleModel: RequestBody,
        @Part("vehicle_color") vehicleColor: RequestBody,
        @Part licenseImage: MultipartBody.Part?,
        @Part vehicleImage: MultipartBody.Part?
    ): Call<ApiResponse<Any>>

    @Multipart
    @POST("driver/update-vehicle")
    fun updateVehicle(
        @Header("Authorization") token: String,
        @Part("vehicle_type") vehicleType: RequestBody,
        @Part("vehicle_plate") vehiclePlate: RequestBody,
        @Part("vehicle_model") vehicleModel: RequestBody,
        @Part("vehicle_color") vehicleColor: RequestBody,
        @Part vehicleImage: MultipartBody.Part?
    ): Call<ApiResponse<Any>>

    @GET("driver/statistics")
    fun getDriverStatistics(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Any>>

    // ===== Passenger Endpoints =====

    @GET("passenger/dashboard")
    fun getPassengerDashboard(
        @Header("Authorization") token: String
    ): Call<ApiResponse<Any>>

    @POST("passenger/update-profile")
    fun updatePassengerProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Call<ApiResponse<Any>>
}

