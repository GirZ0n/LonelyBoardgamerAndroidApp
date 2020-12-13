package com.twoIlya.android.lonelyboardgamer.fragments.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository

class UserProfileViewModel : ViewModel() {
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

    private val _isButtonLoading = MutableLiveData(false)
    val isButtonLoading: LiveData<Boolean> = _isButtonLoading

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


}