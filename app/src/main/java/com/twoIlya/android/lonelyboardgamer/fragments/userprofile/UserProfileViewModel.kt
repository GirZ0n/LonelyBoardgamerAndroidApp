package com.twoIlya.android.lonelyboardgamer.fragments.userprofile

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class UserProfileViewModel : ViewModel() {
    private var currentState: State = LoadingState()

    var id: Int = 0

    private var idVK: String = ""

    private var _friendStatus = MutableLiveData(FriendStatus.Loading)
    val friendStatus: LiveData<FriendStatus> = _friendStatus

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

    private val _isLayoutEnabled = MutableLiveData(true)
    val isLayoutEnabled: LiveData<Boolean> = _isLayoutEnabled

    private val _isBottomButtonLoading = MutableLiveData(false)
    val isBottomButtonLoading: LiveData<Boolean> = _isBottomButtonLoading

    // -----------------------------------------------------

    // Data: token and id
    private val dataForSearchByID = MutableLiveData<Pair<Token, Int>>()
    private val searchByIDServerResponse = Transformations.switchMap(dataForSearchByID) {
        ServerRepository.searchByID(it.first, it.second)
    }

    // Data: token and id
    private val dataForSendFriendRequest = MutableLiveData<Pair<Token, Int>>()
    private val sendFriendRequestServerResponse =
        Transformations.switchMap(dataForSendFriendRequest) {
            ServerRepository.sendFriendRequest(it.first, it.second)
        }

    // Data: token and id
    private val dataForRevokeRequest = MutableLiveData<Pair<Token, Int>>()
    private val revokeRequestServerResponse = Transformations.switchMap(dataForRevokeRequest) {
        ServerRepository.revokeRequest(it.first, it.second)
    }

    // Data: token, id and isAccept
    private val dataForAnswerOnRequest = MutableLiveData<Pair<Token, Pair<Int, Boolean>>>()
    private val answerOnRequestServerResponse = Transformations.switchMap(dataForAnswerOnRequest) {
        ServerRepository.answerOnRequest(it.first, it.second.first, it.second.second)
    }

    // Data: token and id
    private val dataForDeleteFriend = MutableLiveData<Pair<Token, Int>>()
    private val deleteFriendServerResponse = Transformations.switchMap(dataForDeleteFriend) {
        ServerRepository.deleteFriend(it.first, it.second)
    }

    // -----------------------------------------------------

    val events = MediatorLiveData<Event>()

    init {
        events.addSource(searchByIDServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.searchByIDErrorHandler(it as ServerError)
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is UserProfile) {
                updateData(it)

                currentState = when (it.friendStatus) {
                    3 -> FriendState()
                    2 -> InRequestState()
                    1 -> OutRequestState()
                    else -> NoneState()
                }
            }

            updateLayout(
                isLayoutEnabled = true,
                isBottomButtonLoading = false,
                isLayoutRefreshing = false
            )
        }

        events.addSource(sendFriendRequestServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.sendFriendRequestErrorHandler(it as ServerError)
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                currentState = OutRequestState()
                _friendStatus.postValue(FriendStatus.OutRequest)
            }

            updateLayout(isLayoutEnabled = true, isBottomButtonLoading = false)
        }

        events.addSource(answerOnRequestServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.answerOnRequestErrorHandler(it as ServerError)
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                if (it.value.trim { char -> char == '\"' }.isDigitsOnly()) {
                    idVK = it.value.trim { char -> char == '\"' }
                    currentState = FriendState()
                    _friendStatus.postValue(FriendStatus.Friend)
                } else {
                    currentState = InRequestState()
                    _friendStatus.postValue(FriendStatus.InRequest)
                    events.postValue(Event(Event.Type.Notification, "Пользователь скрыт"))
                }
            }

            updateLayout(isLayoutEnabled = true, isBottomButtonLoading = false)
        }

        events.addSource(revokeRequestServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.revokeRequestErrorHandler(it as ServerError)
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                currentState = InRequestState()
                _friendStatus.postValue(FriendStatus.InRequest)
            }

            updateLayout(isLayoutEnabled = true, isBottomButtonLoading = false)
        }

        events.addSource(deleteFriendServerResponse) {
            if (ErrorHandler.isError(it)) {
                val event = ErrorHandler.deleteFriendErrorHandler(it as ServerError)
                if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                    CacheRepository.setIsLoggedIn(false)
                }
                events.postValue(event)
            } else if (it is ServerMessage) {
                currentState = InRequestState()
                _friendStatus.postValue(FriendStatus.InRequest)
            }

            updateLayout(isLayoutEnabled = true, isBottomButtonLoading = false, isLayoutRefreshing = false)
        }
    }

    fun updateProfile() {
        updateLayout(
            isLayoutEnabled = false,
            isBottomButtonLoading = false,
            isLayoutRefreshing = true
        )
        val serverToken = TokenRepository.getServerToken()
        dataForSearchByID.postValue(Pair(serverToken, id))
    }

    fun bottomButtonClick(action: UserProfileAction) {
        currentState.bottomButtonClick(action)
    }

    fun upButtonClick(action: UserProfileAction) {
        currentState.upButtonClick(action)
    }

    private fun updateData(profile: UserProfile) {
        Log.d("UPVM", "profile: $profile")

        id = profile.id
        profile.idVK?.let { idVK = it }

        val friendStatus = FriendStatus.values()[profile.friendStatus + 1]
        _friendStatus.postValue(friendStatus)

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

    private fun updateLayout(
        isLayoutEnabled: Boolean,
        isBottomButtonLoading: Boolean,
        isLayoutRefreshing: Boolean = false,
    ) {
        _isLayoutEnabled.postValue(isLayoutEnabled)
        _isBottomButtonLoading.postValue(isBottomButtonLoading)
        _isLayoutRefreshing.postValue(isLayoutRefreshing)
    }

    // -----------------------------------------------------

    private interface State {
        fun bottomButtonClick(action: UserProfileAction) {
            return
        }

        fun upButtonClick(action: UserProfileAction) {
            return
        }
    }

    // Init state
    inner class LoadingState : State

    inner class FriendState : State {
        override fun bottomButtonClick(action: UserProfileAction) {
            Log.d("UPVM", "id: $idVK")

            when (action) {
                UserProfileAction.CHAT -> events.postValue(Event(Event.Type.Move, idVK))
                else -> events.postValue(
                    Event(
                        Event.Type.Notification,
                        "Что-то пошло не так во время обработки вашего запроса"
                    )
                )
            }
        }

        override fun upButtonClick(action: UserProfileAction) {
            when (action) {
                UserProfileAction.UNFRIEND -> {
                    updateLayout(
                        isLayoutEnabled = false,
                        isBottomButtonLoading = false,
                        isLayoutRefreshing = true
                    )
                    val serverToken = TokenRepository.getServerToken()
                    dataForDeleteFriend.postValue(Pair(serverToken, id))
                }
                else -> {
                    events.postValue(
                        Event(
                            Event.Type.Notification,
                            "Что-то пошло не так во время обработки вашего запроса"
                        )
                    )
                }
            }
        }
    }

    inner class OutRequestState : State {
        override fun bottomButtonClick(action: UserProfileAction) {

            when (action) {
                UserProfileAction.REVOKE -> {
                    updateLayout(isLayoutEnabled = false, isBottomButtonLoading = true)
                    val serverToken = TokenRepository.getServerToken()
                    dataForRevokeRequest.postValue(Pair(serverToken, id))
                }
                else -> {
                    events.postValue(
                        Event(
                            Event.Type.Notification,
                            "Что-то пошло не так во время обработки вашего запроса"
                        )
                    )
                }
            }
        }
    }

    inner class InRequestState : State {
        override fun bottomButtonClick(action: UserProfileAction) {

            val serverToken = TokenRepository.getServerToken()
            when (action) {
                UserProfileAction.ACCEPT -> {
                    updateLayout(isLayoutEnabled = false, isBottomButtonLoading = true)
                    dataForAnswerOnRequest.postValue(Pair(serverToken, Pair(id, true)))
                }
                UserProfileAction.DECLINE -> {
                    updateLayout(isLayoutEnabled = false, isBottomButtonLoading = true)
                    dataForAnswerOnRequest.postValue(Pair(serverToken, Pair(id, false)))
                }
                else -> {
                    events.postValue(
                        Event(
                            Event.Type.Notification,
                            "Что-то пошло не так во время обработки вашего запроса"
                        )
                    )
                }
            }
        }
    }

    inner class NoneState : State {
        override fun bottomButtonClick(action: UserProfileAction) {
            when (action) {
                UserProfileAction.ADD -> {
                    updateLayout(isLayoutEnabled = false, isBottomButtonLoading = true)
                    val serverToken = TokenRepository.getServerToken()
                    dataForSendFriendRequest.postValue(Pair(serverToken, id))
                }
                else -> {
                    events.postValue(
                        Event(
                            Event.Type.Notification,
                            "Что-то пошло не так во время обработки вашего запроса"
                        )
                    )
                }
            }
        }
    }
}
