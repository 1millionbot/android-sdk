package com.onemillionbot.sdk.presentation.chat.adapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.CardButtonListener

class ButtonCardAdapter(
    private val dataSource: List<Message.Bot.MessageButton>,
    private val cardButtonListener: CardButtonListener,
    @LayoutRes private val idLayout: Int = R.layout.button_message
    ) :
    RecyclerView.Adapter<ButtonCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonCardViewHolder {
        return ButtonCardViewHolder.create(idLayout, parent, cardButtonListener)
    }

    override fun onBindViewHolder(viewHolder: ButtonCardViewHolder, position: Int) {
        viewHolder.bind(dataSource[position])
    }

    override fun getItemCount() = dataSource.size
}
