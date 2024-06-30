package com.giffy.gif

import android.widget.ImageView
import com.giffy.model.Gif

interface ImageLoader {

    fun loadAnimatedGif(
        gif: Gif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)? = null
    )

    fun loadFixedGif(
        gif: Gif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)? = null
    )
}
