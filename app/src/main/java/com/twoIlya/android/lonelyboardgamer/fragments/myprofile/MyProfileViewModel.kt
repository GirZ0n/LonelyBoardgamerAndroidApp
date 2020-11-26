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

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _mechanics = MutableLiveData<List<String>>()
    val mechanics: LiveData<List<String>> = _mechanics

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val serverToken = MutableLiveData<Token>()
    private val getProfileServerResponse = Transformations.switchMap(serverToken) {
        ServerRepository.getProfile(it)
    }

    val events = MediatorLiveData<Event>()

    init {
        updateProfile()

        events.addSource(getProfileServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.getProfileErrorHandler(it as ServerError)

                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }

                events.postValue(event)
            } else if (it is Profile) {
                // TODO: ЗАПИСЫВАЕМ В КЭШ
                updateLiveData(it)
            }
        }
    }

    private fun updateProfile() {
        serverToken.postValue(TokenRepository.getServerToken())
    }

    private fun updateLiveData(profile: Profile) {
        val fullName = "${profile.firstName} ${profile.secondName}"
        _name.postValue(fullName)
        _location.postValue(profile.address)
        _categories.postValue(profile.categories)
        _mechanics.postValue(profile.mechanics)
        _description.postValue(profile.description)
    }
}
