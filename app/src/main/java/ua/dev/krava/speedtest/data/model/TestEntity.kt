package ua.dev.krava.speedtest.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@Entity(tableName = "history")
class TestEntity {
    var host: String = ""

    var ping: Int = 1

    @ColumnInfo(name = "download_speed")
    var downloadSpeed: Float = 0.0f

    @ColumnInfo(name = "upload_speed")
    var uploadSpeed: Float = 0.0f

    @PrimaryKey
    var date: Long = 0L

    var provider: String = ""

    @ColumnInfo(name = "network_type")
    var networkType: String = ""


    fun toEntry(): TestEntry {
        val entry = TestEntry()
        entry.host = host
        entry.ping = ping
        entry.downloadSpeed = downloadSpeed
        entry.uploadSpeed = uploadSpeed
        entry.date = date
        entry.provider = provider
        entry.networkType = networkType
        return entry
    }
}