package com.giffy.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.giffy.model.GifRating
import com.google.android.material.shape.MaterialShapeDrawable

object ViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.isVisible = show
    }

    @JvmStatic
    @BindingAdapter("gifRating")
    fun bindRating(textView: TextView, rating: GifRating?) {
        rating?.apply {
            textView.setText(title)
            if (textView.background is MaterialShapeDrawable) {
                (textView.background as MaterialShapeDrawable).fillColor = ContextCompat.getColorStateList(textView.context, color)
            }
        }
    }
}
