package com.giffy.util

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.giffy.model.Gif

object ViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.isVisible = show
    }

    @JvmStatic
    @BindingAdapter("fixedThumb")
    fun loadStaticGif(
        imageView: ImageView,
        gif: Gif?
    ) {
        if (gif == null) {
            return
        }

        Glide
            .with(imageView)
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .load(gif.fixedSmallThumb.url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("gif")
    fun loadGif(
        imageView: ImageView,
        gif: Gif?
    ) {

        if (gif == null) {
            return
        }

        val thumbnailGif = gif.thumbnail ?: gif.downsizedGif

        //Show thumbnail/scaledDown gif if available before the actual gif for a smoother transition!
        if (thumbnailGif != null) {
            Glide
                .with(imageView)
                .load(gif.originalGif.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(
                    Glide
                        .with(imageView)
                        .load(thumbnailGif.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        //Show a thumbnail for smoother loading transition
                        .sizeMultiplier(0.05f)
                )
                .into(imageView)
        } else {
            Glide
                .with(imageView)
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(gif.originalGif.url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }
}
