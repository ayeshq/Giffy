package com.giffy.list

import androidx.recyclerview.widget.RecyclerView
import com.giffy.databinding.ViewGifBinding
import com.giffy.model.Gif

class GifViewHolder(
    private val binding: ViewGifBinding,
    private val onGifClickedListener: (gif: Gif) -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {

    var gif: Gif? = null
        set(value) {
            field = value
            binding.gif = value
        }

    init {
        binding.gifCard
            .setOnClickListener {
                gif?.let(onGifClickedListener)
            }
    }
}
