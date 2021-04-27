package com.onemillionbot.sdk.presentation.chat

import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.data.MessageView
import com.onemillionbot.sdk.presentation.chat.data.StateMessage

fun Message.mapAsView() = when (this) {
    is Message.User -> MessageView.MessageUser(this, StateMessage.Loaded)
    is Message.Bot -> MessageView.MessageBot(this)
}
