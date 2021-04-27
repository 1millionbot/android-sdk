package com.onemillionbot.sdk.presentation.chat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.SpannableString
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.NavGraphDirections
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.api.OneMillionBot.SdkKoinComponent
import com.onemillionbot.sdk.core.RecyclerViewMargin
import com.onemillionbot.sdk.core.debounce
import com.onemillionbot.sdk.core.textChanges
import com.onemillionbot.sdk.core.visibility
import com.onemillionbot.sdk.databinding.ChatFragmentBinding
import com.onemillionbot.sdk.entities.Message
import com.onemillionbot.sdk.entities.ProviderLanguage
import com.onemillionbot.sdk.paging_3_alpha09.common.LoadState
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent.SelectedLanguage
import com.onemillionbot.sdk.presentation.chat.ChatViewEvent.ShowSelectLanguage
import com.onemillionbot.sdk.presentation.chat.adapter.ButtonCardAdapter
import com.onemillionbot.sdk.presentation.chat.adapter.ChatAdapter
import com.onemillionbot.sdk.presentation.chat.adapter.ChatViewHolderFactory
import com.onemillionbot.sdk.presentation.chat.cardcollection.CardCollectionFragment.Companion.CARD_BUTTON_VIEW_EVENT_KEY
import com.onemillionbot.sdk.presentation.home.OneMillionBotActivity
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.UUID


class ChatFragment : Fragment(R.layout.chat_fragment), SdkKoinComponent {
    private lateinit var binding: ChatFragmentBinding
    private val chatViewModel: ChatViewModel by viewModel()
    private val messageAdapter by lazy { ChatAdapter(ChatViewHolderFactory(this, chatViewModel)) }
    private var providerLanguage = ProviderLanguage.SPAIN
    private val textToSpeech by lazy {
        TextToSpeech(context) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? OneMillionBotActivity)?.showMenuOptions()

        binding = ChatFragmentBinding.bind(view).apply {
            btSend.setOnClickListener {
                if (etNewMessage.text.toString().isNotEmpty()) {
                    chatViewModel.onViewEvent(
                        ChatViewEvent.NewMessageUser(
                            etNewMessage.text.toString().trim(),
                            speechToText = false
                        )
                    )
                    etNewMessage.text.clear()
                } else {
                    chatViewModel.onViewEvent(ChatViewEvent.StopTextToSpeech)
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
                    } else {
                        SpeechRecognizerDelegate().startSpeechToText(
                            binding = binding,
                            context = requireActivity(),
                            chatViewModel = chatViewModel,
                            providerLanguage = providerLanguage
                        )
                    }
                }
            }

            etNewMessage.doAfterTextChanged { text ->
                if (text?.isNotBlank() == true) {
                    btSend.setImageResource(R.drawable.ic_send)
                } else {
                    btSend.setImageResource(R.drawable.ic_mic)
                }
            }

            etNewMessage.textChanges(
                debounce(coroutineScope = viewLifecycleOwner.lifecycleScope)
                {
                    if (it.isNotBlank()) {
                        chatViewModel.onViewEvent(ChatViewEvent.UserIsTyping)
                    }
                }
            )

            rvMessages.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    reverseLayout = true
                    //stackFromEnd = true
                }
                adapter = messageAdapter
                addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.margin).toInt()))
                messageAdapter.addLoadStateListener { loadState ->
                    pBLoading.isVisible = loadState.refresh is LoadState.Loading

                    if (loadState.refresh is LoadState.NotLoading) {
                        scrollToPosition(0)
                    }
                    if (loadState.refresh is LoadState.Error) {
                        Toast.makeText(context, getString(R.string.generic_error), Toast.LENGTH_LONG).show()
                    }
                }
            }

            fab.setOnClickListener {
                rvMessages.scrollToPosition(0)
                Handler(Looper.getMainLooper())
                    .postDelayed({ fab.visibility(isVisible = false) }, 100)
            }

            chatViewModel.showButtonsOverTextState.observe(viewLifecycleOwner, { state ->
                if (state is ButtonsOverTextState.Hide) {
                    groupHorizontalButtons.isVisible = false
                    return@observe
                }
                val buttons = (state as ButtonsOverTextState.Show).data

                val layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
                    override fun onLayoutCompleted(state: RecyclerView.State?) {
                        super.onLayoutCompleted(state)
                        rvButtonsNext.isVisible = findLastCompletelyVisibleItemPosition() < buttons.size - 1
                    }
                }

                rvButtonsNext.setOnClickListener {
                    var nextPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    if (nextPosition == -1) {
                        nextPosition = layoutManager.findLastVisibleItemPosition()
                    }
                    if (nextPosition < buttons.size - 1) {
                        rvButtons.smoothSnapToPosition(nextPosition + 1)
                    }
                }

                rvButtons.layoutManager = layoutManager
                rvButtons.addItemDecoration(RecyclerViewMargin(resources.getDimension(R.dimen.margin).toInt()))

                groupHorizontalButtons.isVisible = true
                rvButtons.adapter = ButtonCardAdapter(buttons, object : CardButtonListener {
                    override fun onViewEvent(messageButton: Message.Bot.MessageButton) {
                        val cachedValue = rvButtonsNext.isVisible
                        groupHorizontalButtons.isVisible = messageButton !is Message.Bot.MessageButton.Text
                        rvButtonsNext.isVisible = groupHorizontalButtons.isVisible && cachedValue
                        chatViewModel.onViewEvent(messageButton)
                    }
                }, R.layout.button_message_horizontal)
            })

            chatViewModel.viewState.observe(viewLifecycleOwner, { viewState ->
                pBLoading.indeterminateDrawable.setTint(Color.parseColor(viewState.botConfig.colorHex))
                fab.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                fab.imageTintList = ColorStateList.valueOf(Color.parseColor(viewState.botConfig.colorHex))

                var firstTime = true
                rvMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (dy < 0 && firstTime) {
                            firstTime = false
                            fab.show()
                        }
                    }
                })

                rvButtonsNext.imageTintList = ColorStateList.valueOf(Color.parseColor(viewState.botConfig.colorHex))

                DrawableCompat.setTint(
                    DrawableCompat.wrap(btSendSpeech.background).mutate(),
                    Color.parseColor(viewState.botConfig.colorHex)
                )

                DrawableCompat.setTint(
                    DrawableCompat.wrap(btSend.background).mutate(),
                    Color.parseColor(viewState.botConfig.colorHex)
                )

                providerLanguage = viewState.providerLanguage
            })

            chatViewModel.singleViewState.observe(viewLifecycleOwner, ::observeSingleViewState)
            chatViewModel.textToSpeechState.observe(viewLifecycleOwner, ::observeTextToSpeechState)

            tvOneMillionBot.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_one_million_bot))))
            }
        }

        chatViewModel.pagingDataViewStates.observe(viewLifecycleOwner, { pagingData ->
            viewLifecycleOwner.lifecycleScope.launch { messageAdapter.submitData(pagingData) }
        })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Message.Bot.MessageButton>(
            CARD_BUTTON_VIEW_EVENT_KEY
        )?.observe(viewLifecycleOwner, { cardButton ->
            chatViewModel.onViewEvent(cardButton)
        })
    }

    private fun observeTextToSpeechState(state: TextToSpeechState) {
        when (state) {
            is TextToSpeechState.Play -> {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                }
                textToSpeech.language = state.providerLanguage.locale()
                textToSpeech.speak(
                    fromHtml(state.message).toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    UUID.randomUUID().toString()
                )
            }
            TextToSpeechState.Stop -> {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                }
            }
        }
    }

    private fun observeSingleViewState(singleViewState: SingleViewState) {
        when (singleViewState) {
            SingleViewState.RestartApp -> {
                activity?.finish()
                startActivity(Intent(requireContext(), OneMillionBotActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            is SingleViewState.ShowLanguageSelection -> {
                MaterialDialog(requireContext()).show {
                    title(R.string.select_language)
                    cancelable(singleViewState.cancelable)
                    listItems(items = singleViewState.languagesLabels) { _, _, label ->
                        chatViewModel.onViewEvent(SelectedLanguage(label.toString()))
                    }
                }
            }
            is SingleViewState.OpenBrowser -> {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(singleViewState.url)
                })
            }
            is SingleViewState.OpenCardCollectionScreen -> {
                findNavController().navigate(
                    NavGraphDirections.actionCardCollectionFragment(
                        singleViewState.message
                    )
                )
            }
            SingleViewState.ScrollToBottom -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.rvMessages.scrollToPosition(0)
                }, 100)
                Handler(Looper.getMainLooper())
                    .postDelayed({ binding.fab.visibility(isVisible = false) }, 200)
            }
            is SingleViewState.ShowImageFullScreen -> {
                StfalconImageViewer.Builder(context, listOf(singleViewState.url)) { view, image ->
                    Glide.with(this)
                        .load(image)
                        .into(view)
                }.show()
            }
            is SingleViewState.ShowError -> {
                Toast.makeText(requireContext(), singleViewState.stringRes, Toast.LENGTH_SHORT).show()
            }
            is SingleViewState.ConversationWasAttended -> {
                (activity as? OneMillionBotActivity)?.apply {
                    setNameBot(singleViewState.attended.name)
                    singleViewState.attended.url?.let { setAvatarBot(it) }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE
            && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            SpeechRecognizerDelegate().startSpeechToText(
                binding = binding,
                context = requireActivity(),
                chatViewModel = chatViewModel,
                providerLanguage = providerLanguage
            )
        }
    }

    fun changeLanguage() {
        chatViewModel.onViewEvent(ShowSelectLanguage(cancelable = true))
    }

    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 1
    }
}

fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

private fun fromHtml(html: String?) = when {
    html == null -> SpannableString("")
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    else -> Html.fromHtml(html)
}

