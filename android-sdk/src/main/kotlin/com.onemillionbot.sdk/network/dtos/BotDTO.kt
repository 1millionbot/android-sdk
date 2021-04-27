package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BotDTO(
    @Json(name = "_id")
    val id: String,
    @Json(name = "aliases")
    val aliases: List<BotAliasDTO>,
    @Json(name = "gdpr")
    val gdpr: GdprDTO
)
