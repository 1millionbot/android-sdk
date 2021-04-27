package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageResultDTO(
    @Json(name = "conversation")
    val conversation: MessagesWrapper
)
