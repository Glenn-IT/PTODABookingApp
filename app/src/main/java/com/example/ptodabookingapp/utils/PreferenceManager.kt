package com.example.ptodabookingapp.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private const val PREF_NAME = "ptoda_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_IS_VERIFIED = "is_verified"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ===== Token Management =====

    fun saveToken(token: String) {
        preferences.edit().putString(KEY_TOKEN, "Bearer $token").apply()
    }

    fun getToken(): String? = preferences.getString(KEY_TOKEN, null)

    fun clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply()
    }

    // ===== User Data Management =====

    fun saveUserData(userId: Long, name: String, email: String, userType: String, isVerified: Boolean) {
        preferences.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_TYPE, userType)
            putBoolean(KEY_IS_VERIFIED, isVerified)
            apply()
        }
    }

    fun getUserId(): Long = preferences.getLong(KEY_USER_ID, -1)
    fun getUserName(): String? = preferences.getString(KEY_USER_NAME, null)
    fun getUserEmail(): String? = preferences.getString(KEY_USER_EMAIL, null)
    fun getUserType(): String? = preferences.getString(KEY_USER_TYPE, null)
    fun isVerified(): Boolean = preferences.getBoolean(KEY_IS_VERIFIED, false)

    // ===== Clear All Data =====

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}

