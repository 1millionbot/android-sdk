package com.onemillionbot.sdk.repository.di

import com.onemillionbot.sdk.core.GetCountryUseCase
import com.onemillionbot.sdk.core.GetTimeZoneUseCase
import com.onemillionbot.sdk.core.NetworkUtils
import com.onemillionbot.sdk.repository.ChatRepository
import com.onemillionbot.sdk.repository.ConfigBotRepository
import com.onemillionbot.sdk.repository.SessionRepository
import com.onemillionbot.sdk.repository.SocketRepository
import com.onemillionbot.sdk.repository.StatusRepository
import com.onemillionbot.sdk.repository.UserRepository
import com.onemillionbot.sdk.repository.mappers.BotMapper
import com.onemillionbot.sdk.repository.mappers.MessageMapper
import com.onemillionbot.sdk.repository.mappers.StatusMapper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory { BotMapper(messageMapper = get()) }

    single {
        ConfigBotRepository(
            botApi = get(),
            botMapper = get(),
            sessionRepository = get()
        )
    }

    factory {
        MessageMapper(
            moshi = get()
        )
    }

    single { SessionRepository(androidContext()) }

    factory { GetCountryUseCase(androidContext()) }

    factory { GetTimeZoneUseCase() }

    factory {
        ChatRepository(
            chatApi = get(),
            configBotRepository = get(),
            sessionRepository = get(),
            messageMapper = get()
        )
    }

    factory {
        UserRepository(
            userApi = get(),
            getCountryUseCase = get(),
            getTimeZoneUseCase = get(),
            sessionRepository = get(),
            networkUtils = get()
        )
    }

    factory {
        NetworkUtils(context = androidContext())
    }

    factory {
        StatusRepository(
            statusApi = get(),
            configBotRepository = get(),
            sessionRepository = get(),
            statusMapper = get()
        )
    }

    factory {
        StatusMapper(
            moshi = get()
        )
    }


    factory {
        SocketRepository(
            configBotRepository = get(),
            sessionRepository = get(),
            environment = get()
        )
    }
}
