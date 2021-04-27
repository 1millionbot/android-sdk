package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WebSocketMessageDTO(
    @Json(name = "message")
    val message: WrapperMessage,
    @Json(name = "type")
    val senderType: SenderTypeDTO,
)
