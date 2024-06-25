package com.giffy.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giffy.model.Gif
import com.giffy.service.GiffyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val giffyRepository: GiffyRepository
) : ViewModel() {

    private val _state = MutableLiveData(State.NoSearch)
    val state : LiveData<State> = _state

    private val _searchResult = MutableLiveData<List<Gif>?>()
    val searchResult : LiveData<List<Gif>?> = _searchResult

    private val searchErrorHandler = CoroutineExceptionHandler { _, error ->
        updateState(State.Error)
        error.printStackTrace()
    }

    fun searchGifs(query: String) {
        if (query.isEmpty()) {
            clearSearchResults()
            return
        }

        viewModelScope.launch(searchErrorHandler) {
            giffyRepository
                .searchGif(query)
                .collect { gifsList ->
                    if (gifsList.isNullOrEmpty()) {
                        updateState(State.Empty)
                    } else {
                        updateState(State.Preview)
                    }

                    _searchResult.value = gifsList
                }
        }
    }

    fun clearSearchResults() {
        updateState(State.NoSearch)
        _searchResult.value = null
    }

    private fun updateState(state: State) {
        _state.value = state
    }

    enum class State {
        NoSearch,
        Error,
        Preview,
        Empty
    }
}
