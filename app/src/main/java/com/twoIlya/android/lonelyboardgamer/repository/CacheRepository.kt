package com.twoIlya.android.lonelyboardgamer.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.twoIlya.android.lonelyboardgamer.dataClasses.Profile

object CacheRepository {

    private lateinit var sharedPreferences: SharedPreferences

    fun setSharedPreferences(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private const val IS_LOGGED_IN_KEY = "IS_LOGGED_IN"

    fun isLoggedIn() = sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)

    fun setIsLoggedIn(value: Boolean) {
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN_KEY, value).apply()
    }

    private const val PROFILE_KEY = "PROFILE"

    fun setProfile(profile: Profile) {
        sharedPreferences.edit().putString(PROFILE_KEY, Gson().toJson(profile)).apply()
    }

    fun getProfile(): Profile? {
        val json = sharedPreferences.getString(PROFILE_KEY, "")
        return try {
            Gson().fromJson(json, Profile::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }
}
