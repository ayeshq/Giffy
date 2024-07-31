package com.giffy.trending

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giffy.R
import com.giffy.databinding.FragmentTrendingGifsBinding
import com.giffy.details.GifDetailsFragment
import com.giffy.list.GifsAdapter
import com.giffy.list.GifsGridSpacingItemDecoration
import com.giffy.model.Gif
import com.giffy.random.RandomGifFragment
import com.giffy.search.SearchViewModel
import com.giffy.util.hideKeyboard
import com.giffy.util.textChanges
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

private const val GIF_GRID_ADAPTER_SPAN_COUNT = 3

@AndroidEntryPoint
class TrendingGifsFragment : Fragment() {

    private lateinit var binding: FragmentTrendingGifsBinding

    private val trendingGifsViewModel by viewModels<TrendingGifsViewModel>()
    private val searchViewModel by viewModels<SearchViewModel>()

    private val onGifClickedListener: (gif: Gif) -> Unit = { clickedGif: Gif ->
        val bundle = Bundle()
        bundle.putString(GifDetailsFragment.EXTRA_GIF_ID, clickedGif.id)
        findNavController().navigate(R.id.showGifDetails, bundle, null)
    }

    private val trendingGifsAdapter = GifsAdapter(onGifClickedListener)
    private val searchResultAdapter = GifsAdapter(onGifClickedListener)

    private val gifsListItemDecoration by lazy {
        GifsGridSpacingItemDecoration(
            resources.getDimensionPixelSize(R.dimen.gif_grid_spacing),
            GIF_GRID_ADAPTER_SPAN_COUNT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(context as FragmentActivity) {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    if (binding.searchLayout.searchView.isShowing) {
                        binding.searchLayout.searchView.hide()
                        clearSearchResults()
                    } else {
                        context.finish()
                    }
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTrendingGifsBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            trendingGifsViewModel = this@TrendingGifsFragment.trendingGifsViewModel
            searchViewModel = this@TrendingGifsFragment.searchViewModel
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareTrendingGifs()
        prepareSearch()

        val randomGifFragment = childFragmentManager.findFragmentById(R.id.randomGifFragment) as RandomGifFragment
        randomGifFragment.setOnGifCLickedListener(onGifClickedListener)

        searchViewModel
            .state
            .observe(viewLifecycleOwner) { searchState ->
                searchState?.let {
                    when (it) {
                        SearchViewModel.State.Error,
                        SearchViewModel.State.Preview,
                        SearchViewModel.State.Empty -> {
                            randomGifFragment.stopAutoLoadingRandomGifs()
                        }
                        SearchViewModel.State.NoSearch -> {
                            randomGifFragment.startAutoLoadingRandomGifs()
                        }
                    }
                }
            }
    }

    private fun prepareTrendingGifs() {
        prepareGifsList(binding.trendingGifsLayout.trendingGifsList, trendingGifsAdapter)
        trendingGifsViewModel
            .trendingGifs
            .observe(viewLifecycleOwner) { trendingGifsList ->
                trendingGifsAdapter.submitList(trendingGifsList)
            }

//        trendingGifsViewModel.loadTrendingGifs()
    }

    @OptIn(FlowPreview::class)
    private fun prepareSearch() {
        prepareGifsList(binding.searchLayout.searchResultList, searchResultAdapter)

        binding
            .searchLayout
            .searchView
            .editText
            .apply {
                // Add ime search action listener
                val searchQueryFlow = onImeActionSearch()
                launchSearchQueryFlow(searchQueryFlow)
            }.apply {
                // Add Text changed listener
                val searchQueryFlow = textChanges()
                                // Don't call the search api on every key pressed
                                .debounce(300)
                launchSearchQueryFlow(searchQueryFlow)
            }

        //Hide the keyboard once the user starts scrolling the search result
        binding
            .searchLayout
            .searchResultList
            .addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                        recyclerView.hideKeyboard()
                    }
                }
            })

        //To reset the state of the search view back to SearchViewModel.Sate.NoSearch, clear the search results when the search bar collapses
        binding
            .searchLayout
            .searchView
            .addTransitionListener { _, _, newState ->
                when (newState) {
                    SearchView.TransitionState.HIDING -> {
                        clearSearchResults()
                    }

                    SearchView.TransitionState.SHOWING,
                    SearchView.TransitionState.HIDDEN,
                    SearchView.TransitionState.SHOWN -> {
                        //ignored
                    }
                }
            }

        //Submit search results to the adapter
        searchViewModel
            .searchResult
            .observe(viewLifecycleOwner) { searchResult ->
                searchResultAdapter.submitList(searchResult)
            }

        //Retry search button
        val retrySearchFlow = binding
            .searchLayout
            .retrySearch
            .retrySearch(binding.searchLayout.searchView.editText)

        launchSearchQueryFlow(retrySearchFlow)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun launchSearchQueryFlow(queryFlow: Flow<CharSequence?>) {
        queryFlow
            .filterNotNull()
            .filterNot { query ->
                if (query.isEmpty() || query.isBlank()) {
                    clearSearchResults()
                    true
                } else {
                    false
                }
            }
            .map { query ->
                query.trim()
            }
            .mapLatest { query: CharSequence ->
                searchGif(query)
            }
            .launchIn(lifecycleScope)
    }

    private fun searchGif(q: CharSequence) {
        searchViewModel.searchGifs(q.toString())
    }

    private fun clearSearchResults() {
        searchViewModel.clearSearchResults()
    }

    //Prepare the gifs recycler view with the same ItemDecoration and GridLayoutManager
    private fun prepareGifsList(list: RecyclerView, gifsAdapter: GifsAdapter) {
        with(list) {
            adapter = gifsAdapter
            layoutManager = newGifGridLayoutManager()
            addItemDecoration(gifsListItemDecoration)
        }
    }

    private fun newGifGridLayoutManager() = object : GridLayoutManager(requireContext(),
        GIF_GRID_ADAPTER_SPAN_COUNT
    ) {

        val gifItemSize = gifItemSize()

        override fun checkLayoutParams(params: RecyclerView.LayoutParams?): Boolean {
            return if (params == null) {
                false
            } else if (params.width == gifItemSize && params.height == gifItemSize) {
                true
            } else {
                with(params) {
                    width = gifItemSize
                    height = gifItemSize
                }
                false
            }
        }

        /**
         * Calculates an appropriate grid item view size,
         * based on screen width, span count, and margin/space between views.
         *
         * ViewTreeObserver#OnGlobalLayout can replace this method to fetch measured width of the recyclerView
         */
        private fun gifItemSize(): Int {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            val totalWidth = displayMetrics.widthPixels
            val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.gif_grid_spacing)
            val totalHorizontalSpacing = horizontalSpacing * (GIF_GRID_ADAPTER_SPAN_COUNT + 1)
            val remainingHorizontalSpacing = totalWidth - totalHorizontalSpacing
            val itemSize = remainingHorizontalSpacing / GIF_GRID_ADAPTER_SPAN_COUNT
            return itemSize
        }
    }
}

/**
 * Creates a flow that emits the value of the ime action search event
 */
private fun EditText.onImeActionSearch(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = TextView.OnEditorActionListener { v, actionId, _ ->
            if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                hideKeyboard()
                trySend(v.text)
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        }
        setOnEditorActionListener(listener)
        awaitClose { setOnEditorActionListener(null) }
    }
}

/**
 * Creates a flow that emits the search query text when clicking on the retry button
 */
private fun View.retrySearch(searchEditText: EditText): Flow<CharSequence?> {
    return callbackFlow {
        setOnClickListener {
            it.hideKeyboard()
            trySend(searchEditText.text)
        }
        awaitClose { setOnClickListener(null) }
    }
}
