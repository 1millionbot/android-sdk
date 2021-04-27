package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserPostDTO(
    @Json(name = "bot")
    val botId: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "ip")
    val ip: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "platform")
    val platform: String,
    @Json(name = "timezone")
    val timezone: String
)
