package com.onemillionbot.sdk.presentation.di

import android.content.Context
import androidx.appcompat.app.OneMillionBotContextWrapper
import androidx.appcompat.app.OneMillionBotContextWrapper.Companion.getOverriddenConfigIfPossible
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.presentation.chat.ChatViewModel
import com.onemillionbot.sdk.presentation.home.OneMillionBotViewModel
import com.onemillionbot.sdk.presentation.legalterms.LegalTermsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        LegalTermsViewModel(sessionRepository = get(), configBotRepository = get(), logger = get())
    }

    viewModel {
        OneMillionBotViewModel(
            sessionRepository = get(),
            configBotRepository = get(),
            logger = get(),
            userRepository = get(),
            statusRepository = get()
        )
    }

    viewModel {
        ChatViewModel(
            chatRepository = get(),
            sessionRepository = get(),
            logger = get(),
            configBotRepository = get(),
            localizedWritingMessage = androidContext().getString(
                R.string.writing
            ),
            statusRepository = get(),
            socketRepository = get(),
            userRepository = get()
        )
    }

    factory { (context: Context) ->
        OneMillionBotContextWrapper(
            context.createConfigurationContext(
                getOverriddenConfigIfPossible(context, get())
            )
        )
    }
}
