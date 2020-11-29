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

    private val _isFormEnabled = MutableLiveData(true)
    val isFormEnabled: LiveData<Boolean> = _isFormEnabled

    private val _isButtonLoading = MutableLiveData(false)
    val isButtonLoading: LiveData<Boolean> = _isButtonLoading

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
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }
    }

    fun login(accessToken: String) {
        updateForm(isFormEnabled = false, isButtonLoading = true)
        val token = Token(accessToken)
        TokenRepository.setVKToken(token)
        this.accessToken.postValue(token)
    }

    fun isUserLoggedIn() = CacheRepository.isLoggedIn()

    private fun updateForm(isFormEnabled: Boolean, isButtonLoading: Boolean) {
        _isFormEnabled.postValue(isFormEnabled)
        _isButtonLoading.postValue(isButtonLoading)
    }
}
