package com.twoIlya.android.lonelyboardgamer.fragments.registration

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class RegistrationViewModel : ViewModel() {
    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _mechanics = MutableLiveData<List<String>>()
    val mechanics: LiveData<List<String>> = _mechanics

    private val _aboutMe = MutableLiveData<String>()
    val aboutMe: LiveData<String> = _aboutMe

    private val _isFormEnabled = MutableLiveData(true)
    val isFormEnabled: LiveData<Boolean> = _isFormEnabled

    private val _isButtonLoading = MutableLiveData(false)
    val isButtonLoading: LiveData<Boolean> = _isButtonLoading

    private val registrationData = MutableLiveData<RegistrationData>()
    private val registrationServerResponse = Transformations.switchMap(registrationData) {
        ServerRepository.register(
            it.token,
            it.address,
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
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is Token) {
                TokenRepository.setServerToken(it)
                CacheRepository.setIsLoggedIn(true)
                events.postValue(Event(Event.Type.Move, "MyProfile"))
            }
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }
    }

    fun register() {
        updateForm(isFormEnabled = false, isButtonLoading = true)

        val address = address.value ?: ""
        val description = aboutMe.value ?: ""
        val categories = categories.value ?: emptyList()
        val mechanics = mechanics.value ?: emptyList()

        if (!checkFields(address, description)) {
            updateForm(isFormEnabled = true, isButtonLoading = false)
            return
        }

        val token = TokenRepository.getVKToken()
        registrationData.postValue(
            RegistrationData(
                token,
                address,
                description,
                categories,
                mechanics
            )
        )
    }

    fun updateCategories(indices: IntArray) {
        _categories.postValue(PreferencesRepository.convertToCategoriesList(indices))
    }

    fun updateMechanics(indices: IntArray) {
        _mechanics.postValue(PreferencesRepository.convertToMechanicsList(indices))
    }

    fun updateAddress(address: String) {
        _address.postValue(address)
    }

    fun updateAboutMe(description: String) {
        _aboutMe.postValue(description)
    }

    private fun checkFields(address: String, description: String): Boolean {
        if (address.isBlank()) {
            events.postValue(Event(Event.Type.Notification, "Укажите местоположение"))
            return false
        }

        if (description.length > MAX_LENGTH_OF_DESCRIPTION) {
            events.postValue(
                Event(
                    Event.Type.Notification,
                    "Описание должно содержать не более 250 символов"
                )
            )
            return false
        }

        return true
    }

    private fun updateForm(isFormEnabled: Boolean, isButtonLoading: Boolean) {
        _isFormEnabled.postValue(isFormEnabled)
        _isButtonLoading.postValue(isButtonLoading)
    }

    private data class RegistrationData(
        val token: Token,
        val address: String,
        val description: String,
        val categories: List<String>,
        val mechanics: List<String>,
    )

    companion object {
        const val MAX_LENGTH_OF_DESCRIPTION = 250
    }
}
