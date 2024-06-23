package com.giffy.random

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.MainCoroutineRule
import com.getOrAwaitValueTest
import com.giffy.service.GiffyRepositoryMock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RandomGifViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RandomGifViewModel
    private lateinit var repo: GiffyRepositoryMock

    @Before
    fun setup() {
        repo = GiffyRepositoryMock()
        viewModel = RandomGifViewModel(repo)
    }

    @Test
    fun `loading a Random Gif successfully changes state to Preview`() {
        viewModel.loadRandomGif()
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(RandomGifViewModel.State.Preview)
    }

    @Test
    fun `catching an error while loading a Random Gif changes state to Error`() {
        repo.error = true

        viewModel.loadRandomGif()
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(RandomGifViewModel.State.Error)
    }

    @Test
    fun `automatically loading a new gif will change the state to preview!`() {
        viewModel.stopAutoLoadingRandomGifs()

        //Intentionally changing the state to "error" first, to assert it will change to "preview" later
        repo.error = true
        viewModel.loadRandomGif()
        var state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(RandomGifViewModel.State.Error)

        //Now call startAutoLoadingRandomGifs with no error, and assert the next state will be "preview"
        repo.error = false
        viewModel.startAutoLoadingRandomGifs()
        state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(RandomGifViewModel.State.Preview)

        viewModel.stopAutoLoadingRandomGifs()
    }
}
