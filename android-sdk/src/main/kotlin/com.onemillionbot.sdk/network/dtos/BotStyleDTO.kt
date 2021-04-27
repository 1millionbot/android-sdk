package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BotStyleDTO(
    @Json(name = "primary_color")
    val colorHex: String
)
