package ua.dev.krava.speedtest.presentation.model

import ua.dev.krava.speedtest.data.model.TestEntity

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
class TestEntry {
    var host: String = ""
    var ping: Int = 0
    var downloadSpeed: Float = 0.0f
    var uploadSpeed: Float = 0.0f
    var date: Long = 0L
    var provider: String = ""
    var networkType: String = ""

    override fun equals(other: Any?): Boolean {
        return other != null && other is TestEntry && other.hashCode() == this.hashCode()
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + ping
        result = 31 * result + downloadSpeed.hashCode()
        result = 31 * result + uploadSpeed.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + provider.hashCode()
        result = 31 * result + networkType.hashCode()
        return result
    }

    fun toEntity(): TestEntity {
        val entity = TestEntity()
        entity.host = host
        entity.ping = ping
        entity.downloadSpeed = downloadSpeed
        entity.uploadSpeed = uploadSpeed
        entity.date = date
        entity.provider = provider
        entity.networkType = networkType
        return entity
    }
}