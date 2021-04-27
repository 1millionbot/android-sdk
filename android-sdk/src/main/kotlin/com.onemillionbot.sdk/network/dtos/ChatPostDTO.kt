package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatPostDTO(
    @Json(name = "alias")
    val aliasId: String,
    @Json(name = "bot")
    val botId: String,
    @Json(name = "user")
    val userId: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "company")
    val company: String,
    @Json(name = "integration")
    val integration: String
)
