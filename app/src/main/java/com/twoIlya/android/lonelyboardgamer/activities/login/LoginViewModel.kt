package com.twoIlya.android.lonelyboardgamer.activities.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.repository.Repository
import com.twoIlya.android.lonelyboardgamer.sharedPref.TokenPref

class LoginViewModel(private val app: Application): AndroidViewModel(app) {
    private val repo = Repository

    val loginServerResponse: LiveData<ServerResponse>
    private val token = MutableLiveData<String>()

    init {
        loginServerResponse = Transformations.switchMap(token) { token ->
            repo.login(token)
        }
    }

    fun login(accessToken: String) {
        token.value = accessToken
        TokenPref.setVKToken(app, accessToken)
    }
}
