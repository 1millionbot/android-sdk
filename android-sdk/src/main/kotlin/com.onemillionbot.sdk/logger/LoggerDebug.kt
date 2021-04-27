package com.onemillionbot.sdk.logger

import android.util.Log
import com.onemillionbot.sdk.BuildConfig
import com.onemillionbot.sdk.api.Logger

/**
 * Default implementation for non-fatal errors.
 */
class LoggerDebug : Logger {
    override fun log(e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("error", e.message, e)
        }
    }
}
