package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.core.ViewHolderFactory
import com.onemillionbot.sdk.presentation.chat.ChatViewModel

class ChatViewHolderFactory(
    private val fragment: Fragment,
    private val chatViewModel: ChatViewModel
) : ViewHolderFactory<RecyclerView.ViewHolder> {

    override fun invoke(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ChatAdapter.TYPE_USER_TEXT -> MessageUserTextViewHolder.create(parent, chatViewModel)
            ChatAdapter.TYPE_BOT_TEXT -> MessageBotTextViewHolder.create(parent, chatViewModel)
            ChatAdapter.TYPE_BOT_WRITING -> MessageBotWritingViewHolder.create(parent)
            ChatAdapter.TYPE_BOT_IMAGE -> MessageBotImageViewHolder.create(fragment, parent, chatViewModel)
            ChatAdapter.TYPE_BOT_VIDEO -> MessageBotVideoViewHolder.create(chatViewModel, fragment, parent)
            ChatAdapter.TYPE_BOT_CARD_IMAGE -> MessageBotCardImageViewHolder.create(chatViewModel, fragment, parent)
            ChatAdapter.TYPE_BOT_CARD_TEXT -> MessageBotCardTextViewHolder.create(chatViewModel, parent)
            else -> throw IllegalAccessException("Invalid view type: $viewType")
        }
    }
}
