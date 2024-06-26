package com.giffy.random

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.giffy.R
import com.giffy.databinding.FragmentRandomGifBinding
import com.giffy.model.Gif
import com.giffy.model.ScaledGif
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

        viewModel
            .randomGif
            .observe(viewLifecycleOwner) { newGif ->
                newGif?.let {
                    //Delay loading next random gif until full resolution gif is loaded into the imageview
                    viewModel.stopAutoLoadingRandomGifs()
                    loadRandomGifImage(
                        it,
                        binding.randomGif
                    )
                }
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

    private fun loadRandomGifImage(gif: Gif, imageView: ImageView) {
        val thumbnailGif = gif.thumbnail ?: gif.downsizedGif

        //Show thumbnail/scaledDown gif if available before the actual gif for a smoother transition!
        if (thumbnailGif != null) {
            loadGifWithThumbnail(gif, thumbnailGif, imageView)
        } else {
            loadGifWithNoThumbnail(gif, imageView)
        }
    }

    private fun loadGifWithThumbnail(gif: Gif, thumbnailGif: ScaledGif, imageView: ImageView) {
        Glide
            .with(imageView)
            .asDrawable()
            .load(gif.originalGif.url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(
                Glide
                    .with(imageView)
                    .asDrawable()
                    .load(thumbnailGif.url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    //Show a thumbnail for smoother loading transition
                    .sizeMultiplier(0.05f)
            )
            .addListener(gifLoadingListener)
            .into(imageView)
    }

    private fun loadGifWithNoThumbnail(gif: Gif, imageView: ImageView) {
        Glide
            .with(imageView)
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(gif.originalGif.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .addListener(gifLoadingListener)
            .into(imageView)
    }

    private val gifLoadingListener = object : RequestListener<Drawable> {

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            viewModel.startAutoLoadingRandomGifs()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            viewModel.startAutoLoadingRandomGifs()
            return false
        }
    }
}
