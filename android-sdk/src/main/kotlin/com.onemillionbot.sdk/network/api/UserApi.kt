package com.onemillionbot.sdk.network.api

import com.onemillionbot.sdk.network.dtos.UserPostDTO
import com.onemillionbot.sdk.network.dtos.UserResultDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("users")
    suspend fun createUser(@Body dto: UserPostDTO): UserResultDTO
}
