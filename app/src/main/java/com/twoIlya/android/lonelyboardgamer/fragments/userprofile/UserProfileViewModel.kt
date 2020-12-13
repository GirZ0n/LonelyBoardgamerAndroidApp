package com.twoIlya.android.lonelyboardgamer.fragments.userprofile

import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class UserProfileViewModel : ViewModel() {
    private var state: State = LoadingState()

    private var id: Int = 0

    private var idVK: String = ""

    private var friendStatus: Int = 0

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

    private val _isFormEnabled = MutableLiveData(true)
    val isFormEnabled: LiveData<Boolean> = _isFormEnabled

    private val _isBottomButtonLoading = MutableLiveData(false)
    val isBottomButtonLoading: LiveData<Boolean> = _isBottomButtonLoading

    private val _isUpButtonLoading = MutableLiveData(false)
    val isUpButtonLoading: LiveData<Boolean> = _isUpButtonLoading

    private val _stateCode = MutableLiveData(0)
    val stateCode: LiveData<Int> = _stateCode

    // -----------------------------------------------------

    // Data: token and id
    private val dataForSearchByID = MutableLiveData<Pair<Token, Int>>()
    private val searchByIDServerResponse = Transformations.switchMap(dataForSearchByID) {
        ServerRepository.searchByID(it.first, it.second)
    }

    // Data: token and id
    private val dataForSendFriendRequest = MutableLiveData<Pair<Token, Int>>()
    private val sendFriendRequestServerResponse = Transformations.switchMap(dataForSendFriendRequest) {
        ServerRepository.sendFriendRequest(it.first, it.second)
    }

    // Data: token, id and isAccept
    private val dataForAnswerOnRequest = MutableLiveData<Pair<Token, Pair<Int, Boolean>>>()
    private val answerOnRequestServerResponse = Transformations.switchMap(dataForAnswerOnRequest) {
        ServerRepository.answerOnRequest(it.first, it.second.first, it.second.second)
    }

    // -----------------------------------------------------

    val events = MediatorLiveData<Event>()

    init {
        events.addSource(searchByIDServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.searchByIDErrorHandler(it as ServerError)
                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is UserProfile) {
                updateLiveData(it)

                state = when (it.friendStatus) {
                    3 -> {
                        _stateCode.postValue(3)
                        FriendState()
                    }
                    2 -> {
                        _stateCode.postValue(2)
                        InRequestState()
                    }
                    1 -> {
                        _stateCode.postValue(1)
                        OutRequestState()
                    }
                    else -> {
                        _stateCode.postValue(1)
                        ForeignUserState()
                    }
                }
            }

            _isLayoutRefreshing.postValue(false)
            updateForm(isFormEnabled = true, isBottomButtonLoading = false, isUpButtonLoading = false)
        }

        events.addSource(sendFriendRequestServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.sendFriendRequestErrorHandler(it as ServerError)
                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            }

            updateForm(isFormEnabled = true, isBottomButtonLoading = false, isUpButtonLoading = false)
        }

        events.addSource(answerOnRequestServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.answerOnRequestErrorHandler(it as ServerError)
                if (event.type == EventType.Move || event.type == EventType.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                idVK = it.value
            }

            updateForm(isFormEnabled = true, isBottomButtonLoading = false, isUpButtonLoading = false)
        }
    }

    fun updateProfile(id: Int) {
        _isLayoutRefreshing.postValue(true)
        updateForm(isFormEnabled = false, isBottomButtonLoading = false, isUpButtonLoading = false)
        val serverToken = TokenRepository.getServerToken()
        dataForSearchByID.postValue(Pair(serverToken, id))
    }

    fun bottomButtonClick(action: String) {
        state.bottomButtonClick(action)
    }

    fun upButtonClick(action: String) {
        state.bottomButtonClick(action)
    }

    private fun updateLiveData(profile: UserProfile) {
        id = profile.id
        profile.idVK?.let { idVK = it }
        friendStatus = profile.friendStatus

        val fullName = "${profile.firstName} ${profile.secondName}"
        _name.postValue(fullName)
        _categories.postValue(profile.categories)
        _mechanics.postValue(profile.mechanics)
        _description.postValue(profile.description)

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

    private fun updateForm(isFormEnabled: Boolean, isBottomButtonLoading: Boolean, isUpButtonLoading: Boolean) {
        _isFormEnabled.postValue(isFormEnabled)
        _isBottomButtonLoading.postValue(isBottomButtonLoading)
        _isUpButtonLoading.postValue(isUpButtonLoading)
    }

    // -----------------------------------------------------

    private interface State {
        fun bottomButtonClick(action: String) {
            return
        }

        fun upButtonCLick(action: String) {
            return
        }
    }

    // Init state
    inner class LoadingState : State

    inner class FriendState : State {
        override fun bottomButtonClick(action: String) {
            events.postValue(Event(EventType.Move, idVK))
        }

        override fun upButtonCLick(action: String) {
            updateForm(isFormEnabled = false, isBottomButtonLoading = false, isUpButtonLoading = true)
            // TODO: убрать из друзей
        }
    }

    inner class OutRequestState : State {
        override fun bottomButtonClick(action: String) {
            updateForm(isFormEnabled = false, isBottomButtonLoading = true, isUpButtonLoading = false)
            // TODO: отозвать запрос
        }
    }

    inner class InRequestState : State {
        override fun bottomButtonClick(action: String) {
            updateForm(isFormEnabled = false, isBottomButtonLoading = true, isUpButtonLoading = false)

            val serverToken = TokenRepository.getServerToken()
            when (action) {
                "accept" -> dataForAnswerOnRequest.postValue(Pair(serverToken, Pair(id, true)))
                "decline" -> dataForAnswerOnRequest.postValue(Pair(serverToken, Pair(id, false)))
                else -> {
                    events.postValue(Event(
                            EventType.Notification,
                            "Что-то пошло не так во время обработки вашего запроса")
                    )
                    updateForm(isFormEnabled = true, isBottomButtonLoading = false, isUpButtonLoading = false)
                }
            }
        }
    }

    inner class ForeignUserState : State {
        override fun bottomButtonClick(action: String) {
            updateForm(isFormEnabled = false, isBottomButtonLoading = true, isUpButtonLoading = false)

            val serverToken = TokenRepository.getServerToken()
            dataForSendFriendRequest.postValue(Pair(serverToken, id))
        }
    }
}