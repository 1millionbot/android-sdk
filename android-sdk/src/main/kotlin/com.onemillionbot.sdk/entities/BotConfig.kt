package com.onemillionbot.sdk.entities

data class BotConfig(
    val id: String,
    val aliasId: String,
    val name: String,
    val urlAvatar: String,
    val callToAction: String,
    val colorHex: String,
    val gdprMessage: String,
    val gdprUrl: String,
    val supportedLanguages: List<ProviderLanguage>,
    val welcomeMessage: WelcomeMessage
)
