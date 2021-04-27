package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GdprDTO(
    @Json(name = "url")
    val url: GdprUrlDTO,
    @Json(name = "message")
    val message: GdprMessageDTO
)
