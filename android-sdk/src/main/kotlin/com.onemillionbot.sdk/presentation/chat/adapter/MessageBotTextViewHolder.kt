package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.core.setCorners
import com.onemillionbot.sdk.databinding.MessageBotTextBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel


class MessageBotTextViewHolder(
    private val binding: MessageBotTextBinding,
    private val chatViewModel: ChatViewModel
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(isPrevMessageFromOtherActor: Boolean, message: Message.Bot.Text) {
        binding.tvMessage.html = message.message
        binding.bubble.setCorners(isPrevMessageFromOtherActor, message)
        binding.bubble.setOnClickListener {
            chatViewModel.onViewEvent(ChatViewEvent.StopTextToSpeech)
        }
    }

    companion object {
        fun create(parent: ViewGroup, chatViewModel: ChatViewModel): MessageBotTextViewHolder {
            val binding = MessageBotTextBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotTextViewHolder(binding, chatViewModel)
        }
    }
}
