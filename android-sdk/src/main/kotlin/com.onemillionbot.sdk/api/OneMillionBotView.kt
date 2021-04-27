package com.onemillionbot.sdk.api

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.onemillionbot.sdk.animations.AnimationHandler
import com.onemillionbot.sdk.animations.bounceIn
import com.onemillionbot.sdk.animations.fadeInDown
import com.onemillionbot.sdk.animations.fadeInRight
import com.onemillionbot.sdk.animations.fadeOutRight
import com.onemillionbot.sdk.databinding.OneMillionBotViewBinding
import com.onemillionbot.sdk.presentation.home.OneMillionBotActivity
import com.onemillionbot.sdk.repository.ConfigBotRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


class OneMillionBotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val koin = OneMillionBot.koinApp.koin
    private val logger by koin.inject<Logger>()
    private val handler = CoroutineExceptionHandler { _, e: Throwable -> logger.log(e) }
    private val binding = OneMillionBotViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun bind(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch(handler) {
            val botConfig = koin.get<ConfigBotRepository>().getBot()
            setupUrlAvatar(botConfig.urlAvatar)

            binding.tvInitialMessage.text = botConfig.callToAction

            listOf(binding.bubble, binding.ivAvatar).forEach { view ->
                view.setOnClickListener {
                    context.startActivity(Intent(context, OneMillionBotActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.bubble.isVisible = false
                    }, 1000)
                }
            }

            binding.ivClose.setOnClickListener {
                AnimationHandler(fadeOutRight)
                    .duration(500)
                    .onEnd {
                        binding.container.isVisible = false
                    }
                    .playOn(binding.container)
            }
        }
    }

    private fun setupUrlAvatar(urlAvatar: String) {
        Glide.with(context)
            .load(urlAvatar)
            .circleCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    showAvatar()
                    return false
                }
            })
            .into(binding.ivAvatar)
    }

    private fun showAvatar() {
        binding.ivAvatar.isVisible = true
        AnimationHandler(bounceIn)
            .onEnd {
                binding.bubble.isVisible = true
                AnimationHandler(fadeInRight)
                    .duration(500)
                    .playOn(binding.bubble)

                binding.ivClose.isVisible = true
                AnimationHandler(fadeInDown)
                    .duration(500)
                    .playOn(binding.ivClose)
            }
            .playOn(binding.ivAvatar)
    }
}
