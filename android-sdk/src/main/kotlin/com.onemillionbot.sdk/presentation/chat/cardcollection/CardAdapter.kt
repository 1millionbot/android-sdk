package com.onemillionbot.sdk.presentation.chat.cardcollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.onemillionbot.sdk.entities.Message

class CardAdapter(private val cards: List<Message.Bot.Card>, fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = cards.size

    override fun createFragment(position: Int): Fragment {
        return CardFragment().apply {
            arguments = Bundle().apply {
                putParcelable(CardFragment.CARD_KEY, cards[position])
            }
        }
    }
}
