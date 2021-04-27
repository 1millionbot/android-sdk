package com.onemillionbot.sdk.presentation.chat

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.api.Logger
import com.onemillionbot.sdk.core.SingleLiveData
import com.onemillionbot.sdk.entities.Attended
import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.entities.ProviderLanguage
import com.onemillionbot.sdk.paging_3_alpha09.common.Pager
import com.onemillionbot.sdk.paging_3_alpha09.common.PagingConfig
import com.onemillionbot.sdk.paging_3_alpha09.common.PagingData
import com.onemillionbot.sdk.paging_3_alpha09.common.cachedIn
import com.onemillionbot.sdk.paging_3_alpha09.common.filter
import com.onemillionbot.sdk.paging_3_alpha09.common.map
import com.onemillionbot.sdk.presentation.chat.data.MessageView
import com.onemillionbot.sdk.presentation.chat.data.StateMessage
import com.onemillionbot.sdk.presentation.chat.paging.MessagePagingSource
import com.onemillionbot.sdk.repository.ChatRepository
import com.onemillionbot.sdk.repository.ConfigBotRepository
import com.onemillionbot.sdk.repository.IncomingMessageState
import com.onemillionbot.sdk.repository.SessionRepository
import com.onemillionbot.sdk.repository.SocketRepository
import com.onemillionbot.sdk.repository.StatusRepository
import com.onemillionbot.sdk.repository.UserRepository
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    private val socketRepository: SocketRepository,
    private val configBotRepository: ConfigBotRepository,
    private val chatRepository: ChatRepository,
    private val sessionRepository: SessionRepository,
    private val logger: Logger,
    private val localizedWritingMessage: String,
    private val statusRepository: StatusRepository,
    private val userRepository: UserRepository
) : ViewModel(), CardButtonListener {
    private var socket: Socket? = null
    private var pagingSource: MessagePagingSource? = null
    private val handler = CoroutineExceptionHandler { _, e: Throwable -> logger.log(e) }

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private val _showButtonsOverTextState = MutableLiveData<ButtonsOverTextState>()
    val showButtonsOverTextState: LiveData<ButtonsOverTextState> = _showButtonsOverTextState

    private val _singleViewState = SingleLiveData<SingleViewState>()
    val singleViewState: LiveData<SingleViewState> = _singleViewState

    private val _textToSpeechState = SingleLiveData<TextToSpeechState>()
    val textToSpeechState: LiveData<TextToSpeechState> = _textToSpeechState

    private val _pagingDataViewStates = MutableLiveData<PagingData<MessageView>>()
    val pagingDataViewStates: LiveData<PagingData<MessageView>>
        get() = _pagingDataViewStates

    private var isFirstTimeUserSelectsLanguage = false

    init {
        viewModelScope.launch(handler) {
            if (sessionRepository.hasUserSelectedLanguage()) {
                initPage()
            } else {
                isFirstTimeUserSelectsLanguage = true
                showSelectLanguage(cancelable = false)
            }

            _viewState.value = ViewState(configBotRepository.getBot(), sessionRepository.getSelectedLanguage())
        }

        viewModelScope.launch(handler) {
            statusRepository.attendedStatus()
                .collect { attended ->
                    _singleViewState.setValue(SingleViewState.ConversationWasAttended(attended))
                }
        }

        viewModelScope.launch(handler) {
            chatRepository.incomingMessages()
                .collect { incomingMessageState ->
                    when (incomingMessageState) {
                        is IncomingMessageState.Response -> {
                            val pagingData = _pagingDataViewStates.value ?: return@collect
                            val messageBot = incomingMessageState.messageBot

                            if (messageBot is Message.Bot.ButtonsOverTextField && messageBot.buttons.isNotEmpty()) {
                                _showButtonsOverTextState.value = ButtonsOverTextState.Show(messageBot.buttons)
                            } else {
                                _showButtonsOverTextState.value = ButtonsOverTextState.Hide
                                _pagingDataViewStates.value = pagingData
                                    .map {
                                        if (it is MessageView.MessageUser) {
                                            it.copy(state = StateMessage.Loaded)
                                        } else {
                                            it
                                        }
                                    }
                                    .filter { Message.Bot.PlaceholderWriting.ID != it.id }
                                    .insertHeaderItem(item = messageBot.mapAsView())
                                _singleViewState.setValue(SingleViewState.ScrollToBottom)
                            }

                            if (incomingMessageState.messageBot is Message.Bot.Text
                                && incomingMessageState.speechToText
                            ) {
                                _textToSpeechState.setValue(TextToSpeechState.Stop)
                                _textToSpeechState.setValue(
                                    TextToSpeechState.Play(
                                        incomingMessageState.messageBot.message,
                                        sessionRepository.getSelectedLanguage()
                                    )
                                )
                            }
                        }
                        IncomingMessageState.Writing -> {
                            val pagingData = _pagingDataViewStates.value ?: return@collect
                            _pagingDataViewStates.value = pagingData
                                .filter { Message.Bot.PlaceholderWriting.ID != it.id }
                                .insertHeaderItem(
                                    item = MessageView.MessageBot(
                                        Message.Bot.PlaceholderWriting(
                                            message = localizedWritingMessage,
                                            botConfigColorHex = configBotRepository.getBot().colorHex
                                        )
                                    )
                                )
                            _singleViewState.setValue(SingleViewState.ScrollToBottom)
                        }
                        is IncomingMessageState.Error -> {
                            val pagingData = _pagingDataViewStates.value ?: return@collect
                            _pagingDataViewStates.value = pagingData
                                .filter { Message.Bot.PlaceholderWriting.ID != it.id }
                            logger.log(incomingMessageState.throwable)
                        }
                    }
                }
        }
    }

    fun onViewEvent(chatViewEvent: ChatViewEvent) {
        when (chatViewEvent) {
            is ChatViewEvent.NewMessageUser -> sendNewUserMessage(
                text = chatViewEvent.message,
                chatViewEvent.speechToText
            )
            is ChatViewEvent.RetryMessageUser -> sendNewUserMessage(
                text = chatViewEvent.message,
                messageFailedId = chatViewEvent.idMessage,
                speechToText = false
            )
            is ChatViewEvent.SelectedLanguage -> setSelectedLanguage(chatViewEvent.languageLabel)
            is ChatViewEvent.ShowSelectLanguage -> showSelectLanguage(cancelable = true)
            is ChatViewEvent.ClickOnVideo -> _singleViewState.setValue(SingleViewState.OpenBrowser(chatViewEvent.message.url))
            is ChatViewEvent.ClickOnCardCollection -> {
                _singleViewState.setValue(
                    SingleViewState.OpenCardCollectionScreen(
                        chatViewEvent.message.copy(
                            cards = chatViewEvent.message.cards.drop(1)
                        )
                    )
                )
            }
            is ChatViewEvent.ShowImageFullScreen -> _singleViewState.setValue(
                SingleViewState.ShowImageFullScreen(chatViewEvent.url)
            )
            ChatViewEvent.StopTextToSpeech -> _textToSpeechState.setValue(TextToSpeechState.Stop)
            ChatViewEvent.UserIsTyping -> {
                viewModelScope.launch(handler) {
                    statusRepository.userIsTyping()
                }
            }
        }
    }

    override fun onViewEvent(messageButton: Message.Bot.MessageButton) {
        when (messageButton) {
            is Message.Bot.MessageButton.Text -> sendNewUserMessage(
                text = messageButton.title,
                value = messageButton.value,
                speechToText = false
            )
            is Message.Bot.MessageButton.Link -> {
                _singleViewState.setValue(SingleViewState.OpenBrowser(messageButton.link))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socket?.close()
        CoroutineScope(SupervisorJob() + Dispatchers.Main).launch {
            try {
                statusRepository.chatClosed()
            } catch (e: Exception) {
                //When user ask to forget data closing the session this method throws
            }
        }
    }

    private fun sendNewUserMessage(
        text: String,
        speechToText: Boolean,
        value: String? = null,
        messageFailedId: String? = null
    ) {
        if (text.isBlank()) {
            _singleViewState.setValue(SingleViewState.ShowError(R.string.empty_message_error))
            return
        }

        viewModelScope.launch(handler) {
            if (sessionRepository.getUserId() == null) {
                userRepository.createUser(configBotRepository.getBot().id)
            }

            if (sessionRepository.getChatId() == null) {
                chatRepository.createChat()
            }

            statusRepository.chatOpened()

            if (socket == null) {
                socket = socketRepository.initSocket().also { socket ->
                    chatRepository.setSocket(socket)
                    statusRepository.setSocket(socket)
                }
            }

            statusRepository.userIsNotTyping()
            chatRepository.sendMessage(text, speechToText, value, messageFailedId)
                .collect { message ->
                    val pagingData = _pagingDataViewStates.value ?: return@collect

                    when (message.state) {
                        StateMessage.Loaded -> {
                            pagingData
                                .map { if (message.id == it.id) message else it }
                                .let { _pagingDataViewStates.value = it }
                        }
                        is StateMessage.Error -> {
                            pagingData
                                .map { if (message.id == it.id) message else it }
                                .let { _pagingDataViewStates.value = it }
                            logger.log(message.state.throwable)
                        }
                        StateMessage.Loading -> {
                            if (messageFailedId != null) {
                                pagingData
                                    .map { if (message.id == it.id) message else it }
                                    .let { _pagingDataViewStates.value = it }
                            } else {
                                _pagingDataViewStates.value = pagingData.insertHeaderItem(item = message)
                                _singleViewState.setValue(SingleViewState.ScrollToBottom)
                            }
                        }
                    }
                }
        }
    }

    private fun setSelectedLanguage(labelLanguage: String) {
        viewModelScope.launch(handler) {
            val selectedLanguage = configBotRepository.getSupportedLanguages()
                .first { it.language == labelLanguage }
            val previousSelectedLanguage = sessionRepository.getSelectedLanguage()

            sessionRepository.selectLanguage(selectedLanguage)

            if (previousSelectedLanguage != selectedLanguage) {
                _singleViewState.setValue(SingleViewState.RestartApp)
            } else if (isFirstTimeUserSelectsLanguage) {
                initPage()
            }

            isFirstTimeUserSelectsLanguage = false
        }
    }

    private fun initPage() {
        viewModelScope.launch {
            Pager(
                config = PagingConfig(pageSize = 20)
            ) { MessagePagingSource(::handleFirstPageLoad, chatRepository, logger).also { pagingSource = it } }
                .flow
                .cachedIn(viewModelScope)
                .collect { _pagingDataViewStates.value = it }
        }
    }

    private fun showSelectLanguage(cancelable: Boolean) {
        viewModelScope.launch(handler) {
            _singleViewState.setValue(
                SingleViewState.ShowLanguageSelection(
                    configBotRepository.getSupportedLanguages().map { it.language },
                    cancelable
                )
            )
        }
    }

    private fun handleFirstPageLoad(messages: List<Message>) {
        messages.firstOrNull()?.let { firstMessage ->
            if (firstMessage is Message.Bot.ButtonsOverTextField && firstMessage.buttons.isNotEmpty()) {
                _showButtonsOverTextState.value = ButtonsOverTextState.Show(firstMessage.buttons)
            }
        }
    }
}

sealed class ChatViewEvent {
    data class ClickOnCardCollection(val message: Message.Bot.CardCollection) : ChatViewEvent()
    data class ClickOnVideo(val message: Message.Bot.Video) : ChatViewEvent()
    data class NewMessageUser(val message: String, val speechToText: Boolean) : ChatViewEvent()
    data class RetryMessageUser(val message: String, val idMessage: String) : ChatViewEvent()
    data class SelectedLanguage(val languageLabel: String) : ChatViewEvent()
    data class ShowImageFullScreen(val url: String) : ChatViewEvent()
    data class ShowSelectLanguage(val cancelable: Boolean) : ChatViewEvent()
    object StopTextToSpeech : ChatViewEvent()
    object UserIsTyping : ChatViewEvent()
}

data class ViewState(val botConfig: BotConfig, val providerLanguage: ProviderLanguage)

sealed class TextToSpeechState {
    data class Play(val message: String, val providerLanguage: ProviderLanguage) : TextToSpeechState()
    object Stop : TextToSpeechState()
}

sealed class ButtonsOverTextState {
    data class Show(val data: List<Message.Bot.MessageButton>) : ButtonsOverTextState()
    object Hide : ButtonsOverTextState()
}

sealed class SingleViewState {
    object RestartApp : SingleViewState()
    object ScrollToBottom : SingleViewState()
    data class ShowImageFullScreen(val url: String) : SingleViewState()
    data class ShowLanguageSelection(val languagesLabels: List<String>, val cancelable: Boolean) : SingleViewState()
    data class OpenBrowser(val url: String) : SingleViewState()
    data class OpenCardCollectionScreen(val message: Message.Bot.CardCollection) : SingleViewState()
    data class ShowError(@StringRes val stringRes: Int) : SingleViewState()
    data class ConversationWasAttended(val attended: Attended) : SingleViewState()
}
