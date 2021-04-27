package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GdprMessageDTO(
    @Json(name = "es")
    val es: String?,
    @Json(name = "en")
    val en: String?,
    @Json(name = "va")
    val va: String?,
    @Json(name = "ca")
    val ca: String?
)
