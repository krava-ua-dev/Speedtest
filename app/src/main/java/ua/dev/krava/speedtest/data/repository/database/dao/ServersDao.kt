package ua.dev.krava.speedtest.data.repository.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import ua.dev.krava.speedtest.data.model.ServerEntity

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@Dao
interface ServersDao {

    @Query("SELECT * FROM servers")
    fun getAll(): List<ServerEntity>

    @Query("SELECT * FROM servers WHERE cc = :countryCode")
    fun findByCountryCode(countryCode: String): List<ServerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(servers: List<ServerEntity>)
}