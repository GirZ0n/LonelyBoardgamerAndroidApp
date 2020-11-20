package com.twoIlya.android.lonelyboardgamer.activities.login

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class LoginViewModel : ViewModel() {
    private val repo = ServerRepository
    private val errorHandler = ErrorHandler

    private val _serverToken = MutableLiveData<Token>()
    val serverToken: LiveData<Token> = _serverToken

    private val accessToken = MutableLiveData<Token>()
    private val loginServerResponse = Transformations.switchMap(accessToken) { token ->
        repo.login(token)
    }

    val eventLiveData = MediatorLiveData<Event>()

    init {
        eventLiveData.addSource(loginServerResponse) {
            if (errorHandler.isError(it)) {
                eventLiveData.postValue(errorHandler.loginErrorHandler(it as ServerError))
            } else {
                _serverToken.postValue(it as Token)
            }
        }
    }

    fun login(accessToken: String) {
        val token = Token(accessToken)
        this.accessToken.value = token
        TokenRepository.setVKToken(token)
    }

    fun isUserLoggedIn() = CacheRepository.isLoggedIn()
}
