package com.giffy.model

import com.google.gson.annotations.SerializedName

data class GiffyResponse<T>(
    @SerializedName("data")
    val data: T,

    @SerializedName("meta")
    val meta: MetaData
)

data class MetaData(
    @SerializedName("status")
    val status: Int,

    @SerializedName("msg")
    val message: String,
)
