package com.onemillionbot.sdk.entities

data class WelcomeMessage(
    val esMessages: List<Message>,
    val enMessages: List<Message>,
    val caMessages: List<Message>,
    val vaMessages: List<Message>
)
