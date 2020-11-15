package com.twoIlya.android.lonelyboardgamer.sharedPref

import android.content.Context
import androidx.preference.PreferenceManager

object Cache {

    private const val IS_LOGGED_IN_KEY = "is_logged_in"

    fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(IS_LOGGED_IN_KEY, false)
    }

    fun setIsLoggedIn(context: Context, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean(IS_LOGGED_IN_KEY, value).apply()
    }
}