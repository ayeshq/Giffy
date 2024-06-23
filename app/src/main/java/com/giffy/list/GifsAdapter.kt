package com.giffy.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.giffy.R
import com.giffy.databinding.ViewGifBinding
import com.giffy.model.Gif

private val itemCallback = object : DiffUtil.ItemCallback<Gif>() {

    override fun areItemsTheSame(oldItem: Gif, newItem: Gif) = oldItem.id === newItem.id

    override fun areContentsTheSame(oldItem: Gif, newItem: Gif) = true
}

class GifsAdapter(
    private val onGifClickedListener: (gif: Gif) -> Unit
) : ListAdapter<Gif, GifViewHolder>(
    itemCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifViewHolder {
        val binding = DataBindingUtil.inflate<ViewGifBinding>(
            LayoutInflater.from(parent.context),
            R.layout.view_gif,
            parent,
            false
        )
        return GifViewHolder(binding, onGifClickedListener)
    }

    override fun onBindViewHolder(holder: GifViewHolder, position: Int) {
        holder.gif = getItem(position)
    }
}
