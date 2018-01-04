package ua.dev.krava.speedtest.domain.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class ValueSettingsImpl(context: Context): ValueSettings {
    private val prefs: SharedPreferences = context.getSharedPreferences("krava.speedtest", Context.MODE_PRIVATE)
    private val ONE_WEEK = 1000 * 60 * 60 * 24 * 7
    private val HALF_HOUR = 1000 * 60 * 30



    override fun needLoadServers(): Boolean {
        val lastDate = prefs.getLong("servers_loaded_at", -1L)
        return lastDate == -1L || System.currentTimeMillis() - lastDate > HALF_HOUR
    }

    override fun serversLoadedAt(date: Long) {
        prefs.edit().putLong("servers_loaded_at", date).apply()
    }
}