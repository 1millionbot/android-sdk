package com.onemillionbot.sdk.entities

import java.util.Locale

enum class ProviderLanguage(val language: String, val countryCode: String, val androidLanguageCode: String, val apiLanguageCode : String) {
    ENGLISH(language = "English", countryCode = "US", androidLanguageCode = "en", apiLanguageCode = "en"),
    SPAIN(language = "Castellano", countryCode = "ES", androidLanguageCode = "es", apiLanguageCode = "es"),
    CATALAN(language = "Català", countryCode = "ES", androidLanguageCode = "ca", apiLanguageCode = "ca"),
    VALENCIAN("Valencià", countryCode = "ES", androidLanguageCode = "ca", apiLanguageCode = "va");

    fun locale() = Locale(androidLanguageCode, countryCode)
    fun extraLanguage() = "${androidLanguageCode}-${countryCode}"
}
