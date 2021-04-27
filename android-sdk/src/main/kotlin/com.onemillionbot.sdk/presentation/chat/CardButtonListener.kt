package com.onemillionbot.sdk.presentation.chat

import com.onemillionbot.sdk.entities.Message

interface CardButtonListener {
    fun onViewEvent(messageButton: Message.Bot.MessageButton)
}
