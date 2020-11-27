package com.twoIlya.android.lonelyboardgamer.activities.login

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class LoginViewModel : ViewModel() {
    private val repo = ServerRepository
    private val errorHandler = ErrorHandler

    private val accessToken = MutableLiveData<Token>()
    private val loginServerResponse = Transformations.switchMap(accessToken) { token ->
        repo.login(token)
    }

    val events = MediatorLiveData<Event>()

    init {
        events.addSource(loginServerResponse) {
            if (errorHandler.isError(it)) {
                events.postValue(errorHandler.loginErrorHandler(it as ServerError))
            } else if (it is Token) {
                TokenRepository.setServerToken(it)
                CacheRepository.setIsLoggedIn(true)
                events.postValue(Event(EventType.Move, "MyProfile"))
            }
        }
    }

    fun login(accessToken: String) {
        val token = Token(accessToken)
        TokenRepository.setVKToken(token)
        this.accessToken.value = token
    }

    fun isUserLoggedIn() = CacheRepository.isLoggedIn()
}
