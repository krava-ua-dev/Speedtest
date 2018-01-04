package ua.dev.krava.speedtest.domain.repository

import io.reactivex.Observable
import ua.dev.krava.speedtest.data.model.ServerEntity
import ua.dev.krava.speedtest.presentation.model.TestEntry

/**
 * Created by evheniikravchyna on 03.01.2018.
 */
interface IDataRepository {

    fun saveTest(test: TestEntry)

    fun loadServers(): Observable<Boolean>

    fun saveServers(servers: List<ServerEntity>)

    fun loadTestHistory(): List<TestEntry>

}