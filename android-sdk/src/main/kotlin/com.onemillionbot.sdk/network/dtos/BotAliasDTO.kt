package com.onemillionbot.sdk.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BotAliasDTO(
    @Json(name = "_id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "image")
    val urlImage: String,
    @Json(name = "call_to_action")
    val callToAction: String,
    @Json(name = "styles")
    val styles: BotStyleDTO,
    @Json(name = "welcome_message")
    val welcomeMessage: WelcomeMessageDTO,
    @Json(name = "languages")
    val supportedLanguages: List<String>
)

@JsonClass(generateAdapter = true)
data class WelcomeMessageDTO(
    @Json(name = "web")
    val web: WebDTO
)

@JsonClass(generateAdapter = true)
data class WebDTO(
    @Json(name = "es")
    val esMessages : List<WrapperMessage>?,
    @Json(name = "en")
    val enMessages : List<WrapperMessage>?,
    @Json(name = "ca")
    val caMessages : List<WrapperMessage>?,
    @Json(name = "va")
    val vaMessages : List<WrapperMessage>?
)
