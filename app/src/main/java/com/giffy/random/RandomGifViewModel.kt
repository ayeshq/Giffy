package com.giffy.random

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giffy.model.Gif
import com.giffy.service.GiffyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

private const val REFRESH_INTERVAL_MILLIS = 10 * 1000L

@HiltViewModel
class RandomGifViewModel @Inject constructor(
    private val giffyRepository: GiffyRepository
) : ViewModel() {

    private val _randomGif = MutableLiveData<Gif?>()
    val randomGif : LiveData<Gif?> = _randomGif

    private val _state = MutableLiveData(State.Loading)
    val state : LiveData<State> = _state

    private var randomGifLoadingJob: Job? = null

    private val gifLoadingErrorHandler = CoroutineExceptionHandler { _, error ->
        updateState(State.Error)
        stopAutoLoadingRandomGifs()
        error.printStackTrace()
    }

    fun startAutoLoadingRandomGifs() {
        stopAutoLoadingRandomGifs()

        randomGifLoadingJob = viewModelScope.launch(gifLoadingErrorHandler) {
            while (true) {
                loadRandomGif()
                delay(REFRESH_INTERVAL_MILLIS)
            }
        }
    }

    fun stopAutoLoadingRandomGifs() {
        randomGifLoadingJob?.apply {
            if (isActive) {
                cancel()
            }
        }
    }

    @VisibleForTesting
    fun loadRandomGif() {
        viewModelScope.launch(gifLoadingErrorHandler) {
            updateState(State.Loading)

            giffyRepository.randomGif().collect { gif: Gif? ->
                if (gif == null) {
                    updateState(State.Error)
                } else {
                    previewGif(gif)
                }
            }
        }
    }

    private fun previewGif(gif: Gif) {
        _randomGif.value = gif
        updateState(State.Preview)
    }

    private fun updateState(state: State) {
        _state.value = state
    }

    enum class State {
        Loading,
        Preview,
        Error
    }
}
