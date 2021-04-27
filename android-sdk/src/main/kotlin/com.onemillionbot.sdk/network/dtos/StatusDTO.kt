package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusDTO(
    @Json(name = "online")
    val online: Boolean,
    @Json(name = "typing")
    val typing: Boolean,
    @Json(name = "userName")
    val userName: String?,
    @Json(name = "deleted")
    val deleted: Boolean,
    @Json(name = "attended")
    val attended: AttendedDTO?,
    @Json(name = "origin")
    val origin: String?,
)
