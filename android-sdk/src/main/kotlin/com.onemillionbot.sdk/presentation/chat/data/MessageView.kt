package com.onemillionbot.sdk.presentation.chat.data

import com.onemillionbot.sdk.entities.Message

sealed class StateMessage {
    object Loaded : StateMessage()
    object Loading : StateMessage()
    data class Error(val throwable: Throwable) : StateMessage()
}

sealed class MessageView {
    abstract val id: String

    data class MessageUser(val message: Message.User, val state: StateMessage) : MessageView() {
        override val id: String
            get() = message.id
    }

    data class MessageBot(val message: Message.Bot) : MessageView() {
        override val id: String
            get() = message.id
    }
}
