package com.onemillionbot.sdk.entities

import com.onemillionbot.sdk.network.dtos.AttendedDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Status(
    @Json(name = "online")
    val online: Boolean,
    @Json(name = "typing")
    val typing: Boolean,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "deleted")
    val deleted: Boolean,
    @Json(name = "attended")
    val attended: AttendedDTO?,
    @Json(name = "origin")
    val origin: String?,
)
