package com.onemillionbot.sdk.presentation.chat.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.core.setCorners
import com.onemillionbot.sdk.databinding.MessageBotCardTextBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel

class MessageBotCardTextViewHolder(
    private val chatViewModel: ChatViewModel,
    private val binding: MessageBotCardTextBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(isPrevMessageFromOtherActor: Boolean, message: Message.Bot.CardCollection) {
        val card = message.cards.first()
        binding.rvButtons.apply {
            adapter = ButtonCardAdapter(card.buttons, chatViewModel)
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_list, null)!!)
            })
        }

        binding.bubble.setCorners(isPrevMessageFromOtherActor, message)
        binding.tvTittle.text = card.title
        binding.tvTittle.isVisible = !card.title.isNullOrBlank()

        binding.tvSubTittle.isVisible = !card.subTitle.isNullOrBlank()
        binding.tvSubTittle.html = card.subTitle

        binding.vSeparator.isGone = binding.tvTittle.isGone && binding.tvSubTittle.isGone

        binding.ivMoreCards.apply {
            isVisible = message.cards.size > 1
            if (isVisible) {
                setOnClickListener {
                    chatViewModel.onViewEvent(ChatViewEvent.ClickOnCardCollection(message))
                }
            }
        }

        ImageViewCompat.setImageTintList(
            binding.ivMoreCards,
            ColorStateList.valueOf(Color.parseColor(message.botConfigColorHex))
        )
    }

    companion object {
        fun create(chatViewModel: ChatViewModel, parent: ViewGroup): MessageBotCardTextViewHolder {
            val binding = MessageBotCardTextBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotCardTextViewHolder(chatViewModel, binding)
        }
    }
}
