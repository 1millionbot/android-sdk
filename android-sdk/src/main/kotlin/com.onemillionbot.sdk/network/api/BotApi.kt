package com.onemillionbot.sdk.network.api

import com.onemillionbot.sdk.network.dtos.BotResultDTO
import retrofit2.http.GET

interface BotApi {
    @GET("bots")
    suspend fun getAnyBot(): BotResultDTO
}
