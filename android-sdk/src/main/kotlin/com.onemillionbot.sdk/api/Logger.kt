package com.onemillionbot.sdk.api

/**
 * Provide an output for non-fatal errors.
 */
interface Logger {
    fun log(e: Throwable)
}
