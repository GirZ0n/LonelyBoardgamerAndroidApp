package com.twoIlya.android.lonelyboardgamer.fragments.registration

import androidx.lifecycle.*
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class RegistrationViewModel : ViewModel() {
    private val _locationLiveData = MutableLiveData<String>()
    val locationLiveData: LiveData<String> = _locationLiveData

    fun updateLocation(address: String) {
        _locationLiveData.postValue(address)
    }

    private var categories = listOf<String>()
    private var mechanics = listOf<String>()

    val descriptionLiveData = MutableLiveData<String>()

    private val registrationDataLiveData = MutableLiveData<RegistrationData>()
    private val registrationServerResponse = Transformations.switchMap(registrationDataLiveData) {
        ServerRepository.register(
            it.token,
            it.location,
            it.categories,
            it.mechanics,
            it.description
        )
    }

    val eventLiveData = MediatorLiveData<Event>()

    init {
        eventLiveData.addSource(registrationServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.registrationErrorHandler(it as ServerError)

                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }

                eventLiveData.postValue(event)
            } else if (it is Token) {
                TokenRepository.setServerToken(it)
                CacheRepository.setIsLoggedIn(true)
                eventLiveData.postValue(Event(EventType.Move, "MyProfile"))
            }
        }
    }

    fun register() {
        val location = locationLiveData.value ?: ""
        val description = descriptionLiveData.value ?: ""

        if (!checkFields(location, description)) {
            return
        }

        val token = TokenRepository.getVKToken()
        registrationDataLiveData.postValue(
            RegistrationData(
                token,
                location,
                description,
                categories,
                mechanics
            )
        )
    }

    fun updateCategories(items: List<KeyPairBoolData>) {
        categories = PreferencesRepository.convertToList(items)
    }

    fun updateMechanics(items: List<KeyPairBoolData>) {
        mechanics = PreferencesRepository.convertToList(items)
    }

    private fun checkFields(location: String, description: String): Boolean {
        if (location.isBlank()) {
            eventLiveData.postValue(Event(EventType.Warning, "Укажите местоположение"))
            return false
        }

        if (description.length > MAX_LENGTH_OF_DESCRIPTION) {
            eventLiveData.postValue(
                Event(
                    EventType.Warning,
                    "Описание должно содержать не более 250 символов"
                )
            )
            return false
        }

        return true
    }

    private data class RegistrationData(
        val token: Token,
        val location: String,
        val description: String,
        val categories: List<String>,
        val mechanics: List<String>,
    )

    companion object {
        const val MAX_LENGTH_OF_DESCRIPTION = 250
    }
}
