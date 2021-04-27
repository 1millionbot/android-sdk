package com.onemillionbot.sdk.repository

import com.onemillionbot.sdk.entities.Attended
import com.onemillionbot.sdk.network.api.StatusApi
import com.onemillionbot.sdk.network.dtos.RequestStatusDTO
import com.onemillionbot.sdk.network.dtos.StatusDTO
import com.onemillionbot.sdk.repository.mappers.StatusMapper
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class StatusRepository(
    private val statusApi: StatusApi,
    private val configBotRepository: ConfigBotRepository,
    private val sessionRepository: SessionRepository,
    private val statusMapper: StatusMapper,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val attendedStatus = MutableSharedFlow<Attended>(replay = 0)
    fun attendedStatus(): Flow<Attended> = attendedStatus

    private var cachedStatus: StatusDTO? = null

    suspend fun chatOpened() {
        postStatus(typing = false, online = true)

        getStatus()?.attended?.let {
            attendedStatus.emit(statusMapper.asEntity(it))
        }
    }

    suspend fun userIsTyping() {
        // We only notify about this event if the chat is being attended by a person instead of the Bot.
        if (getStatus()?.attended != null) {
            postStatus(typing = true, online = true)
        }
    }

    suspend fun userIsNotTyping() {
        postStatus(typing = false, online = true)
    }

    suspend fun chatClosed() {
        postStatus(typing = false, online = false)
    }

    suspend fun userAskedToForgetTheirData() {
        val chatId = sessionRepository.getChatId() ?: return
        val botId = configBotRepository.getBot().id

        statusApi.status(
            RequestStatusDTO(
                status = StatusDTO(
                    typing = false,
                    online = false,
                    deleted = true,
                    attended = null,
                    userName = null,
                    origin = null
                ),
                chatId = chatId,
                botId = botId
            )
        )
    }

    suspend fun setSocket(socket: Socket) {
        val chatId = sessionRepository.getChatId() ?: return

        socket.on("${chatId}_@_status") {
            cachedStatus = statusMapper.asDto(json = it[0].toString())
            cachedStatus?.attended?.let {
                coroutineScope.launch {
                    attendedStatus.emit(statusMapper.asEntity(it))
                }
            }
        }
    }

    private suspend fun postStatus(
        typing: Boolean = false,
        online: Boolean = true
    ) {
        val chatId = sessionRepository.getChatId() ?: return
        val status = getStatus() ?: return
        val botId = configBotRepository.getBot().id

        statusApi.status(
            RequestStatusDTO(
                status = status.copy(
                        typing = typing,
                        online = online
                    ),
                chatId = chatId,
                botId = botId
            )
        )
    }

    private suspend fun getStatus(): StatusDTO? {
        val chatId = sessionRepository.getChatId() ?: return null
        val botId = configBotRepository.getBot().id
        val userName = sessionRepository.getUserName()

        return if (cachedStatus != null) {
            cachedStatus!!
        } else {
            statusMapper.asDto(json = statusApi.status(botId, chatId).string(), userName)
                .also { cachedStatus = it }
        }
    }
}
