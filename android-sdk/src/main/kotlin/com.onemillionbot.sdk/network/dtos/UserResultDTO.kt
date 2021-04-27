package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class UserResultDTO(
    @Json(name = "user")
    val userDTO: UserDTO
)
