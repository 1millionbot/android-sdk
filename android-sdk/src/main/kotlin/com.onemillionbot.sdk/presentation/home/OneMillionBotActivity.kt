package com.onemillionbot.sdk.presentation.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.BaseContextWrapperActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.bumptech.glide.Glide
import com.onemillionbot.sdk.BuildConfig
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.api.OneMillionBot.SdkKoinComponent
import com.onemillionbot.sdk.entities.BotConfig
import com.onemillionbot.sdk.presentation.chat.ChatFragment
import com.onemillionbot.sdk.presentation.home.SingleViewState.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class OneMillionBotActivity : BaseContextWrapperActivity(R.layout.home_activity), SdkKoinComponent {
    private val viewModel: OneMillionBotViewModel by viewModel()
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private var botConfig: BotConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpToolbar()

        findViewById<View>(R.id.btClose).setOnClickListener { moveTaskToBack(true) }
        if (BuildConfig.DEBUG) {
            findViewById<View>(R.id.ivAvatar).setOnClickListener {
                viewModel.onViewEvent(ViewEvent.CopyToClipboard)
            }
        }

        viewModel.singleViewState.observe(this, { state ->
            when (state) {
                is InitNavigation -> initNavGraph(state)
                is CloseBot -> finish()
                is CopyToClipboard -> {
                    Toast.makeText(this, "Debug info copied to clipboard ${state.debugInfo}", Toast.LENGTH_LONG).show()
                    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
                    val clip = ClipData.newPlainText("label", state.debugInfo)
                    clipboard.setPrimaryClip(clip)
                }
            }
        })

        viewModel.viewState.observe(this, { state ->
            setAvatarBot(state.botConfig.urlAvatar)
            setNameBot(state.botConfig.name)
            toolbar.setBackgroundColor(Color.parseColor(state.botConfig.colorHex))
            botConfig = state.botConfig
        })
    }

    fun setNameBot(name: String) {
        findViewById<TextView>(R.id.tvAvatar)?.text = name
    }

    fun setAvatarBot(url: String) {
        findViewById<ImageView>(R.id.ivAvatar)?.let {
            Glide.with(this)
                .load(url)
                .circleCrop()
                .into(it)
        }
    }

    fun showMenuOptions() {
        toolbar.inflateMenu(R.menu.main_menu)
    }

    private fun setUpToolbar() {
        toolbar.apply {
            overflowIcon?.setTint(ContextCompat.getColor(this@OneMillionBotActivity, R.color.white))

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.legalTerms -> {
                        botConfig?.gdprUrl?.let {
                            startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(it)
                            })
                        }
                    }
                    R.id.forgetMe -> forgetMe()
                    R.id.selectLanguage -> {
                        (findNavHostFragment().childFragmentManager
                            .fragments.firstOrNull() as? ChatFragment)?.changeLanguage()
                    }
                }
                false
            }
        }
    }

    private fun initNavGraph(state: InitNavigation) {
        val navHostFragment = findNavHostFragment()
        navHostFragment.navController.graph =
            navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph_sdk).apply {
                startDestination = state.destination
            }
    }

    private fun forgetMe() {
        MaterialDialog(this).show {
            title(R.string.clear_data)
            message(R.string.forget_me_warning)
            positiveButton(R.string.agree) { viewModel.onViewEvent(ViewEvent.CloseSession) }
            negativeButton(R.string.cancel)
            botConfig?.let {
                getActionButton(WhichButton.POSITIVE).updateTextColor(Color.parseColor(it.colorHex))
                getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor(it.colorHex))
            }
        }
    }

    private fun findNavHostFragment() =
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
}
