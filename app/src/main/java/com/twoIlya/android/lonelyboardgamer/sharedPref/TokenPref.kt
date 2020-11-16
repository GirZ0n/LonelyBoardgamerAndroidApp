package com.twoIlya.android.lonelyboardgamer.sharedPref

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object TokenPref {

    private const val SHARED_PREF_NAME = "token_preferences"
    private const val SERVER_TOKEN_KEY = "server_token"
    private const val VK_TOKEN_KEY = "vk_token"

    fun getServerToken(context: Context) =
        getEncryptedSharedPreferences(context).getString(SERVER_TOKEN_KEY, "") ?: ""

    fun setServerToken(context: Context, value: String) =
        getEncryptedSharedPreferences(context).edit().putString(SERVER_TOKEN_KEY, value).apply()

    fun getVKToken(context: Context) =
        getEncryptedSharedPreferences(context).getString(VK_TOKEN_KEY, "") ?: ""

    fun setVKToken(context: Context, value: String) =
        getEncryptedSharedPreferences(context).edit().putString(VK_TOKEN_KEY, value).apply()

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
