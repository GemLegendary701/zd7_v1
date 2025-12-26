package com.example.zd7_v1.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("tour_prefs", Context.MODE_PRIVATE)

    fun saveLoginData(email: String, role: String) {
        sharedPref.edit().apply {
            putString("email", email)
            putString("role", role)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun logout() {
        sharedPref.edit().apply {
            remove("email")
            remove("role")
            putBoolean("is_logged_in", false)
            apply()
        }
    }

    fun getRole(): String = sharedPref.getString("role", "client") ?: "client"
    fun getEmail(): String = sharedPref.getString("email", "") ?: ""
    fun isLoggedIn(): Boolean = sharedPref.getBoolean("is_logged_in", false)
    fun isAgent(): Boolean = getRole() == "agent"
    fun isClient(): Boolean = getRole() == "client"
}