package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BotResultDTO(
    @Json(name = "bot")
    val bot: BotDTO
)
