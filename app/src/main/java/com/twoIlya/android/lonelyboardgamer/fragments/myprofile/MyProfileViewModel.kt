package com.twoIlya.android.lonelyboardgamer.fragments.myprofile

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class MyProfileViewModel : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _mechanics = MutableLiveData<List<String>>()
    val mechanics: LiveData<List<String>> = _mechanics

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    private val _isLayoutRefreshing = MutableLiveData(false)
    val isLayoutRefreshing: LiveData<Boolean> = _isLayoutRefreshing

    private val serverTokenForGetProfile = MutableLiveData<Token>()
    private val getProfileServerResponse = Transformations.switchMap(serverTokenForGetProfile) {
        ServerRepository.getProfile(it)
    }

    private val serverTokenForLogout = MutableLiveData<Token>()
    private val logoutServerResponse = Transformations.switchMap(serverTokenForLogout) {
        ServerRepository.logout(it)
    }

    val events = MediatorLiveData<Event>()

    init {
        CacheRepository.getProfile()?.let { updateLiveData(it) } ?: updateProfile()

        events.addSource(getProfileServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.getProfileErrorHandler(it as ServerError)

                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }

                _isLayoutRefreshing.postValue(false)
                events.postValue(event)
            } else if (it is Profile) {
                CacheRepository.setProfile(it)
                updateLiveData(it)
            }
        }

        events.addSource(logoutServerResponse) {
            CacheRepository.setIsLoggedIn(false)
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.logoutErrorHandler(it as ServerError)
                events.postValue(event)
            } else if (it is ServerMessage) {
                events.postValue(Event(EventType.Move, "Login"))
            }
        }
    }

    fun updateProfile() {
        serverTokenForGetProfile.postValue(TokenRepository.getServerToken())
    }

    fun logout() {
        serverTokenForLogout.postValue(TokenRepository.getServerToken())
    }

    private fun updateLiveData(profile: Profile) {
        val fullName = "${profile.firstName} ${profile.secondName}"
        _name.postValue(fullName)
        _address.postValue(profile.address)
        _categories.postValue(profile.categories)
        _mechanics.postValue(profile.mechanics)
        _description.postValue(profile.description)
        _isLayoutRefreshing.postValue(false)
        _imageUrl.postValue(
            "https://eu.ui-avatars.com/api/" +
                    "?name=${profile.firstName}+${profile.secondName}" +
                    "&bold=true" +
                    "&size=512" +
                    "&rounded=true" +
                    "&color=fff" +
                    "&background=000"
        )
    }
}
