package com.onemillionbot.sdk.repository

import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.entities.ProviderLanguage
import com.onemillionbot.sdk.network.api.BotApi
import com.onemillionbot.sdk.repository.mappers.BotMapper

class ConfigBotRepository(
    private val botApi: BotApi,
    private val botMapper: BotMapper,
    private val sessionRepository: SessionRepository
) {
    private var cachedBotConfig: BotConfig? = null

    suspend fun getBot(): BotConfig {
        return if (cachedBotConfig != null) {
            cachedBotConfig!!
        } else {
            botMapper.asEntity(sessionRepository.getSelectedLanguage(), botApi.getAnyBot())
                .also { cachedBotConfig = it }
        }
    }

    suspend fun getSupportedLanguages(): List<ProviderLanguage> {
        return getBot().supportedLanguages
    }
}
