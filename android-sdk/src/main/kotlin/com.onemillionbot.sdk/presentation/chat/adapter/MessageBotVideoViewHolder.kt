package com.onemillionbot.sdk.presentation.chat.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.databinding.MessageBotVideoBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel

class MessageBotVideoViewHolder(
    private val chatViewModel: ChatViewModel,
    private val fragment: Fragment,
    private val binding: MessageBotVideoBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message.Bot.Video) {
        Glide.with(fragment)
            .load(message.urlImage)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.ivImage)

        binding.ivImage.setColorFilter(
            ContextCompat.getColor(binding.ivImage.context, R.color.black_semi_transparent), PorterDuff.Mode.DARKEN
        )
        ImageViewCompat.setImageTintList(
            binding.ivIconPlay,
            ColorStateList.valueOf(Color.parseColor(message.botConfigColorHex))
        )

        binding.root.setOnClickListener {
            chatViewModel.onViewEvent(ChatViewEvent.ClickOnVideo(message))
        }
    }

    companion object {
        fun create(chatViewModel: ChatViewModel, fragment: Fragment, parent: ViewGroup): MessageBotVideoViewHolder {
            val binding = MessageBotVideoBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotVideoViewHolder(chatViewModel, fragment, binding)
        }
    }
}
