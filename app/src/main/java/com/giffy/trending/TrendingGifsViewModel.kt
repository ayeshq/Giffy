package com.giffy.trending

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
class TrendingGifsViewModel @Inject constructor(
    private val repository: GiffyRepository
) : ViewModel() {

    private val _trendingGifs = MutableLiveData<List<Gif>?>()
    val trendingGifs : LiveData<List<Gif>?> = _trendingGifs

    private val _state = MutableLiveData(State.Loading)
    val state : LiveData<State> = _state

    fun loadTrendingGifs() {
        val errorHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            updateState(State.Error)
        }

        viewModelScope.launch(errorHandler) {
            updateState(State.Loading)

            repository.trendingGifs().collect { gifsList ->
                if (gifsList.isNullOrEmpty()) {
                    updateState(State.Empty)
                } else {
                    _trendingGifs.value = gifsList
                    updateState(State.Preview)
                }
            }
        }
    }

    private fun updateState(state: State) {
        _state.value = state
    }

    enum class State {

        Loading,

        Error,

        Empty,

        Preview
    }
}
