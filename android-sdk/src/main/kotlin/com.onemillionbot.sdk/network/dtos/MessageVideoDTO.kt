package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageVideoDTO(
    @Json(name = "videoUrl")
    val videoUrl: String,
    @Json(name = "imageUrl")
    val imageUrl: String?
)
