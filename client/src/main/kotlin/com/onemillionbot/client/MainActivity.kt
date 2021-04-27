package com.onemillionbot.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onemillionbot.sdk.api.OneMillionBotView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<OneMillionBotView>(R.id.btShowOneMillionBot)
            .bind(this)
    }
}
