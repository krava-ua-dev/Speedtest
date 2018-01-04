package ua.dev.krava.speedtest.domain.preferences

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
interface ValueSettings {

    fun needLoadServers(): Boolean

    fun serversLoadedAt(date: Long)
}