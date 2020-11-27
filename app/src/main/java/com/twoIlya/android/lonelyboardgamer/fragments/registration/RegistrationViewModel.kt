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
    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    fun updateLocation(address: String) {
        _location.postValue(address)
    }

    private var categories = listOf<String>()
    private var mechanics = listOf<String>()

    val description = MutableLiveData<String>()

    private val registrationData = MutableLiveData<RegistrationData>()
    private val registrationServerResponse = Transformations.switchMap(registrationData) {
        ServerRepository.register(
            it.token,
            it.location,
            it.categories,
            it.mechanics,
            it.description
        )
    }

    val events = MediatorLiveData<Event>()

    init {
        events.addSource(registrationServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.registrationErrorHandler(it as ServerError)

                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }

                events.postValue(event)
            } else if (it is Token) {
                TokenRepository.setServerToken(it)
                CacheRepository.setIsLoggedIn(true)
                events.postValue(Event(EventType.Move, "MyProfile"))
            }
        }
    }

    fun register() {
        val location = location.value ?: ""
        val description = description.value ?: ""

        if (!checkFields(location, description)) {
            return
        }

        val token = TokenRepository.getVKToken()
        registrationData.postValue(
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
            events.postValue(Event(EventType.Warning, "Укажите местоположение"))
            return false
        }

        if (description.length > MAX_LENGTH_OF_DESCRIPTION) {
            events.postValue(
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
