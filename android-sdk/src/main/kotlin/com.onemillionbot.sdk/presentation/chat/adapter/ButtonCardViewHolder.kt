package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.CardButtonListener

class ButtonCardViewHolder(
    private val view: View,
    private val cardButtonListener: CardButtonListener,
) : RecyclerView.ViewHolder(view) {

    fun bind(messageButton: Message.Bot.MessageButton) {
        val tvText: TextView = view.findViewById(R.id.tvText)
        when (messageButton) {
            is Message.Bot.MessageButton.Text -> {
                tvText.html = messageButton.title
            }
            is Message.Bot.MessageButton.Link -> {
                tvText.html = messageButton.title
                tvText.setTextColor(
                    ContextCompat.getColor(
                        tvText.context,
                        R.color.link
                    )
                )
            }
        }

        tvText.setOnClickListener {
            cardButtonListener.onViewEvent(messageButton)
        }
    }

    companion object {
        fun create(
            @LayoutRes idLayout: Int,
            parent: ViewGroup,
            cardButtonListener: CardButtonListener
        ): ButtonCardViewHolder {
            return ButtonCardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(idLayout, parent, false), cardButtonListener
            )
        }
    }
}
