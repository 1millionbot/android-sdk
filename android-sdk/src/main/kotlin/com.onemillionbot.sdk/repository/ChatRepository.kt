package com.onemillionbot.sdk.repository

import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.entities.ProviderLanguage
import com.onemillionbot.sdk.network.api.ChatApi
import com.onemillionbot.sdk.network.dtos.ChatPostDTO
import com.onemillionbot.sdk.network.dtos.MessagePostDTO
import com.onemillionbot.sdk.network.dtos.MessageTextPostDTO
import com.onemillionbot.sdk.presentation.chat.data.MessageView
import com.onemillionbot.sdk.presentation.chat.data.StateMessage
import com.onemillionbot.sdk.repository.mappers.MessageMapper
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID


class ChatRepository(
    private val chatApi: ChatApi,
    private val configBotRepository: ConfigBotRepository,
    private val sessionRepository: SessionRepository,
    private val messageMapper: MessageMapper,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    @Volatile
    private var shouldSendWritingAfterSendMessageResponse = false

    private val incomingMessages = MutableSharedFlow<IncomingMessageState>(replay = 0)
    fun incomingMessages(): Flow<IncomingMessageState> = incomingMessages

    private var speechToText: Boolean = false

    fun sendMessage(
        newMessage: String,
        speechToText: Boolean,
        value: String?,
        messageFailedId: String? = null
    ): Flow<MessageView.MessageUser> = flow {
        this@ChatRepository.speechToText = speechToText

        val botConfigColorHex = configBotRepository.getBot().colorHex

        val message = MessageView.MessageUser(
            message = Message.User.Text(
                id = messageFailedId ?: UUID.randomUUID().toString(),
                message = newMessage,
                botConfigColorHex = botConfigColorHex
            ),
            state = StateMessage.Loading
        )

        emit(message)

        val chatId = sessionRepository.getChatId()!!
        val botId = configBotRepository.getBot().id
        val userId = sessionRepository.getUserId()!!
        val language = sessionRepository.getSelectedLanguage().apiLanguageCode

        try {
            shouldSendWritingAfterSendMessageResponse = true
            chatApi.sendMessage(
                MessagePostDTO(
                    chatId = chatId,
                    botId = botId,
                    userId = userId,
                    language = language,
                    text = MessageTextPostDTO(value ?: newMessage)
                )
            )
            if (shouldSendWritingAfterSendMessageResponse) {
                shouldSendWritingAfterSendMessageResponse = false
                emit(message.copy(state = StateMessage.Loaded))
                //incomingMessages.emit(IncomingMessageState.Writing)
            }
        } catch (e: Throwable) {
            emit(message.copy(state = StateMessage.Error(e)))
        }
    }

    suspend fun getMessages(lastIdQueried: String?): List<Message> {
        return if (lastIdQueried == "1") {
            emptyList()
        } else {
            if (sessionRepository.getChatId() == null) {
                val welcomeMessage = configBotRepository.getBot().welcomeMessage
                val fallbackWelcomeMessage = welcomeMessage.esMessages
                    .ifEmpty { welcomeMessage.caMessages }
                    .ifEmpty { welcomeMessage.vaMessages }
                    .ifEmpty { welcomeMessage.enMessages }

                when (sessionRepository.getSelectedLanguage()) {
                    ProviderLanguage.ENGLISH -> welcomeMessage.enMessages.ifEmpty { fallbackWelcomeMessage }
                    ProviderLanguage.SPAIN -> welcomeMessage.esMessages.ifEmpty { fallbackWelcomeMessage }
                    ProviderLanguage.CATALAN -> welcomeMessage.caMessages.ifEmpty { fallbackWelcomeMessage }
                    ProviderLanguage.VALENCIAN -> welcomeMessage.vaMessages.ifEmpty { fallbackWelcomeMessage }
                }
            } else {
                val chatId = sessionRepository.getChatId()!!

                messageMapper.asEntity(
                    configBotRepository.getBot().colorHex, chatApi.getMessages(chatId)
                )
            }
        }.reversed()
    }

    suspend fun setSocket(socket: Socket) {
        val chatId = sessionRepository.getChatId()!!
        val bot = configBotRepository.getBot()

        socket.on(chatId) {
            try {
                coroutineScope.launch {
                    messageMapper.asEntity(bot.colorHex, it[0].toString()).forEach { message ->
                        shouldSendWritingAfterSendMessageResponse = false

                        incomingMessages.emit(
                            IncomingMessageState.Response(
                                messageBot = message,
                                speechToText
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                coroutineScope.launch {
                    incomingMessages.emit(IncomingMessageState.Error(e))
                }
            }
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) {
            shouldSendWritingAfterSendMessageResponse = false
            coroutineScope.launch {
                incomingMessages.emit(IncomingMessageState.Error(Throwable(it[0].toString())))
            }
        }
    }

    suspend fun createChat(): String {
        val bot = configBotRepository.getBot()
        val userId = sessionRepository.getUserId()!!
        val company = sessionRepository.getCompanyId()
        val language = sessionRepository.getSelectedLanguage().androidLanguageCode

        val result = chatApi.create(
            ChatPostDTO(
                aliasId = bot.aliasId,
                botId = bot.id,
                userId = userId,
                language = language,
                company = company,
                integration = "app"
            )
        )

        sessionRepository.setChatId(result.chat.id)
        return result.chat.id
    }
}

sealed class IncomingMessageState {
    data class Response(val messageBot: Message, val speechToText: Boolean) : IncomingMessageState()
    object Writing : IncomingMessageState()
    data class Error(val throwable: Throwable) : IncomingMessageState()
}
