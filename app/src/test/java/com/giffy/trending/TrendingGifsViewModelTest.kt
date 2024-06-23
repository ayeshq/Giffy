package com.giffy.trending

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.MainCoroutineRule
import com.getOrAwaitValueTest
import com.giffy.service.GiffyRepositoryMock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TrendingGifsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TrendingGifsViewModel
    private lateinit var repo: GiffyRepositoryMock

    @Before
    fun setup() {
        repo = GiffyRepositoryMock()
        viewModel = TrendingGifsViewModel(repo)
    }

    @Test
    fun `empty trending gifs list will change the state to Empty`() {
        repo.empty = true
        repo.error = false

        viewModel.loadTrendingGifs()
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(TrendingGifsViewModel.State.Empty)
    }

    @Test
    fun `non-empty trending gifs list will change the state to Preview`() {
        repo.empty = false
        repo.error = false

        viewModel.loadTrendingGifs()
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(TrendingGifsViewModel.State.Preview)
    }

    @Test
    fun `catching an error while loading trending gifs will change the state to Error`() {
        repo.empty = false
        repo.error = true

        viewModel.loadTrendingGifs()
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(TrendingGifsViewModel.State.Error)
    }
}
