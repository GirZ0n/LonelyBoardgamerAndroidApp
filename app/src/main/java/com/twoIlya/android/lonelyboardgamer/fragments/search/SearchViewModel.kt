package com.twoIlya.android.lonelyboardgamer.fragments.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.twoIlya.android.lonelyboardgamer.dataClasses.SearchProfile
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import com.twoIlya.android.lonelyboardgamer.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel : ViewModel() {
    private val repo = ServerRepository

    fun search(): Flow<PagingData<SearchProfile>> {
        return repo.search(TokenRepository.getServerToken())
            .cachedIn(viewModelScope)
    }
}
