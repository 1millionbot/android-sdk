package com.onemillionbot.sdk.repository

import com.onemillionbot.sdk.core.GetCountryUseCase
import com.onemillionbot.sdk.core.GetTimeZoneUseCase
import com.onemillionbot.sdk.core.NetworkUtils
import com.onemillionbot.sdk.network.api.UserApi
import com.onemillionbot.sdk.network.dtos.UserPostDTO


class UserRepository(
    private val userApi: UserApi,
    private val getCountryUseCase: GetCountryUseCase,
    private val getTimeZoneUseCase: GetTimeZoneUseCase,
    private val sessionRepository: SessionRepository,
    private val networkUtils: NetworkUtils
) {
    suspend fun createUser(botId: String) {
        val result = userApi.createUser(
            dto = UserPostDTO(
                botId = botId,
                country = getCountryUseCase().orEmpty(),
                ip = networkUtils.getIp(),
                language = sessionRepository.getSelectedLanguage().androidLanguageCode,
                platform = "Android",
                timezone = getTimeZoneUseCase()
            )
        )
        sessionRepository.setUserId(result.userDTO.id)
        sessionRepository.setCompanyId(result.userDTO.company)
        sessionRepository.setUserName(result.userDTO.name)
    }
}
