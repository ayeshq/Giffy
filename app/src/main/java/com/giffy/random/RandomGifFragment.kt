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
import com.giffy.util.asAgeRestrictionBadge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RandomGifFragment : Fragment() {

    private lateinit var binding: FragmentRandomGifBinding

    private val viewModel: RandomGifViewModel by viewModels<RandomGifViewModel>()

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
    }

    override fun onResume() {
        super.onResume()

        viewModel.startAutoLoadingRandomGifs()
    }

    override fun onPause() {
        super.onPause()

        viewModel.stopAutoLoadingRandomGifs()
    }
}
