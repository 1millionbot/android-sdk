package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageCardDTO(
    @Json(name = "imageUrl")
    val imageUrl: String?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "subtitle")
    val subTitle: String?,
    @Json(name = "buttons")
    val buttons: List<MessageButtonDTO>?
)
