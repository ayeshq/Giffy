package com.giffy.trending

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.giffy.R
import com.giffy.databinding.FragmentTrendingGifsBinding
import com.giffy.list.GifsAdapter
import com.giffy.list.GifsGridSpacingItemDecoration
import com.giffy.model.Gif
import dagger.hilt.android.AndroidEntryPoint

private const val GIF_GRID_ADAPTER_SPAN_COUNT = 3

@AndroidEntryPoint
class TrendingGifsFragment : Fragment(R.layout.fragment_trending_gifs) {

    private lateinit var binding: FragmentTrendingGifsBinding

    private val trendingGifsViewModel by viewModels<TrendingGifsViewModel>()

    private val onGifClickedListener: (gif: Gif) -> Unit = {
        //For later
    }

    private val trendingGifsAdapter = GifsAdapter(onGifClickedListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind<FragmentTrendingGifsBinding>(view)!!.apply {
            lifecycleOwner = viewLifecycleOwner
            trendingGifsViewModel = this@TrendingGifsFragment.trendingGifsViewModel
        }

        prepareTrendingGifsList()

        trendingGifsViewModel.trendingGifs.observe(viewLifecycleOwner) { trendingGifsList ->
            trendingGifsAdapter.submitList(trendingGifsList)
        }

        trendingGifsViewModel.loadTrendingGifs()
    }

    private fun prepareTrendingGifsList() {
        with(binding.trendingGifsLayout.trendingGifsList) {

            adapter = trendingGifsAdapter

            layoutManager = object : GridLayoutManager(requireContext(),
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
            }

            addItemDecoration(
                GifsGridSpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.gif_grid_spacing),
                    GIF_GRID_ADAPTER_SPAN_COUNT
                )
            )
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
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        val totalWidth = displayMetrics.widthPixels
        val horizontalSpacing = resources.getDimensionPixelSize(R.dimen.gif_grid_spacing)
        val totalHorizontalSpacing = horizontalSpacing * (GIF_GRID_ADAPTER_SPAN_COUNT + 1)
        val remainingHorizontalSpacing = totalWidth - totalHorizontalSpacing
        val itemSize = remainingHorizontalSpacing / GIF_GRID_ADAPTER_SPAN_COUNT
        return itemSize
    }
}
