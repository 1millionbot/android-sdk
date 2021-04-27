package com.onemillionbot.sdk.api

interface Environment {
    val url: String
    val urlSocket: String
}

data class EnvStaging(
    override val url: String = "https://staging-api.1millionbot.com/api/public/",
    override val urlSocket: String = "https://socket-staging.1millionbot.com"
) : Environment

data class EnvProduction(
    override val url: String = "https://api.1millionbot.com/api/public/",
    override val urlSocket: String = "https://socket.1millionbot.com"
) : Environment
