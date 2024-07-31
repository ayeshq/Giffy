package com.giffy.details

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
class GifDetailsViewModel @Inject constructor(
    private val giffyRepository: GiffyRepository
) : ViewModel() {

    private val _gif = MutableLiveData<Gif>()
    val gif: LiveData<Gif> = _gif

    private val _state = MutableLiveData(State.Loading)
    val state: LiveData<State> = _state

    fun loadGifDetails(gifId: String) {
        _state.value = State.Loading

        val errorHandler = CoroutineExceptionHandler { _, error ->
            error.printStackTrace()
            _state.value = State.Error
        }

        viewModelScope.launch(errorHandler) {
            giffyRepository.gifById(gifId).collect { gif ->
                if (gif == null) {
                    _state.value = State.Error
                } else {
                    _gif.value = gif!!
                    _state.value = State.Preview
                }
            }
        }
    }

    enum class State {
        Loading,
        Preview,
        Error
    }
}
