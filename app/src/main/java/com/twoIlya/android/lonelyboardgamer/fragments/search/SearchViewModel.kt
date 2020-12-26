package com.twoIlya.android.lonelyboardgamer.fragments.search

import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.twoIlya.android.lonelyboardgamer.ErrorHandler
import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.SearchProfile
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.CacheRepository
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository

class SearchViewModel : ViewModel() {
    private val repo = ServerRepository

    private val _isListVisible = MutableLiveData(false)
    val isListVisible: LiveData<Boolean> = _isListVisible

    private val _isProgressBarVisible = MutableLiveData(false)
    val isProgressBarVisible: LiveData<Boolean> = _isProgressBarVisible

    private val _isRetryButtonVisible = MutableLiveData(false)
    val isRetryButtonVisible: LiveData<Boolean> = _isRetryButtonVisible

    private val serverToken = MutableLiveData<Token>()
    val searchLiveData: LiveData<PagingData<SearchProfile>> =
        Transformations.switchMap(serverToken) {
            repo.search(it).cachedIn(viewModelScope)
        }

    private val _events = MutableLiveData<Event>()
    val events: LiveData<Event> = _events

    init {
        search()
    }

    fun search() {
        serverToken.postValue(TokenRepository.getServerToken())
    }

    fun loadStateListener(loadState: CombinedLoadStates) {
        val loadStateRefresh = loadState.source.refresh

        // Only show the list if refresh succeeds.
        _isListVisible.postValue(loadStateRefresh is LoadState.NotLoading)
        // Show loading spinner during initial load or refresh.
        _isProgressBarVisible.postValue(loadStateRefresh is LoadState.Loading)
        // Show the retry state if initial load or refresh fails.
        _isRetryButtonVisible.postValue(loadStateRefresh is LoadState.Error)

        if (loadStateRefresh is LoadState.Error) {
            val event = ErrorHandler.getListErrorHandler(loadStateRefresh.error)
            if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                CacheRepository.setIsLoggedIn(false)
            }
            _events.postValue(event)
        }

        // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
        errorState?.let {
            val event = ErrorHandler.getListErrorHandler(it.error)
            if (event.type == Event.Type.Move || event.type == Event.Type.Error) {
                CacheRepository.setIsLoggedIn(false)
            }
            _events.postValue(event)
        }
    }
}
