package ua.dev.krava.speedtest.presentation.features.speedtest

import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class TestState {
    lateinit var server: ServerEntity
    var ping: Int = 1
    var downloadSpeed: Float = 0.0f
    var uploadSpeed: Float = 0.0f
    var date: Long = 0L
    var provider: String = "Lifecell :)"
    var networkType: String = "3g"

    fun createEntry(): TestEntry {
        val entity = TestEntry()
        entity.date = date
        entity.host = server.host
        entity.provider = provider
        entity.networkType = networkType
        entity.downloadSpeed = downloadSpeed
        entity.uploadSpeed = uploadSpeed
        entity.ping = ping
        return entity
    }
}