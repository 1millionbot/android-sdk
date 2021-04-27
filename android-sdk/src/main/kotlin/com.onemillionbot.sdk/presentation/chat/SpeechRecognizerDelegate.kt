package com.onemillionbot.sdk.presentation.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.view.isVisible
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.core.SimpleRecognitionListener
import com.onemillionbot.sdk.databinding.ChatFragmentBinding
import com.onemillionbot.sdk.entities.ProviderLanguage
import java.util.Timer
import java.util.TimerTask

class SpeechRecognizerDelegate {
    fun startSpeechToText(
        providerLanguage: ProviderLanguage,
        chatViewModel: ChatViewModel,
        context: Activity,
        binding: ChatFragmentBinding
    ) {
        binding.groupSpeech.isVisible = true
        binding.etNewMessage.hint = ""
        binding.tvRecordingCounter.text = "00:00"
        binding.etNewMessage.isFocusable = false

        var userClickSendButton = false
        val timer = Timer()
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        fun turnOffSpeech() {
            timer.cancel()
            speechRecognizer.destroy()
            binding.groupSpeech.isVisible = false
            binding.etNewMessage.hint = context.getString(R.string.ask_question)
            binding.etNewMessage.isFocusable = true
            binding.etNewMessage.isFocusableInTouchMode = true
        }

        fun startListening() {
            speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                //These flags would be ideal for our use case but it does not work anymore
                //https://stackoverflow.com/a/28628826/1525990
                //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100000)
                //putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100000)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    providerLanguage.extraLanguage()
                )
            })
        }

        var textSpeechToSend = ""
        var isSpeechComputing = true
        var didUserStartToSpeak = false

        speechRecognizer.setRecognitionListener(object : SimpleRecognitionListener() {
            override fun onBeginningOfSpeech() {
                isSpeechComputing = true
                didUserStartToSpeak = true
            }

            override fun onEndOfSpeech() {
                isSpeechComputing = false
            }

            override fun onError(error: Int) {
                isSpeechComputing = false
                startListening()
            }

            override fun onResults(results: Bundle) {
                val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!!
                textSpeechToSend += if (textSpeechToSend.isNotEmpty()) {
                    " ${data[0]}"
                } else {
                    data[0]
                }

                if (userClickSendButton) {
                    turnOffSpeech()
                    chatViewModel.onViewEvent(ChatViewEvent.NewMessageUser(textSpeechToSend, speechToText = true))
                } else {
                    startListening()
                }

                isSpeechComputing = false
            }
        })

        startListening()

        val interval = 1000L
        var elapsedTime = 0

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                context.runOnUiThread {
                    elapsedTime += 1
                    binding.tvRecordingCounter.text =
                        String.format("%02d:%02d", elapsedTime / 60 % 60, elapsedTime % 60)
                }
            }
        }, 1000, interval)

        binding.ivBin.setOnClickListener { turnOffSpeech() }
        binding.btSendSpeech.setOnClickListener {
            timer.cancel()
            userClickSendButton = true

            if (!isSpeechComputing || !didUserStartToSpeak) {
                turnOffSpeech()
                chatViewModel.onViewEvent(ChatViewEvent.NewMessageUser(textSpeechToSend, speechToText = true))
            }
        }
    }

}
