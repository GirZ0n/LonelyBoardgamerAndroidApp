package com.twoIlya.android.lonelyboardgamer.activities.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class LoginViewModel : ViewModel() {
    private val repo = ServerRepository

    private val token = MutableLiveData<String>()
    val loginServerResponse = Transformations.switchMap(token) { token ->
        repo.login(token)
    }

    fun login(accessToken: String) {
        token.value = accessToken
        TokenRepository.setVKToken(accessToken)
    }

    fun isUserLoggedIn() = CacheRepository.isLoggedIn()
}
