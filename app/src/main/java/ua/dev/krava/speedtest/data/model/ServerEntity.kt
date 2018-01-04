package ua.dev.krava.speedtest.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@Entity(tableName = "servers")
class ServerEntity {
    var url: String = ""
    var lat: Double = 0.0
    var lon: Double = 0.0
    var name: String = ""
    var country: String = ""
    var cc: String = ""
    var sponsor: String = ""
    @PrimaryKey
    var id: Int = 0
    var url2: String = ""
    var host: String = ""
}