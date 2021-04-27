package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class MessagePostDTO(
    @Json(name = "conversation")
    val chatId: String,
    @Json(name = "bot")
    val botId: String,
    @Json(name = "sender")
    val userId: String,
    @Json(name = "language")
    val language: String,
    @Json(name = "sender_type")
    val senderType: String = "User",
    @Json(name = "message")
    val text: MessageTextPostDTO
)
