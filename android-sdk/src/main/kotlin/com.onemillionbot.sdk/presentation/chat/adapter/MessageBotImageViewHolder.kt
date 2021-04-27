package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.databinding.MessageBotImageBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel

class MessageBotImageViewHolder(
    private val fragment: Fragment,
    private val binding: MessageBotImageBinding,
    private val chatViewModel: ChatViewModel
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message.Bot.Image) {
        Glide.with(fragment)
            .load(message.url)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.ivImage)
        binding.ivImage.setOnClickListener {
            chatViewModel.onViewEvent(ChatViewEvent.ShowImageFullScreen(message.url))
        }
    }

    companion object {
        fun create(fragment: Fragment, parent: ViewGroup, chatViewModel: ChatViewModel): MessageBotImageViewHolder {
            val binding = MessageBotImageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotImageViewHolder(fragment, binding, chatViewModel)
        }
    }
}
