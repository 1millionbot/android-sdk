package androidx.appcompat.app

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import com.onemillionbot.sdk.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import java.util.Locale

class OneMillionBotContextWrapper(context: Context) : ContextWrapper(context) {

    companion object {
        fun getOverriddenConfigIfPossible(
            context: Context,
            configurationRepository: SessionRepository
        ): Configuration {
            val language = runBlocking { configurationRepository.getSelectedLanguage() }
            val config: Configuration = context.resources.configuration
            val locale = Locale(language.androidLanguageCode, language.androidLanguageCode)
            Locale.setDefault(locale)
            config.setLayoutDirection(locale)
            config.setLocale(locale)
            return config
        }
    }
}
