package com.onemillionbot.sdk.network.api

import com.onemillionbot.sdk.network.dtos.ChatPostDTO
import com.onemillionbot.sdk.network.dtos.ChatResultDTO
import com.onemillionbot.sdk.network.dtos.MessagePostDTO
import com.onemillionbot.sdk.network.dtos.MessageResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    @POST("messages")
    suspend fun sendMessage(@Body dto: MessagePostDTO)

    @POST("conversations")
    suspend fun create(@Body dto: ChatPostDTO): ChatResultDTO

    @GET("conversations/{id}")
    suspend fun getMessages(@Path("id") id: String): MessageResultDTO
}
