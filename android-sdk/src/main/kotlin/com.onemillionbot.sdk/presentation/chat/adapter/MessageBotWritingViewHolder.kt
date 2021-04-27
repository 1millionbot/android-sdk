package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.databinding.MessageBotWritingBinding
import com.onemillionbot.sdk.entities.Message

class MessageBotWritingViewHolder(private val binding: MessageBotWritingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message.Bot.PlaceholderWriting) {
        binding.tvMessage.text = message.message
    }

    companion object {
        fun create(parent: ViewGroup): MessageBotWritingViewHolder {
            val binding = MessageBotWritingBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotWritingViewHolder(binding)
        }
    }
}
