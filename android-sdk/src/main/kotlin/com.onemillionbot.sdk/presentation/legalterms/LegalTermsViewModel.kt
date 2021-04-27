package com.onemillionbot.sdk.presentation.legalterms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onemillionbot.sdk.api.Logger
import com.onemillionbot.sdk.core.SingleLiveData
import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.repository.ConfigBotRepository
import com.onemillionbot.sdk.repository.SessionRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LegalTermsViewModel(
    private val sessionRepository: SessionRepository,
    private val configBotRepository: ConfigBotRepository,
    private val logger: Logger
) : ViewModel() {
    private val handler = CoroutineExceptionHandler { _, e: Throwable -> logger.log(e) }

    private val _viewState: MutableLiveData<BotConfig> = MutableLiveData()
    val viewState: MutableLiveData<BotConfig>
        get() = _viewState

    private val _singleViewState = SingleLiveData<SingleViewState>()
    val singleViewState: LiveData<SingleViewState>
        get() = _singleViewState

    init {
        viewModelScope.launch(handler) {
            _viewState.value = configBotRepository.getBot()
        }
    }

    fun onViewEvent(viewEvent: LegalTermsViewEvent) {
        when (viewEvent) {
            is LegalTermsViewEvent.AcceptLegalTerms -> {
                viewModelScope.launch {
                    sessionRepository.acceptLegalTerms()
                    _singleViewState.setValue(SingleViewState.NavigateToChat)
                }
            }
        }
    }
}

sealed class LegalTermsViewEvent {
    object AcceptLegalTerms : LegalTermsViewEvent()
}

sealed class SingleViewState {
    object NavigateToChat : SingleViewState()
}
