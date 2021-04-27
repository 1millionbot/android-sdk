package com.onemillionbot.sdk.repository.mappers

import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.entities.WelcomeMessage
import com.onemillionbot.sdk.network.dtos.MessageButtonDTO
import com.onemillionbot.sdk.network.dtos.MessageButtonTypeDTO
import com.onemillionbot.sdk.network.dtos.MessageResultDTO
import com.onemillionbot.sdk.network.dtos.RootMessageDTO
import com.onemillionbot.sdk.network.dtos.SenderTypeDTO
import com.onemillionbot.sdk.network.dtos.WebSocketMessageDTO
import com.onemillionbot.sdk.network.dtos.WelcomeMessageDTO
import com.onemillionbot.sdk.network.dtos.WrapperMessage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.util.UUID

class MessageMapper(private val moshi: Moshi) {
    fun asEntity(botConfigColorHex: String, textSocket: String): List<Message> {
        val adapter: JsonAdapter<WebSocketMessageDTO> = moshi.adapter(WebSocketMessageDTO::class.java)

        val wrapperDTO = adapter.fromJson(textSocket)!!

        if (wrapperDTO.senderType == SenderTypeDTO.User) {
            return emptyList()
        }

        val rootMessageDTO = RootMessageDTO(
            id = UUID.randomUUID().toString(),
            message = wrapperDTO.message,
            senderType = wrapperDTO.senderType
        )

        return asEntity(botConfigColorHex, rootMessageDTO)
    }

    fun asEntity(botConfigColorHex: String, dto: MessageResultDTO): List<Message> {
        return dto.conversation.messages.flatMap { rootDTO ->
            asEntity(botConfigColorHex, rootDTO)
        }
    }

    fun asEntity(botConfigColorHex: String, dto: WelcomeMessageDTO): WelcomeMessage {
        fun mapWrapperMessages(messages: List<WrapperMessage>?): List<Message> {
            return messages?.flatMap { message ->
                asEntity(
                    botConfigColorHex,
                    RootMessageDTO(
                        id = UUID.randomUUID().toString(),
                        message = message,
                        senderType = SenderTypeDTO.Bot
                    )
                )
            } ?: emptyList()
        }

        return WelcomeMessage(
            esMessages = mapWrapperMessages(dto.web.esMessages),
            enMessages = mapWrapperMessages(dto.web.enMessages),
            caMessages = mapWrapperMessages(dto.web.caMessages),
            vaMessages = mapWrapperMessages(dto.web.vaMessages)
        )
    }
}

fun asEntity(botConfigColorHex: String, rootDTO: RootMessageDTO): List<Message> {
    val messages = mutableListOf<Message>()

    with(rootDTO.message.payload) {
        this?.videos?.forEach { video ->
            messages.add(
                Message.Bot.Video(
                    id = rootDTO.id,
                    url = video.videoUrl,
                    urlImage = video.imageUrl,
                    botConfigColorHex = botConfigColorHex
                )
            )
        }

        this?.images?.forEach { image ->
            messages.add(
                Message.Bot.Image(
                    id = rootDTO.id,
                    url = image.imageUrl,
                    botConfigColorHex = botConfigColorHex
                )
            )
        }

        rootDTO.message.text?.let { text ->
            when (rootDTO.senderType) {
                SenderTypeDTO.Employee -> {
                    messages.add(Message.Bot.Text(rootDTO.id, text, botConfigColorHex))
                }
                SenderTypeDTO.Bot -> {
                    messages.add(Message.Bot.Text(rootDTO.id, text, botConfigColorHex))
                }
                SenderTypeDTO.User -> {
                    messages.add(Message.User.Text(rootDTO.id, text, botConfigColorHex))
                }
            }
        }

        val cards = this?.cards?.map {
            Message.Bot.Card(
                id = rootDTO.id,
                title = it.title,
                subTitle = it.subTitle,
                imageUrl = it.imageUrl,
                buttons = it.buttons?.map { asEntity(it) }.orEmpty()
            )
        }.orEmpty()
        if (cards.isNotEmpty()) messages.add(Message.Bot.CardCollection(rootDTO.id, cards, botConfigColorHex))

        if (this?.buttons != null) {
            messages.add(
                Message.Bot.ButtonsOverTextField(
                    id = rootDTO.id,
                    buttons = buttons.map { asEntity(it) },
                    botConfigColorHex = botConfigColorHex
                )
            )
        }
    }

    return messages
}

private fun asEntity(dto: MessageButtonDTO) = with(dto) {
    when (type) {
        MessageButtonTypeDTO.Url -> Message.Bot.MessageButton.Link(title = text, link = value)
        MessageButtonTypeDTO.Text -> Message.Bot.MessageButton.Text(title = text, value = value)
        MessageButtonTypeDTO.Event -> Message.Bot.MessageButton.Link(title = text, link = value)
    }
}
