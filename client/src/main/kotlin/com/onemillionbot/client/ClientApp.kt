package com.onemillionbot.client

import android.app.Application
import com.onemillionbot.sdk.api.EnvStaging
import com.onemillionbot.sdk.api.OneMillionBot
import com.onemillionbot.sdk.api.OneMillionBotCredentials

class ClientApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val yourApiKey = "60553d58c41f5dfa095b34b9"
        OneMillionBot.init(
            application = this,
            credentials = OneMillionBotCredentials(yourApiKey),
            environment = EnvStaging()
        )
    }
}
