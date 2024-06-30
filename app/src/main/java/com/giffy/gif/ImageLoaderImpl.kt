package com.giffy.gif

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.Target
import com.giffy.model.Gif
import com.giffy.model.ScaledGif
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageLoaderImpl @Inject constructor(
    @ApplicationContext val context: Context
) : ImageLoader {

    private val glide: Glide = Glide.get(context)

    override fun loadAnimatedGif(
        gif: Gif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)?
    ) {
        val thumbnailGif = gif.thumbnail ?: gif.downsizedGif

        //Show thumbnail/scaledDown gif if available before the actual gif for a smoother transition!
        if (thumbnailGif != null) {
            loadGifWithThumbnail(gif, thumbnailGif, imageView, onGifLoadedListener)
        } else {
            loadGifWithNoThumbnail(gif, imageView, onGifLoadedListener)
        }
    }

    override fun loadFixedGif(
        gif: Gif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)?
    ) {
        glide
            .requestManagerRetriever
            .get(context)
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(gif.fixedSmallThumb.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(imageView)
    }

    private fun loadGifWithThumbnail(
        gif: Gif,
        thumbnailGif: ScaledGif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)?
    ) {
        glide
            .requestManagerRetriever
            .get(context)
            .asGif()
            .load(gif.originalGif.url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .thumbnail(
                glide
                    .requestManagerRetriever
                    .get(context)
                    .asGif()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .load(thumbnailGif.url)
                    .sizeMultiplier(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
            )
            .addListener(object : RequestListenerAdapter<GifDrawable>() {

                override fun onResourceReady(
                    resource: GifDrawable,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onGifLoadedListener?.invoke(gif)
                    return false
                }
            })
            .into(imageView)
    }

    private fun loadGifWithNoThumbnail(
        gif: Gif,
        imageView: ImageView,
        onGifLoadedListener: ((gif: Gif) -> Unit)?
    ) {
        glide
            .requestManagerRetriever
            .get(context)
            .asGif()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(gif.originalGif.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .addListener(object : RequestListenerAdapter<GifDrawable>() {

                override fun onResourceReady(
                    resource: GifDrawable,
                    model: Any?,
                    target: Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onGifLoadedListener?.invoke(gif)
                    return false
                }
            })
            .into(imageView)
    }
}
