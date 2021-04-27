package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WrapperMessage (
    @Json(name = "text")
    val text: String?,
    @Json(name = "payload")
    val payload: PayloadMessageDTO?
)
