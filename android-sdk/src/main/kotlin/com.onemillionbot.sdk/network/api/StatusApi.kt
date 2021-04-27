package com.onemillionbot.sdk.network.api

import com.onemillionbot.sdk.network.dtos.RequestStatusDTO
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StatusApi {
    @GET("live/status/{botId}/{conversationId}")
    suspend fun status(@Path("botId") botId: String, @Path("conversationId") conversationId: String) : ResponseBody

    @POST("live/status")
    suspend fun status(@Body status: RequestStatusDTO)
}
