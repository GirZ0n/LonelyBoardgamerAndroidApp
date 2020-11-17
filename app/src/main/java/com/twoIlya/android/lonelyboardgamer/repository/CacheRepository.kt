package com.twoIlya.android.lonelyboardgamer.repository

import android.content.Context
import androidx.preference.PreferenceManager

object CacheRepository {

    private lateinit var context: Context

    fun setContext(context: Context) {
        CacheRepository.context = context
    }

    private const val IS_LOGGED_IN_KEY = "is_logged_in"

    fun isLoggedIn(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    fun setIsLoggedIn(value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN_KEY, value).apply()
    }
}
