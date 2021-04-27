package com.onemillionbot.sdk.network.di

import com.onemillionbot.sdk.api.Environment
import com.onemillionbot.sdk.network.api.BotApi
import com.onemillionbot.sdk.network.api.ChatApi
import com.onemillionbot.sdk.network.api.StatusApi
import com.onemillionbot.sdk.network.api.UserApi
import com.onemillionbot.sdk.network.interceptors.AddCommonHeadersInterceptor
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    factory<AddCommonHeadersInterceptor> {
        AddCommonHeadersInterceptor(
            credentials = get()
        )
    }
    single<Moshi> { Moshi.Builder().build() }
    single<OkHttpClient> {
        OkHttpClient().newBuilder()
            .addInterceptor(get<AddCommonHeadersInterceptor>())
            .build()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(get<Environment>().url)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .build()
    }

    factory<UserApi> { get<Retrofit>().create(UserApi::class.java) }
    factory<BotApi> { get<Retrofit>().create(BotApi::class.java) }
    factory<ChatApi> { get<Retrofit>().create(ChatApi::class.java) }
    factory<StatusApi> { get<Retrofit>().create(StatusApi::class.java) }
}
