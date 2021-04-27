package com.onemillionbot.sdk.core

import java.util.TimeZone

class GetTimeZoneUseCase {
    operator fun invoke(): String = TimeZone.getDefault().displayName
}
