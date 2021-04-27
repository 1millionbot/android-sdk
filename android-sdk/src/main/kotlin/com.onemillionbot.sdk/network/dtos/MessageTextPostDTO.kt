package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageTextPostDTO(
    @Json(name = "text")
    val text: String
)
