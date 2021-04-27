package com.onemillionbot.sdk.presentation.chat.paging

import com.onemillionbot.sdk.api.Logger
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.paging_3_alpha09.common.PagingSource
import com.onemillionbot.sdk.presentation.chat.data.MessageView
import com.onemillionbot.sdk.presentation.chat.mapAsView
import com.onemillionbot.sdk.repository.ChatRepository

class MessagePagingSource(
    private val onFirstPageLoad: (List<Message>) -> Unit,
    private val chatRepository: ChatRepository,
    private val logger: Logger
) : PagingSource<String, MessageView>() {
    private var key = 0

    override suspend fun load(params: LoadParams<String>): LoadResult<String, MessageView> = try {
        val messages = chatRepository.getMessages(params.key)

        if (key == 0) {
            onFirstPageLoad(messages)
        }

        key += 1
        LoadResult.Page(
            data = messages
                .filterNot { it is Message.Bot.ButtonsOverTextField }
                .map { message -> message.mapAsView() },
            prevKey = null, // Only paging forward
            nextKey = if (key == 1) key.toString() else null//messages.last().id
        )
    } catch (e: Exception) {
        logger.log(e)
        LoadResult.Error(e)
    }
}
