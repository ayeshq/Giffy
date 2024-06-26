package com.giffy.random

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.giffy.R
import com.giffy.databinding.FragmentRandomGifBinding
import com.giffy.model.Gif
import com.giffy.util.asAgeRestrictionBadge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RandomGifFragment : Fragment() {

    private lateinit var binding: FragmentRandomGifBinding

    private val viewModel: RandomGifViewModel by viewModels<RandomGifViewModel>()

    private var gifClickListener: ((gif: Gif) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate<FragmentRandomGifBinding?>(
            inflater,
            R.layout.fragment_random_gif,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@RandomGifFragment.viewModel
        }

        binding.
            retryRandomGif.
            setOnClickListener{ _ ->
                viewModel.startAutoLoadingRandomGifs()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding
            .ageBadgeText
            .asAgeRestrictionBadge()

        binding
            .randomGif
            .setOnClickListener {
                gifClickListener?.invoke(viewModel.randomGif.value!!)
            }
    }

    override fun onResume() {
        super.onResume()

        viewModel.startAutoLoadingRandomGifs()
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopAutoLoadingRandomGifs()
    }

    fun setOnGifCLickedListener(listener: (gif: Gif) -> Unit) {
        gifClickListener = listener
    }

    fun startAutoLoadingRandomGifs() {
        viewModel.startAutoLoadingRandomGifs()
    }

    fun stopAutoLoadingRandomGifs() {
        viewModel.stopAutoLoadingRandomGifs()
    }
}
