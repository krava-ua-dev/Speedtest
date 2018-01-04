package ua.dev.krava.speedtest.data.repository.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import ua.dev.krava.speedtest.data.model.TestEntity

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
@Dao
interface TestHistoryDao {

    @Query("SELECT * FROM history order by date DESC")
    fun getAll(): List<TestEntity>

    @Insert
    fun insert(speedtest: TestEntity)
}