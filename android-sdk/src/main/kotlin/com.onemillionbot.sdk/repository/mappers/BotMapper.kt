package com.onemillionbot.sdk.repository.mappers

import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.entities.ProviderLanguage
import com.onemillionbot.sdk.network.dtos.BotAliasDTO
import com.onemillionbot.sdk.network.dtos.BotResultDTO
import com.onemillionbot.sdk.network.dtos.GdprMessageDTO
import com.onemillionbot.sdk.network.dtos.GdprUrlDTO

class BotMapper(
    private val messageMapper: MessageMapper
) {
    fun asEntity(selectedLanguage: ProviderLanguage, dto: BotResultDTO): BotConfig {
        with(dto.bot) {
            val alias: BotAliasDTO = aliases.first()
            return BotConfig(
                id = id,
                aliasId = alias.id,
                name = alias.name,
                urlAvatar = alias.urlImage,
                callToAction = alias.callToAction,
                colorHex = alias.styles.colorHex,
                gdprMessage = gdprMessage(selectedLanguage, gdpr.message),
                gdprUrl = gdprUrl(selectedLanguage, gdpr.url),
                welcomeMessage = messageMapper.asEntity(alias.styles.colorHex, alias.welcomeMessage),
                supportedLanguages = alias.supportedLanguages.map { supportedLanguage ->
                    ProviderLanguage.values().first { it.apiLanguageCode == supportedLanguage }
                }
            )
        }
    }

    private fun gdprMessage(selectedLanguage: ProviderLanguage, dto: GdprMessageDTO): String {
        return when (selectedLanguage) {
            ProviderLanguage.ENGLISH -> dto.en
            ProviderLanguage.SPAIN -> dto.es
            ProviderLanguage.CATALAN -> dto.ca
            ProviderLanguage.VALENCIAN -> dto.va
        } ?: listOf(dto.en, dto.es, dto.ca, dto.va)
            .first { it != null }
            .orEmpty()
    }

    private fun gdprUrl(selectedLanguage: ProviderLanguage, dto: GdprUrlDTO): String {
        return when (selectedLanguage) {
            ProviderLanguage.ENGLISH -> dto.en
            ProviderLanguage.SPAIN -> dto.es
            ProviderLanguage.CATALAN -> dto.ca
            ProviderLanguage.VALENCIAN -> dto.va
        } ?: listOf(dto.en, dto.es, dto.ca, dto.va)
            .first { it != null }
            .orEmpty()
    }
}
