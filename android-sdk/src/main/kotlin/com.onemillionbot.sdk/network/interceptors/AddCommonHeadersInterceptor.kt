package com.onemillionbot.sdk.network.interceptors

import com.onemillionbot.sdk.api.OneMillionBotCredentials
import okhttp3.Interceptor
import okhttp3.Response


class AddCommonHeadersInterceptor(private val credentials: OneMillionBotCredentials) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", credentials.apiKey)
                .addHeader("Content-Type", "application/json")
                .build()
        )
    }
}
