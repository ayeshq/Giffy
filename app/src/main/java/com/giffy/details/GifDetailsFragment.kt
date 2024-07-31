package com.giffy.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.giffy.R
import com.giffy.databinding.FragmentGifDetailsBinding
import com.giffy.gif.ImageLoader
import com.giffy.util.asAgeRestrictionBadge
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GifDetailsFragment : Fragment() {

    private lateinit var binding: FragmentGifDetailsBinding

    private val viewModel by viewModels<GifDetailsViewModel>()

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(context as FragmentActivity) {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.trendingGifsFragment, false)
                    remove()
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(EXTRA_GIF_ID)?.let {
            viewModel.loadGifDetails(it)
        }

        viewModel.gif.observe(this) {
            imageLoader.loadAnimatedGif(it, binding.gifImageView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGifDetailsBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@GifDetailsFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(binding.toolbar, findNavController())

        binding
            .gifDetailsView
            .ageBadgeText
            .asAgeRestrictionBadge()
    }

    companion object {

        const val EXTRA_GIF_ID = "gifId"
    }
}
