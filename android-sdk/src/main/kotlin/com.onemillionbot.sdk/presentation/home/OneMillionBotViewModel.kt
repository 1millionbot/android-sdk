package com.onemillionbot.sdk.presentation.home

import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.api.Logger
import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.presentation.home.SingleViewState.InitNavigation
import com.onemillionbot.sdk.presentation.home.ViewEvent.CloseSession
import com.onemillionbot.sdk.repository.ConfigBotRepository
import com.onemillionbot.sdk.repository.SessionRepository
import com.onemillionbot.sdk.repository.StatusRepository
import com.onemillionbot.sdk.repository.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class OneMillionBotViewModel(
    private val sessionRepository: SessionRepository,
    private val configBotRepository: ConfigBotRepository,
    private val logger: Logger,
    private val userRepository: UserRepository,
    private val statusRepository: StatusRepository
) : ViewModel() {
    private val handler = CoroutineExceptionHandler { _, e: Throwable -> logger.log(e) }

    private val _viewState: MutableLiveData<ViewStateSuccess> = MutableLiveData()
    val viewState: MutableLiveData<ViewStateSuccess>
        get() = _viewState

    private val _singleViewState = MutableLiveData<SingleViewState>()
    val singleViewState: MutableLiveData<SingleViewState>
        get() = _singleViewState

    init {
        viewModelScope.launch(handler) {
            val botConfig = configBotRepository.getBot()
            _viewState.value = ViewStateSuccess(botConfig)

            val hasUserAcceptedLegalTerms = sessionRepository.hasUserAcceptedLegalTerms()
            val destination =
                if (hasUserAcceptedLegalTerms) R.id.chatFragment else R.id.legalTermsFragment
            _singleViewState.value = InitNavigation(destination)
        }
    }

    fun onViewEvent(viewEvent: ViewEvent) {
        when (viewEvent) {
            is CloseSession -> {
                viewModelScope.launch(handler) {
                    statusRepository.userAskedToForgetTheirData()
                    sessionRepository.closeSession()
                    _singleViewState.value = SingleViewState.CloseBot
                }
            }
            is ViewEvent.CopyToClipboard -> copyToClipboard()
        }
    }

    private fun copyToClipboard() {
        viewModelScope.launch(handler) {
            val chatId = sessionRepository.getChatId()!!
            val botId = configBotRepository.getBot().id
            val userId = sessionRepository.getUserId()!!

            _singleViewState.value = SingleViewState.CopyToClipboard(
                """
                ChatId = $chatId
                BotId = $botId
                UserId = $userId
            """.trimIndent()
            )
        }
    }
}

data class ViewStateSuccess(val botConfig: BotConfig)

sealed class SingleViewState {
    data class InitNavigation(@IdRes val destination: Int) : SingleViewState()
    object CloseBot : SingleViewState()
    data class CopyToClipboard(val debugInfo: String) : SingleViewState()
}

sealed class ViewEvent {
    object CloseSession : ViewEvent()
    object CopyToClipboard : ViewEvent()
}
