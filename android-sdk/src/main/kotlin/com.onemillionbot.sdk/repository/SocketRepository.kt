package com.onemillionbot.sdk.repository

import com.onemillionbot.sdk.api.Environment
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SocketRepository(
    private val configBotRepository: ConfigBotRepository,
    private val sessionRepository: SessionRepository,
    private val environment: Environment
) {
    suspend fun initSocket(): Socket {
        return withContext(Dispatchers.IO) {
            val chatId = sessionRepository.getChatId()!!
            val bot = configBotRepository.getBot()
            val botId = bot.id

            val url = "${environment.urlSocket}?bot=$botId&conversation=$chatId"

            val options = IO.Options()
            options.transports = arrayOf(WebSocket.NAME)

            IO.socket(url, options).connect()
        }
    }
}
