package com.onemillionbot.sdk.core

import android.content.Context
import android.net.wifi.WifiManager


class NetworkUtils(private val context: Context) {
    fun getIp(): String {
        val wifiMan = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        val ipAddress = wifiInf.ipAddress
        return String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff, ipAddress shr 24 and 0xff
        )
    }
}
