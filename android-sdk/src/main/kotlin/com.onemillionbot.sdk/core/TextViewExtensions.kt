package com.onemillionbot.sdk.core

import android.os.Build
import android.text.Html
import android.widget.TextView

var TextView.html: String?
    get() = (text as? String).orEmpty()
    set(value) {
        if (value != null) {
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(value)
            }
        }
    }
