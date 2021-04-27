package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.paging_3_alpha09.runtime.PagingDataAdapter
import com.onemillionbot.sdk.presentation.chat.data.MessageView

class ChatAdapter(private val chatViewHolderFactory: ChatViewHolderFactory) :
    PagingDataAdapter<MessageView, RecyclerView.ViewHolder>(MessageComparator) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val isPrevMessageFromOtherActor = isPrevMessageFromOtherActor(position)
        when (holder) {
            is MessageUserTextViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageUser
                holder.bind(isPrevMessageFromOtherActor, (message.message as Message.User.Text), message.state)
            }
            is MessageBotTextViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind(
                    isPrevMessageFromOtherActor,
                    message.message as Message.Bot.Text
                )
            }
            is MessageBotWritingViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind(message.message as Message.Bot.PlaceholderWriting)
            }
            is MessageBotImageViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind((message.message as Message.Bot.Image))
            }
            is MessageBotVideoViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind((message.message as Message.Bot.Video))
            }
            is MessageBotCardImageViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind(isPrevMessageFromOtherActor, (message.message as Message.Bot.CardCollection))
            }
            is MessageBotCardTextViewHolder -> {
                val message = getItem(position)!! as MessageView.MessageBot
                holder.bind(isPrevMessageFromOtherActor, (message.message as Message.Bot.CardCollection))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return chatViewHolderFactory(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val message = getItem(position)!!) {
            is MessageView.MessageUser -> TYPE_USER_TEXT
            is MessageView.MessageBot -> {
                when (message.message) {
                    is Message.Bot.Text -> TYPE_BOT_TEXT
                    is Message.Bot.Image -> TYPE_BOT_IMAGE
                    is Message.Bot.Video -> TYPE_BOT_VIDEO
                    is Message.Bot.CardCollection -> {
                        if (!message.message.cards.first().imageUrl.isNullOrBlank()) TYPE_BOT_CARD_IMAGE
                        else TYPE_BOT_CARD_TEXT
                    }
                    is Message.Bot.ButtonsOverTextField -> error(" Message.Bot.ButtonsOverTextField not supported")
                    is Message.Bot.PlaceholderWriting -> TYPE_BOT_WRITING
                }
            }
        }
    }

    object MessageComparator : DiffUtil.ItemCallback<MessageView>() {
        override fun areItemsTheSame(oldItem: MessageView, newItem: MessageView) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MessageView, newItem: MessageView) = oldItem == newItem
    }

    private fun isPrevMessageFromOtherActor(position: Int): Boolean {
        if (position + 1 == itemCount) return true
        return when (peek(position)!!) {
            is MessageView.MessageUser -> {
                val previousMessage = peek(position + 1)
                previousMessage is MessageView.MessageBot
            }
            is MessageView.MessageBot -> {
                val previousMessage = peek(position + 1)
                previousMessage is MessageView.MessageUser
            }
        }
    }

    companion object {
        const val TYPE_USER_TEXT = 0
        const val TYPE_BOT_TEXT = 1
        const val TYPE_BOT_WRITING = 2
        const val TYPE_BOT_IMAGE = 3
        const val TYPE_BOT_VIDEO = 4
        const val TYPE_BOT_CARD_IMAGE = 5
        const val TYPE_BOT_CARD_TEXT = 6
    }
}
