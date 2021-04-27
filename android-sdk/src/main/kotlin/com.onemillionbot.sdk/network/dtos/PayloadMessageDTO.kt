package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PayloadMessageDTO(
    @Json(name = "images")
    val images: List<MessageImageDTO>?,
    @Json(name = "videos")
    val videos: List<MessageVideoDTO>?,
    @Json(name = "buttons")
    val buttons: List<MessageButtonDTO>?,
    @Json(name = "cards")
    val cards: List<MessageCardDTO>?
)
