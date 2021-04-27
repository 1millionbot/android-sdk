package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestStatusDTO(
    @Json(name = "status")
    val status: StatusDTO,
    @Json(name = "bot")
    val botId: String,
    @Json(name = "conversation")
    val chatId: String
)
