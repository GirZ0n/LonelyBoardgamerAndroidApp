package com.twoIlya.android.lonelyboardgamer.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token

object TokenRepository {

    private lateinit var encryptedSharedPreferences: SharedPreferences

    fun setSharedPreferences(context: Context) {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            context,
            SHARED_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private const val SHARED_PREF_NAME = "token_preferences"
    private const val SERVER_TOKEN_KEY = "SERVER_TOKEN"
    private const val VK_TOKEN_KEY = "VK_TOKEN"

    fun getServerToken(): Token {
        val value = encryptedSharedPreferences.getString(SERVER_TOKEN_KEY, "") ?: ""
        return Token(value)
    }

    fun setServerToken(token: Token) =
        encryptedSharedPreferences.edit().putString(SERVER_TOKEN_KEY, token.value).apply()

    fun getVKToken(): Token {
        val value = encryptedSharedPreferences.getString(VK_TOKEN_KEY, "") ?: ""
        return Token(value)
    }

    fun setVKToken(token: Token) =
        encryptedSharedPreferences.edit().putString(VK_TOKEN_KEY, token.value).apply()

}
