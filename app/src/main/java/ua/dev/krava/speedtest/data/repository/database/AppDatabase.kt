package ua.dev.krava.speedtest.data.repository.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.data.model.TestEntity
import ua.dev.krava.speedtest.data.repository.database.dao.ServersDao
import ua.dev.krava.speedtest.data.repository.database.dao.TestHistoryDao

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@Database(entities = [(TestEntity::class), (ServerEntity::class)], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun history(): TestHistoryDao

    abstract fun servers(): ServersDao

}