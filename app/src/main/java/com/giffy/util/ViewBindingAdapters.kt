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
}
