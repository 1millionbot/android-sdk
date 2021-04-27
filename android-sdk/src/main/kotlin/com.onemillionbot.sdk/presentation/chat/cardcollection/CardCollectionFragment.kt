package com.onemillionbot.sdk.presentation.chat.cardcollection

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayoutMediator
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.databinding.CardCollectionFragmentBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.CardButtonListener

class CardCollectionFragment : AppCompatDialogFragment(), CardButtonListener {
    private val navArgs by navArgs<CardCollectionFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        return inflater.inflate(R.layout.card_collection_fragment, container)
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CardCollectionFragmentBinding.bind(view).apply {
            pager.adapter = CardAdapter(navArgs.cardCollection.cards, this@CardCollectionFragment)
            pager.setPageTransformer(
                MarginPageTransformer(
                    requireContext().resources.getDimensionPixelOffset(R.dimen.margin_large)
                )
            )

            tabLayout.isVisible = navArgs.cardCollection.cards.size > 1
            if (tabLayout.isVisible) {
                TabLayoutMediator(tabLayout, pager) { _, _ -> }.attach()
            }
        }
    }

    override fun onViewEvent(messageButton: Message.Bot.MessageButton) {
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(CARD_BUTTON_VIEW_EVENT_KEY, messageButton)
            popBackStack()
        }
    }

    companion object {
        const val CARD_BUTTON_VIEW_EVENT_KEY = "card_button_view_event_key"
    }
}
