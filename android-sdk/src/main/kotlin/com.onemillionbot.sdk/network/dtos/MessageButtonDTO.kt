package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageButtonDTO(
    @Json(name = "type")
    val type: MessageButtonTypeDTO,
    @Json(name = "text")
    val text: String,
    @Json(name = "value")
    val value: String
)

enum class MessageButtonTypeDTO {
    @Json(name = "url")
    Url,
    @Json(name = "text")
    Text,
    @Json(name = "event")
    Event
}
