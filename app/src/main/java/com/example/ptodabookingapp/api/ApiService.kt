package com.example.ptodabookingapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    
    @POST("register")
    fun registerUser(@Body request: RegisterRequest): Call<ApiResponse<User>>
    
    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<ApiResponse<User>>
}

