package com.onemillionbot.sdk.presentation.chat.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.core.setCorners
import com.onemillionbot.sdk.databinding.MessageBotCardImageBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent
import com.onemillionbot.sdk.presentation.chat.ChatViewModel

class MessageBotCardImageViewHolder(
    private val chatViewModel: ChatViewModel,
    private val fragment: Fragment,
    private val binding: MessageBotCardImageBinding
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

        binding.bubble.setCorners(isPrevMessageFromOtherActor, message, onlyApplyMarginTop = true)
        binding.tvTittle.text = card.title
        binding.tvTittle.isVisible = !card.title.isNullOrBlank()

        binding.tvSubTittle.isVisible = !card.subTitle.isNullOrBlank()
        binding.tvSubTittle.html = card.subTitle

        binding.vSeparator.isGone = binding.tvTittle.isGone && binding.tvSubTittle.isGone

        binding.ivImage.isVisible = card.imageUrl != null
        card.imageUrl?.let { imageUrl ->
            binding.ivImage.setOnClickListener {
                chatViewModel.onViewEvent(ChatViewEvent.ShowImageFullScreen(imageUrl))
            }

            Glide.with(fragment)
                .load(imageUrl)
                .into(binding.ivImage)
        }

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
        fun create(chatViewModel: ChatViewModel, fragment: Fragment, parent: ViewGroup): MessageBotCardImageViewHolder {
            val binding = MessageBotCardImageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MessageBotCardImageViewHolder(chatViewModel, fragment, binding)
        }
    }
}
