package com.giffy.list

import androidx.recyclerview.widget.RecyclerView
import com.giffy.databinding.ViewGifBinding
import com.giffy.gif.ImageLoader
import com.giffy.model.Gif
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class GifViewHolder(
    private val binding: ViewGifBinding,
    private val onGifClickedListener: (gif: Gif) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {

    private val imageLoader = EntryPoints.get(
        binding.root.context.applicationContext,
        GifViewHolderEntryPoint::class.java
    ).imageLoader()

    var gif: Gif? = null
        set(value) {
            field = value
            gif?.let{
                imageLoader.loadFixedGif(gif!!, binding.imageView)
            }
        }

    init {
        binding.gifCard
            .setOnClickListener {
                gif?.let(onGifClickedListener)
            }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GifViewHolderEntryPoint {

        fun imageLoader(): ImageLoader
    }
}
