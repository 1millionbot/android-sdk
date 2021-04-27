package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json

enum class SenderTypeDTO {
    @Json(name = "Bot")
    Bot,
    @Json(name = "User")
    User,
    @Json(name = "Employee")
    Employee
}
