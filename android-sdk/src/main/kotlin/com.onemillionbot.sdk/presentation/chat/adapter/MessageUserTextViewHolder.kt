package com.onemillionbot.sdk.presentation.chat.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.core.setCorners
import com.onemillionbot.sdk.databinding.MessageUserTextBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel
import com.onemillionbot.sdk.presentation.chat.data.StateMessage


class MessageUserTextViewHolder(private val binding: MessageUserTextBinding, private val chatViewModel: ChatViewModel) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(isPrevMessageFromOtherActor: Boolean, message: Message.User.Text, stateMessage: StateMessage) {
        when (stateMessage) {
            StateMessage.Loaded -> binding.bubble.alpha = 1f
            is StateMessage.Error -> binding.bubble.alpha = .5f
            StateMessage.Loading -> binding.bubble.alpha = .5f
        }

        val colorBot = Color.parseColor(message.botConfigColorHex)

        binding.loading.isVisible = stateMessage == StateMessage.Loading
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            binding.loading.indeterminateDrawable.colorFilter = BlendModeColorFilter(colorBot, BlendMode.SRC_ATOP)
        } else {
            binding.loading.indeterminateDrawable.setColorFilter(colorBot, PorterDuff.Mode.SRC_ATOP);
        }

        binding.btRetry.apply {
            setBackgroundColor(colorBot)
            isVisible = stateMessage is StateMessage.Error
            if (isVisible) {
                setOnClickListener {
                    chatViewModel.onViewEvent(ChatViewEvent.RetryMessageUser(message.message, message.id))
                }
            }
        }

        binding.tvMessage.text = message.message
        binding.bubble.setCorners(isPrevMessageFromOtherActor, message)
    }

    companion object {
        fun create(parent: ViewGroup, chatViewModel: ChatViewModel): MessageUserTextViewHolder {
            val binding = MessageUserTextBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageUserTextViewHolder(binding, chatViewModel)
        }
    }
}
