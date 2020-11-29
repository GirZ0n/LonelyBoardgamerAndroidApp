package com.twoIlya.android.lonelyboardgamer.fragments.editprofile

import androidx.lifecycle.*
import com.androidbuts.multispinnerfilter.KeyPairBoolData
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

    private lateinit var oldDescription: String
    val description = MutableLiveData<String>()

    val isFormEnabled = MutableLiveData(true)

    private val _isLayoutLoading = MutableLiveData(false)
    val isLayoutLoading: LiveData<Boolean> = _isLayoutLoading

    private val _isButtonLoading = MutableLiveData(false)
    val isButtonLoading: LiveData<Boolean> = _isButtonLoading

    private val serverTokenForGetProfile = MutableLiveData<Token>()
    private val getProfileServerResponse = Transformations.switchMap(serverTokenForGetProfile) {
        ServerRepository.getProfile(it)
    }

    private val dataForChangeAddress = MutableLiveData<Pair<Token, String>>()
    private val changeAddressServerResponse =
        Transformations.switchMap(dataForChangeAddress) {
            ServerRepository.changeAddress(it.first, it.second)
        }

    private val dataForChangeCategories = MutableLiveData<Pair<Token, List<String>>>()
    private val changeCategoriesServerResponse =
        Transformations.switchMap(dataForChangeCategories) {
            ServerRepository.changeCategories(it.first, it.second)
        }

    private val dataForChangeMechanics = MutableLiveData<Pair<Token, List<String>>>()
    private val changeMechanicsServerResponse =
        Transformations.switchMap(dataForChangeMechanics) {
            ServerRepository.changeMechanics(it.first, it.second)
        }

    private val dataForChangeDescription = MutableLiveData<Pair<Token, String>>()
    private val changeDescriptionServerResponse =
        Transformations.switchMap(dataForChangeDescription) {
            ServerRepository.changeDescription(it.first, it.second)
        }

    val events = MediatorLiveData<Event>()

    init {
        _isLayoutLoading.postValue(true)

        CacheRepository.getProfile()?.let {
            initLiveData(it)
            _isLayoutLoading.postValue(false)
        } ?: serverTokenForGetProfile.postValue(TokenRepository.getServerToken())

        events.addSource(getProfileServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.getProfileErrorHandler(it as ServerError)

                when (event.type) {
                    EventType.Move, EventType.Error -> CacheRepository.setIsLoggedIn(false)
                    EventType.Warning -> isFormEnabled.postValue(true)
                }

                events.postValue(event)
            } else if (it is Profile) {
                CacheRepository.setProfile(it)
                initLiveData(it)
            }

            _isLayoutLoading.postValue(false)
        }

        events.addSource(changeAddressServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)

                when (event.type) {
                    EventType.Move, EventType.Error -> CacheRepository.setIsLoggedIn(false)
                    EventType.Warning -> updateForm(true)
                }

                events.postValue(event)
            } else if (it is ServerMessage) {
                _address.value?.let { address ->
                    CacheRepository.setAddress(address)
                    oldAddress = address
                }
                updateForm(true)
                events.postValue(Event(EventType.Warning, "Адрес изменён"))
            }
        }

        events.addSource(changeCategoriesServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)

                when (event.type) {
                    EventType.Move, EventType.Error -> CacheRepository.setIsLoggedIn(false)
                    EventType.Warning -> updateForm(true)
                }

                events.postValue(event)
            } else if (it is ServerMessage) {
                _categories.value?.let { categories ->
                    CacheRepository.setCategories(categories)
                    oldCategories = categories
                }
                updateForm(true)
                events.postValue(Event(EventType.Warning, "Категории изменены"))
            }
        }

        events.addSource(changeMechanicsServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)

                when (event.type) {
                    EventType.Move, EventType.Error -> CacheRepository.setIsLoggedIn(false)
                    EventType.Warning -> updateForm(true)
                }

                events.postValue(event)
            } else if (it is ServerMessage) {
                _mechanics.value?.let { mechanics ->
                    CacheRepository.setMechanics(mechanics)
                    oldMechanics = mechanics
                }
                updateForm(true)
                events.postValue(Event(EventType.Warning, "Механики изменены"))
            }
        }

        events.addSource(changeDescriptionServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.changeProfileErrorHandler(it as ServerError)

                when (event.type) {
                    EventType.Move, EventType.Error -> CacheRepository.setIsLoggedIn(false)
                    EventType.Warning -> updateForm(true)
                }

                events.postValue(event)
            } else if (it is ServerMessage) {
                description.value?.let { description ->
                    CacheRepository.setDescription(description)
                    oldDescription = description
                }
                updateForm(true)
                events.postValue(Event(EventType.Warning, "Описание изменено"))
            }
        }
    }

    fun edit() {
        updateForm(false)

        val address = address.value ?: ""
        val description = description.value ?: ""
        val categories = categories.value ?: emptyList()
        val mechanics = mechanics.value ?: emptyList()

        if (!checkFields(address, description, categories, mechanics)) {
            updateForm(true)
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

        if (oldDescription != description) {
            dataForChangeDescription.postValue(Pair(serverToken, description))
        }
    }

    fun updateAddress(address: String) {
        _address.postValue(address)
    }

    fun updateCategories(items: List<KeyPairBoolData>) {
        _categories.postValue(PreferencesRepository.convertToList(items))
    }

    fun updateMechanics(items: List<KeyPairBoolData>) {
        _mechanics.postValue(PreferencesRepository.convertToList(items))
    }

    fun updateProfile() {
        serverTokenForGetProfile.postValue(TokenRepository.getServerToken())
    }

    private fun updateForm(isEnabled: Boolean) {
        isFormEnabled.postValue(isEnabled)
        _isButtonLoading.postValue(!isEnabled)
    }

    private fun checkFields(
        address: String,
        description: String,
        categories: List<String>,
        mechanics: List<String>
    ): Boolean {
        return when {
            address.isBlank() -> {
                events.postValue(Event(EventType.Warning, "Укажите местоположение"))
                false
            }
            description.length > MAX_LENGTH_OF_DESCRIPTION -> {
                events.postValue(
                    Event(
                        EventType.Warning,
                        "Описание должно содержать не более 250 символов"
                    )
                )
                false
            }
            oldAddress == address &&
                    oldCategories == categories &&
                    oldMechanics == mechanics &&
                    oldDescription == description -> {
                false
            }
            else -> true
        }
    }

    private fun initLiveData(profile: Profile) {
        oldAddress = profile.address
        _address.postValue(profile.address)

        oldCategories = profile.categories
        _categories.postValue(profile.categories)

        oldMechanics = profile.mechanics
        _mechanics.postValue(profile.mechanics)

        oldDescription = profile.description
        description.postValue(profile.description)
    }

    companion object {
        const val MAX_LENGTH_OF_DESCRIPTION = 250
    }
}
