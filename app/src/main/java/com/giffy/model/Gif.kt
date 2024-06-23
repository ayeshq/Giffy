package com.giffy.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.giffy.R
import com.google.gson.annotations.SerializedName

data class Gif(
    val id: String,
    val type: String = "gif",
    val title: String,
    val rating: GifRating = GifRating.RatingG,

    @SerializedName("images")
    val scaledImages: Map<String, ScaledGif>
) {

    val originalGif: ScaledGif
        get() {
            //This could be debatable, but I doubt the original image could ever be null!!
            return scaledImages["original"]!!
        }

    val downsizedGif: ScaledGif?
        get() {
            return scaledImages["downsized"]
        }

    val thumbnail: ScaledGif?
        get() {
            return scaledImages["preview_gif"]
        }

    val fixedSmallThumb: ScaledGif
        get() {
            //Also assuming this scaled image is always available
            return scaledImages["fixed_width_small_still"]!!
        }
}

data class ScaledGif(
    val height: Int,
    val width: Int,
    val size: Int,
    val url: String?
)

enum class GifRating(
    @ColorRes val color: Int,
    @StringRes val title: Int
) {

    @SerializedName("g")
    RatingG(
        R.color.rating_g,
        R.string.rating_g
    ),

    @SerializedName("pg")
    RatingPG(
        R.color.rating_pg,
        R.string.rating_pg
    ),

    @SerializedName("pg-13")
    RatingPG13(
        R.color.rating_pg_13,
        R.string.rating_pg_13
    ),

    @SerializedName("r")
    RatingR(
        R.color.rating_r,
        R.string.rating_r
    )
}
