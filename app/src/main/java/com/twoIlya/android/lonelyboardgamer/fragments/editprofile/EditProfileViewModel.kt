package com.twoIlya.android.lonelyboardgamer.fragments.editprofile

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.PreferencesRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class EditProfileViewModel : ViewModel() {
    private lateinit var oldAddress: String
    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private lateinit var oldCategories: List<String>
    private val _categories = MutableLiveData<List<String>>()
    var categories: LiveData<List<String>> = _categories

    private lateinit var oldMechanics: List<String>
    private val _mechanics = MutableLiveData<List<String>>()
    var mechanics: LiveData<List<String>> = _mechanics

    private lateinit var oldAboutMe: String
    private val _aboutMe = MutableLiveData<String>()
    val aboutMe: LiveData<String> = _aboutMe

    private val _isFormEnabled = MutableLiveData(true)
    val isFormEnabled: LiveData<Boolean> = _isFormEnabled

    private val _isLayoutLoading = MutableLiveData(false)
    val isLayoutLoading: LiveData<Boolean> = _isLayoutLoading

    private val _isButtonLoading = MutableLiveData(false)
    val isButtonLoading: LiveData<Boolean> = _isButtonLoading

    private val serverTokenForGetProfile = MutableLiveData<Token>()
    private val getProfileServerResponse = Transformations.switchMap(serverTokenForGetProfile) {
        ServerRepository.getProfile(it)
    }

    // Data: token and new address
    private val dataForChangeAddress = MutableLiveData<Pair<Token, String>>()
    private val changeAddressServerResponse =
        Transformations.switchMap(dataForChangeAddress) {
            ServerRepository.changeAddress(it.first, it.second)
        }

    // Data: token and new category list
    private val dataForChangeCategories = MutableLiveData<Pair<Token, List<String>>>()
    private val changeCategoriesServerResponse =
        Transformations.switchMap(dataForChangeCategories) {
            ServerRepository.changeCategories(it.first, it.second)
        }

    // Data: token and new mechanics list
    private val dataForChangeMechanics = MutableLiveData<Pair<Token, List<String>>>()
    private val changeMechanicsServerResponse =
        Transformations.switchMap(dataForChangeMechanics) {
            ServerRepository.changeMechanics(it.first, it.second)
        }

    // Data: token and new description
    private val dataForChangeDescription = MutableLiveData<Pair<Token, String>>()
    private val changeDescriptionServerResponse =
        Transformations.switchMap(dataForChangeDescription) {
            ServerRepository.changeDescription(it.first, it.second)
        }

    val events = MediatorLiveData<Event>()

    init {
        _isLayoutLoading.postValue(true)
        updateForm(isFormEnabled = false, isButtonLoading = false)

        CacheRepository.getProfile()?.let {
            updateLiveData(it)
            _isLayoutLoading.postValue(false)
            updateForm(isFormEnabled = true, isButtonLoading = false)
        } ?: serverTokenForGetProfile.postValue(TokenRepository.getServerToken())

        events.addSource(getProfileServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.getProfileErrorHandler(it as ServerError)
                if (event.type == Event.Type.MOVE || event.type == Event.Type.ERROR) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is MyProfile) {
                CacheRepository.setProfile(it)
                updateLiveData(it)
            }

            _isLayoutLoading.postValue(false)
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }

        events.addSource(changeAddressServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)
                if (event.type == Event.Type.MOVE || event.type == Event.Type.ERROR) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                _address.value?.let { address ->
                    CacheRepository.setAddress(address)
                    oldAddress = address
                }
                events.postValue(Event(Event.Type.NOTIFICATION, "Адрес изменён"))
            }
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }

        events.addSource(changeCategoriesServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)
                if (event.type == Event.Type.MOVE || event.type == Event.Type.ERROR) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                _categories.value?.let { categories ->
                    CacheRepository.setCategories(categories)
                    oldCategories = categories
                }
                events.postValue(Event(Event.Type.NOTIFICATION, "Категории изменены"))
            }
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }

        events.addSource(changeMechanicsServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)
                if (event.type == Event.Type.MOVE || event.type == Event.Type.ERROR) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                _mechanics.value?.let { mechanics ->
                    CacheRepository.setMechanics(mechanics)
                    oldMechanics = mechanics
                }
                events.postValue(Event(Event.Type.NOTIFICATION, "Механики изменены"))
            }
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }

        events.addSource(changeDescriptionServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)
                if (event.type == Event.Type.MOVE || event.type == Event.Type.ERROR) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                aboutMe.value?.let { description ->
                    CacheRepository.setDescription(description)
                    oldAboutMe = description
                }
                events.postValue(Event(Event.Type.NOTIFICATION, "Описание изменено"))
            }
            updateForm(isFormEnabled = true, isButtonLoading = false)
        }
    }

    fun edit() {
        updateForm(isFormEnabled = false, isButtonLoading = true)

        val address = address.value ?: ""
        val description = aboutMe.value ?: ""
        val categories = categories.value ?: emptyList()
        val mechanics = mechanics.value ?: emptyList()

        if (!checkFields(address, description, categories, mechanics)) {
            updateForm(isFormEnabled = true, isButtonLoading = false)
            return
        }

        val serverToken = TokenRepository.getServerToken()

        if (oldAddress != address) {
            dataForChangeAddress.postValue(Pair(serverToken, address))
        }

        if (oldCategories != categories) {
            dataForChangeCategories.postValue(Pair(serverToken, categories))
        }

        if (oldMechanics != mechanics) {
            dataForChangeMechanics.postValue(Pair(serverToken, mechanics))
        }

        if (oldAboutMe != description) {
            dataForChangeDescription.postValue(Pair(serverToken, description))
        }
    }

    fun updateAddress(address: String) {
        _address.postValue(address)
    }

    fun updateCategories(indices: IntArray) {
        _categories.postValue(PreferencesRepository.convertToCategoriesList(indices))
    }

    fun updateMechanics(indices: IntArray) {
        _mechanics.postValue(PreferencesRepository.convertToMechanicsList(indices))
    }

    fun updateAboutMe(description: String) {
        _aboutMe.postValue(description)
    }

    fun updateProfile() {
        serverTokenForGetProfile.postValue(TokenRepository.getServerToken())
    }

    private fun updateForm(isFormEnabled: Boolean, isButtonLoading: Boolean) {
        _isFormEnabled.postValue(isFormEnabled)
        _isButtonLoading.postValue(isButtonLoading)
    }

    private fun checkFields(
        address: String,
        description: String,
        categories: List<String>,
        mechanics: List<String>
    ): Boolean {
        return when {
            address.isBlank() -> {
                events.postValue(Event(Event.Type.NOTIFICATION, "Укажите местоположение"))
                false
            }
            description.length > MAX_LENGTH_OF_DESCRIPTION -> {
                events.postValue(
                    Event(
                        Event.Type.NOTIFICATION,
                        "Описание должно содержать не более 250 символов"
                    )
                )
                false
            }
            !isSomethingChanged(address, categories, mechanics, description) -> false
            else -> true
        }
    }

    private fun isSomethingChanged(
        address: String,
        categories: List<String>,
        mechanics: List<String>,
        description: String
    ): Boolean {
        return oldAddress != address || oldCategories != categories ||
                oldMechanics != mechanics || oldAboutMe != description
    }

    private fun updateLiveData(profile: MyProfile) {
        oldAddress = profile.address
        _address.postValue(profile.address)

        oldCategories = profile.categories
        _categories.postValue(profile.categories)

        oldMechanics = profile.mechanics
        _mechanics.postValue(profile.mechanics)

        oldAboutMe = profile.description
        _aboutMe.postValue(profile.description)
    }

    companion object {
        const val MAX_LENGTH_OF_DESCRIPTION = 250
    }
}
