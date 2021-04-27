package com.onemillionbot.sdk.presentation.chat.cardcollection

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.databinding.CardFragmentBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.presentation.chat.CardButtonListener
import com.onemillionbot.sdk.presentation.chat.adapter.ButtonCardAdapter
import com.stfalcon.imageviewer.StfalconImageViewer

class CardFragment : Fragment(R.layout.card_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        CardFragmentBinding.bind(view).apply {
            arguments?.getParcelable<Message.Bot.Card>(CARD_KEY)?.apply {
                ivImage.isVisible = imageUrl != null
                imageUrl?.let { url ->
                    ivImage.setOnClickListener {
                        StfalconImageViewer.Builder(context, listOf(url)) { view, image ->
                            Glide.with(this@CardFragment)
                                .load(image)
                                .into(view)
                        }.show()
                    }
                    Glide.with(this@CardFragment)
                        .load(url)
                        .placeholder(R.drawable.image_placeholder)
                        .into(ivImage)
                }

                tvTitle.text = title
                tvTitle.isVisible = !title.isNullOrBlank()

                tvSubTittle.isVisible = !subTitle.isNullOrBlank()
                tvSubTittle.html = subTitle

                rvButtons.apply {
                    adapter = ButtonCardAdapter(buttons, parentFragment as CardButtonListener)
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                        setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider_list, null)!!)
                    })
                }
            }
        }
    }

    companion object {
        const val CARD_KEY = "card_key"
    }
}
