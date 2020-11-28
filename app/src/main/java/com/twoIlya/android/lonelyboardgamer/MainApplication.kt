package com.twoIlya.android.lonelyboardgamer

import android.app.Application
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CacheRepository.setSharedPreferences(this)
        TokenRepository.setSharedPreferences(this)
    }
}
