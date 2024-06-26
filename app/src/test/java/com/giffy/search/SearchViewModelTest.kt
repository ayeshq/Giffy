package com.giffy.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.MainCoroutineRule
import com.getOrAwaitValueTest
import com.giffy.service.GiffyRepositoryMock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var repo: GiffyRepositoryMock

    @Before
    fun setup() {
        repo = GiffyRepositoryMock()
        viewModel = SearchViewModel(repo)
    }

    @Test
    fun `empty search query will change the state to NoSearch`() {
        repo.empty = false
        repo.error = false

        viewModel.searchGifs("")
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.NoSearch)
    }

    @Test
    fun `empty search result will change the state to Empty`() {
        repo.empty = true
        repo.error = false

        viewModel.searchGifs("Something")
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.Empty)
    }

    @Test
    fun `catching an error while searching will change the state to Error`() {
        repo.empty = false
        repo.error = true

        viewModel.searchGifs("Something")
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.Error)
    }

    @Test
    fun `non-empty search results will change the state to Preview`() {
        repo.empty = false
        repo.error = false

        viewModel.searchGifs("Something")
        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.Preview)
    }

    @Test
    fun `clearing the search results after non-empty search results will change the state to NoSearch`() {
        repo.empty = false //Load real results to change the state to preview first
        repo.error = false

        viewModel.searchGifs("Something")
        var state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.Preview)

        viewModel.clearSearchResults()
        state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(SearchViewModel.State.NoSearch)
    }
}
