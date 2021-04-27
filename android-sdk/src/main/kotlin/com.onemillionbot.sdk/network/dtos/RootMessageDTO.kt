package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RootMessageDTO(
    @Json(name = "_id")
    val id: String,
    @Json(name = "message")
    val message: WrapperMessage,
    @Json(name = "sender_type")
    val senderType: SenderTypeDTO,
)


