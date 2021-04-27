package com.onemillionbot.sdk.presentation.legalterms

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.onemillionbot.sdk.NavGraphDirections
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.api.OneMillionBot.SdkKoinComponent
import com.onemillionbot.sdk.core.html
import com.onemillionbot.sdk.databinding.LegalTermsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LegalTermsFragment : Fragment(R.layout.legal_terms_fragment), SdkKoinComponent {
    private val viewModel: LegalTermsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LegalTermsFragmentBinding.bind(view).apply {
            btAgree.setOnClickListener { viewModel.onViewEvent(LegalTermsViewEvent.AcceptLegalTerms) }
            btDisagree.setOnClickListener { activity?.moveTaskToBack(true) }

            viewModel.viewState.observe(viewLifecycleOwner, { configBot ->
                btAgree.setBackgroundColor(Color.parseColor(configBot.colorHex))
                btDisagree.setBackgroundColor(Color.parseColor(configBot.colorHex))

                tvLegalTermsExplanation.html = configBot.gdprMessage
                tvLegalTerms.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(configBot.gdprUrl)))
                }
            })
        }

        viewModel.singleViewState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is SingleViewState.NavigateToChat -> {
                    findNavController().popBackStack()
                    findNavController().navigate(NavGraphDirections.actionChatFragment())
                }
            }
        })
    }
}
