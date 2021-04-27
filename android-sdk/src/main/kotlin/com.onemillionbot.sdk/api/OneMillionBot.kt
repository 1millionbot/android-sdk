package com.onemillionbot.sdk.api

import android.app.Application
import com.onemillionbot.sdk.logger.LoggerDebug
import com.onemillionbot.sdk.logger.di.loggerModule
import com.onemillionbot.sdk.network.di.networkModule
import com.onemillionbot.sdk.presentation.di.presentationModule
import com.onemillionbot.sdk.repository.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinComponent
import org.koin.dsl.module

object OneMillionBot {
    internal val koinApp = KoinApplication.init()

    fun init(
        application: Application,
        credentials: OneMillionBotCredentials,
        environment: Environment = EnvProduction(),
        logger: Logger = LoggerDebug()
    ) {
        val appModule = module {
            factory { credentials }
        }

        koinApp.apply {
            androidContext(application)
            modules(appModule,
                presentationModule,
                repositoryModule,
                networkModule,
                loggerModule,
                module {
                    factory { logger }
                    factory { environment }
                }
            )
        }
    }

    internal interface SdkKoinComponent : KoinComponent {
        override fun getKoin(): Koin = koinApp.koin
    }
}

